package com.example.sumup.domain.model

/**
 * Represents PDF processing state and metadata
 */
data class PdfDocument(
    val uri: String,
    val fileName: String,
    val sizeBytes: Long,
    val pageCount: Int? = null,
    val isPasswordProtected: Boolean = false,
    val extractedText: String? = null,
    val processingState: PdfProcessingState = PdfProcessingState.Idle
)

// Moved to PdfDomainModels.kt to avoid duplication