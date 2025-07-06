package com.example.sumup.domain.usecase

import android.content.Context
import com.example.sumup.domain.model.DocumentType
import com.example.sumup.domain.repository.DocumentRepository
import com.example.sumup.domain.repository.PdfRepository
import com.example.sumup.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Provider

/**
 * Factory to get the appropriate document processor based on document type
 */
@Singleton
class DocumentProcessorFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfRepository: PdfRepository,
    private val hybridDocumentProcessorProvider: Provider<HybridDocumentProcessor>
) {
    fun getProcessor(documentType: DocumentType): DocumentProcessor {
        return when (documentType) {
            DocumentType.PDF -> PdfDocumentProcessor(pdfRepository)
            DocumentType.DOC, DocumentType.DOCX -> hybridDocumentProcessorProvider.get()
            DocumentType.TXT -> TxtDocumentProcessor(context)
            DocumentType.RTF -> RtfDocumentProcessor(context)
        }
    }
    
    fun getSupportedMimeTypes(): List<String> {
        return listOf(
            "application/pdf",
            "application/msword", // DOC
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
            "text/plain", // TXT
            "application/rtf", // RTF
            "text/rtf" // RTF alternative
        )
    }
    
    fun getDocumentTypeFromMimeType(mimeType: String): DocumentType? {
        return when (mimeType) {
            "application/pdf" -> DocumentType.PDF
            "application/msword" -> DocumentType.DOC
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> DocumentType.DOCX
            "text/plain" -> DocumentType.TXT
            "application/rtf", "text/rtf" -> DocumentType.RTF
            else -> null
        }
    }
}

interface DocumentProcessor {
    suspend fun extractText(uri: String): String
    suspend fun getPageCount(uri: String): Int?
    suspend fun validateDocument(uri: String): Boolean
}