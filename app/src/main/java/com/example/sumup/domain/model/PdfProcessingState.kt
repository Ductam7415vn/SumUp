package com.example.sumup.domain.model

/**
 * State of PDF processing
 */
sealed class PdfProcessingState {
    object Idle : PdfProcessingState()
    object Loading : PdfProcessingState()
    data class Success(val result: PdfExtractionResult) : PdfProcessingState()
    data class Error(val message: String) : PdfProcessingState()
}