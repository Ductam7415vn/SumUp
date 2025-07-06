package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.sumup.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for processing any supported document type
 */
class ProcessDocumentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentProcessorFactory: DocumentProcessorFactory
) {
    suspend fun processDocument(
        uri: String,
        documentType: DocumentType,
        pageRange: IntRange? = null
    ): Flow<DocumentExtractionResult> = flow {
        try {
            val processor = documentProcessorFactory.getProcessor(documentType)
            
            // Validate document first
            if (!processor.validateDocument(uri)) {
                val errorMessage = when (documentType) {
                    DocumentType.DOCX -> "Invalid DOCX file. Please ensure the file is not corrupted and is a valid Word document."
                    DocumentType.DOC -> "DOC format is not supported. Please convert to DOCX, PDF, or TXT format."
                    DocumentType.PDF -> "Invalid PDF file. Please ensure the file is not corrupted."
                    else -> "Invalid ${documentType.name} file"
                }
                emit(DocumentExtractionResult(
                    extractedText = "",
                    pageCount = null,
                    success = false,
                    errorMessage = errorMessage
                ))
                return@flow
            }
            
            // Extract text
            val extractedText = processor.extractText(uri)
            val pageCount = processor.getPageCount(uri)
            
            // Apply page range if specified (only for documents with pages)
            val finalText = if (pageRange != null && documentType == DocumentType.PDF) {
                // For PDF, we can use existing page range functionality
                // For other formats, we'd need to implement page-based extraction
                extractedText
            } else {
                extractedText
            }
            
            emit(DocumentExtractionResult(
                extractedText = finalText,
                pageCount = pageCount,
                success = true,
                confidence = 1.0f
            ))
        } catch (e: Exception) {
            emit(DocumentExtractionResult(
                extractedText = "",
                pageCount = null,
                success = false,
                errorMessage = e.message ?: "Failed to process document"
            ))
        }
    }
    
    fun getDocumentInfo(uri: String): Document? {
        return try {
            val documentUri = Uri.parse(uri)
            context.contentResolver.query(documentUri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    
                    val fileName = cursor.getString(nameIndex) ?: "document"
                    val sizeBytes = cursor.getLong(sizeIndex)
                    
                    val mimeType = context.contentResolver.getType(documentUri)
                    val documentType = mimeType?.let { 
                        documentProcessorFactory.getDocumentTypeFromMimeType(it)
                    } ?: guessDocumentTypeFromFileName(fileName)
                    
                    Document(
                        uri = uri,
                        fileName = fileName,
                        sizeBytes = sizeBytes,
                        type = documentType ?: DocumentType.TXT
                    )
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun guessDocumentTypeFromFileName(fileName: String): DocumentType? {
        return when {
            fileName.endsWith(".pdf", ignoreCase = true) -> DocumentType.PDF
            fileName.endsWith(".docx", ignoreCase = true) -> DocumentType.DOCX
            fileName.endsWith(".doc", ignoreCase = true) -> DocumentType.DOC
            fileName.endsWith(".txt", ignoreCase = true) -> DocumentType.TXT
            fileName.endsWith(".rtf", ignoreCase = true) -> DocumentType.RTF
            else -> null
        }
    }
}