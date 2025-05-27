package com.example.sumup.domain.model

/**
 * Represents the state of OCR processing
 */
sealed class OcrState {
    object Searching : OcrState()
    object Focusing : OcrState()
    object Ready : OcrState()
    object Processing : OcrState()
}