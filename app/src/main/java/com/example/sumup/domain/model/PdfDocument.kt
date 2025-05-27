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
    val processingState: PdfProcessingState = PdfProcessingState.PENDING
)

enum class PdfProcessingState {
    PENDING,
    EXTRACTING,
    EXTRACTED,
    FAILED
}

/**
 * PDF extraction result with metadata
 */
data class PdfExtractionResult(
    val text: String,
    val wordCount: Int,
    val pageCount: Int,
    val hasImages: Boolean,
    val hasTables: Boolean,
    val extractionTimeMs: Long,
    val confidence: Float = 0.8f // How confident we are in extraction quality
) {
    val isTextExtractable: Boolean
        get() = text.trim().length >= 50 && confidence > 0.5f
        
    val estimatedReadingTime: Int
        get() = (wordCount / 200.0).toInt().coerceAtLeast(1)
}