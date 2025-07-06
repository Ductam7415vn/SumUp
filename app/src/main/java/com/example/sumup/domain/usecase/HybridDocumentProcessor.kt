package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.sumup.domain.model.DocumentType
import com.example.sumup.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Hybrid document processor that handles both DOC and DOCX files
 * - DOCX: Uses local Mammoth library (works offline)
 * - DOC: Uses server-side processing (requires internet)
 */
class HybridDocumentProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkUtils: NetworkUtils,
    private val serverDocProcessor: ServerDocProcessor
) : DocumentProcessor {
    
    private val localDocxProcessor = EnhancedDocxProcessor(context)
    private val docxDocumentProcessor = DocxDocumentProcessor(context)
    
    override suspend fun extractText(uri: String): String = withContext(Dispatchers.IO) {
        android.util.Log.d("HybridDocProcessor", "extractText called with uri: $uri")
        
        // Try to determine file type from URI or content
        val documentType = determineDocumentType(uri)
        android.util.Log.d("HybridDocProcessor", "Determined document type: $documentType")
        
        when (documentType) {
            DocumentType.DOCX -> {
                android.util.Log.d("HybridDocProcessor", "Processing as DOCX")
                // Use local processing for DOCX
                try {
                    docxDocumentProcessor.extractText(uri)
                } catch (e: Exception) {
                    android.util.Log.w("HybridDocProcessor", "DocxDocumentProcessor failed, trying enhanced processor", e)
                    // Fallback to enhanced processor if needed
                    localDocxProcessor.extractText(uri)
                }
            }
            DocumentType.DOC -> {
                android.util.Log.d("HybridDocProcessor", "Processing as DOC")
                // Check network availability first
                if (!networkUtils.isNetworkAvailable()) {
                    throw Exception(
                        "DOC files require internet connection for processing. " +
                        "Please connect to the internet or convert your file to DOCX format."
                    )
                }
                
                // Use server-side processing for DOC
                serverDocProcessor.extractText(uri)
            }
            else -> {
                android.util.Log.e("HybridDocProcessor", "Unsupported document type: $documentType for uri: $uri")
                throw Exception("Unsupported document format. Only DOC and DOCX files are supported.")
            }
        }
    }
    
    override suspend fun getPageCount(uri: String): Int? = withContext(Dispatchers.IO) {
        val documentType = determineDocumentType(uri)
        
        when (documentType) {
            DocumentType.DOCX -> {
                try {
                    docxDocumentProcessor.getPageCount(uri)
                } catch (e: Exception) {
                    localDocxProcessor.getPageCount(uri)
                }
            }
            DocumentType.DOC -> {
                if (networkUtils.isNetworkAvailable()) {
                    serverDocProcessor.getPageCount(uri)
                } else {
                    null // Cannot determine page count offline
                }
            }
            else -> null
        }
    }
    
    override suspend fun validateDocument(uri: String): Boolean = withContext(Dispatchers.IO) {
        android.util.Log.d("HybridDocProcessor", "validateDocument called with uri: $uri")
        val documentType = determineDocumentType(uri)
        android.util.Log.d("HybridDocProcessor", "Document type for validation: $documentType")
        
        when (documentType) {
            DocumentType.DOCX -> {
                try {
                    val result = docxDocumentProcessor.validateDocument(uri)
                    android.util.Log.d("HybridDocProcessor", "DocxDocumentProcessor validation result: $result")
                    result
                } catch (e: Exception) {
                    android.util.Log.w("HybridDocProcessor", "DocxDocumentProcessor validation failed, trying enhanced", e)
                    val result = localDocxProcessor.validateDocument(uri)
                    android.util.Log.d("HybridDocProcessor", "EnhancedDocxProcessor validation result: $result")
                    result
                }
            }
            DocumentType.DOC -> {
                // Basic validation - check file header
                try {
                    context.contentResolver.openInputStream(Uri.parse(uri))?.use { inputStream ->
                        val buffer = ByteArray(8)
                        val bytesRead = inputStream.read(buffer)
                        
                        if (bytesRead >= 8) {
                            // Check for Microsoft Office header
                            val isValid = buffer[0] == 0xD0.toByte() && 
                                         buffer[1] == 0xCF.toByte() &&
                                         buffer[2] == 0x11.toByte() &&
                                         buffer[3] == 0xE0.toByte()
                            android.util.Log.d("HybridDocProcessor", "DOC validation result: $isValid")
                            isValid
                        } else {
                            android.util.Log.d("HybridDocProcessor", "DOC file too small for validation")
                            false
                        }
                    } ?: false
                } catch (e: Exception) {
                    android.util.Log.e("HybridDocProcessor", "DOC validation error", e)
                    false
                }
            }
            else -> {
                android.util.Log.d("HybridDocProcessor", "Unknown document type, validation failed")
                false
            }
        }
    }
    
    private fun determineDocumentType(uri: String): DocumentType? {
        android.util.Log.d("HybridDocProcessor", "determineDocumentType called with uri: $uri")
        
        // First try to determine from URI extension
        when {
            uri.endsWith(".docx", ignoreCase = true) -> {
                android.util.Log.d("HybridDocProcessor", "URI ends with .docx")
                return DocumentType.DOCX
            }
            uri.endsWith(".doc", ignoreCase = true) -> {
                android.util.Log.d("HybridDocProcessor", "URI ends with .doc")
                return DocumentType.DOC
            }
        }
        
        // If URI doesn't have extension, try to determine from content
        return try {
            val androidUri = Uri.parse(uri)
            val mimeType = context.contentResolver.getType(androidUri)
            android.util.Log.d("HybridDocProcessor", "MIME type from content resolver: $mimeType")
            
            when (mimeType) {
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                    android.util.Log.d("HybridDocProcessor", "MIME type indicates DOCX")
                    DocumentType.DOCX
                }
                "application/msword" -> {
                    android.util.Log.d("HybridDocProcessor", "MIME type indicates DOC")
                    DocumentType.DOC
                }
                else -> {
                    android.util.Log.d("HybridDocProcessor", "Unknown MIME type, checking filename")
                    // Try to get filename from content resolver
                    context.contentResolver.query(androidUri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            val fileName = cursor.getString(nameIndex) ?: ""
                            android.util.Log.d("HybridDocProcessor", "Filename from content resolver: $fileName")
                            
                            when {
                                fileName.endsWith(".docx", ignoreCase = true) -> {
                                    android.util.Log.d("HybridDocProcessor", "Filename indicates DOCX")
                                    DocumentType.DOCX
                                }
                                fileName.endsWith(".doc", ignoreCase = true) -> {
                                    android.util.Log.d("HybridDocProcessor", "Filename indicates DOC")
                                    DocumentType.DOC
                                }
                                else -> {
                                    android.util.Log.d("HybridDocProcessor", "Cannot determine type from filename")
                                    null
                                }
                            }
                        } else {
                            android.util.Log.d("HybridDocProcessor", "Cursor is empty")
                            null
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("HybridDocProcessor", "Error determining document type", e)
            null
        }
    }
    
    /**
     * Process document with progress tracking
     */
    fun processDocumentWithProgress(uri: String): Flow<ProcessingProgress> = flow {
        val documentType = determineDocumentType(uri)
        
        when (documentType) {
            DocumentType.DOCX -> {
                emit(ProcessingProgress.Processing(0f, "Opening DOCX file..."))
                
                try {
                    val validation = localDocxProcessor.validateDocxComprehensive(uri)
                    
                    if (!validation.isValid) {
                        emit(ProcessingProgress.Failed("Invalid DOCX file"))
                        return@flow
                    }
                    
                    emit(ProcessingProgress.Processing(0.2f, "Extracting text..."))
                    
                    if (validation.mammothCompatibility < 0.5f) {
                        emit(ProcessingProgress.Warning(
                            "This document may not be fully compatible. " +
                            "Some content might be missing."
                        ))
                    }
                    
                    val text = localDocxProcessor.extractText(uri)
                    emit(ProcessingProgress.Processing(0.8f, "Finalizing..."))
                    
                    val pageCount = localDocxProcessor.getPageCount(uri)
                    
                    emit(ProcessingProgress.Completed(
                        text = text,
                        pageCount = pageCount,
                        warnings = if (validation.hasImages || validation.hasTables) {
                            listOf("Images and tables are not included in the text extraction")
                        } else null
                    ))
                } catch (e: Exception) {
                    emit(ProcessingProgress.Failed(e.message ?: "Failed to process DOCX"))
                }
            }
            
            DocumentType.DOC -> {
                if (!networkUtils.isNetworkAvailable()) {
                    emit(ProcessingProgress.Failed(
                        "Internet connection required for DOC files. " +
                        "Please check your connection or convert to DOCX."
                    ))
                    return@flow
                }
                
                emit(ProcessingProgress.Processing(0f, "Uploading DOC file..."))
                
                try {
                    // Option 1: Direct text extraction
                    emit(ProcessingProgress.Processing(0.3f, "Processing on server..."))
                    val text = serverDocProcessor.extractText(uri)
                    
                    emit(ProcessingProgress.Processing(0.9f, "Finalizing..."))
                    
                    emit(ProcessingProgress.Completed(
                        text = text,
                        pageCount = null,
                        warnings = listOf("Processed via server")
                    ))
                    
                    // Option 2: Convert to DOCX first (commented out)
                    /*
                    serverDocProcessor.convertDocToDocxWithProgress(uri).collect { conversion ->
                        when (conversion) {
                            is ServerDocProcessor.ConversionProgress.Processing -> {
                                emit(ProcessingProgress.Processing(
                                    0.3f + conversion.progress * 0.6f,
                                    conversion.message
                                ))
                            }
                            is ServerDocProcessor.ConversionProgress.Completed -> {
                                emit(ProcessingProgress.Processing(0.9f, "Processing converted file..."))
                                val text = localDocxProcessor.extractText(conversion.file.absolutePath)
                                emit(ProcessingProgress.Completed(text = text))
                            }
                            is ServerDocProcessor.ConversionProgress.Failed -> {
                                emit(ProcessingProgress.Failed(conversion.error))
                            }
                            else -> {}
                        }
                    }
                    */
                } catch (e: Exception) {
                    emit(ProcessingProgress.Failed(e.message ?: "Server processing failed"))
                }
            }
            
            else -> {
                emit(ProcessingProgress.Failed("Unsupported file format. Only DOC and DOCX files are supported."))
            }
        }
    }
    
    sealed class ProcessingProgress {
        data class Processing(val progress: Float, val message: String) : ProcessingProgress()
        data class Warning(val message: String) : ProcessingProgress()
        data class Completed(
            val text: String,
            val pageCount: Int? = null,
            val warnings: List<String>? = null
        ) : ProcessingProgress()
        data class Failed(val error: String) : ProcessingProgress()
    }
}