package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.repository.PdfRepository

/**
 * Adapter to make existing PdfRepository work with new DocumentProcessor interface
 */
class PdfDocumentProcessor(
    private val pdfRepository: PdfRepository
) : DocumentProcessor {
    
    override suspend fun extractText(uri: String): String {
        val pdfDoc = pdfRepository.validatePdfFile(uri)
        val result = pdfRepository.extractTextFromPdf(pdfDoc)
        if (!result.success) {
            throw Exception(result.errorMessage ?: "Failed to extract text from PDF")
        }
        return result.extractedText
    }
    
    override suspend fun getPageCount(uri: String): Int? {
        val metadata = pdfRepository.getPdfMetadata(uri)
        return metadata.pageCount
    }
    
    override suspend fun validateDocument(uri: String): Boolean {
        return try {
            pdfRepository.validatePdfFile(uri)
            true
        } catch (e: Exception) {
            false
        }
    }
}