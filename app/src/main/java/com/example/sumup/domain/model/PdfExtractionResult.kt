package com.example.sumup.domain.model

/**
 * Result of PDF text extraction
 */
data class PdfExtractionResult(
    val extractedText: String,
    val pageCount: Int,
    val success: Boolean,
    val errorMessage: String? = null,
    val confidence: Float = 0.0f,
    val hasTableStructure: Boolean = false
)