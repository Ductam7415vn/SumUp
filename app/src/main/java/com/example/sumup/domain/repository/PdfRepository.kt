package com.example.sumup.domain.repository

import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.model.PdfExtractionResult

interface PdfRepository {
    suspend fun extractTextFromPdf(pdfDocument: PdfDocument): PdfExtractionResult
    suspend fun validatePdfFile(uri: String): PdfDocument
    suspend fun getPdfMetadata(uri: String): PdfDocument
    suspend fun extractTextFromPdfRange(
        uri: String,
        startPage: Int,
        endPage: Int
    ): PdfExtractionResult
}