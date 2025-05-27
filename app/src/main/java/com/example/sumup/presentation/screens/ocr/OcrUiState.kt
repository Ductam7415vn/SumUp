package com.example.sumup.presentation.screens.ocr

import com.example.sumup.domain.model.AppError
import com.example.sumup.domain.model.OcrState

data class OcrUiState(
    val isPermissionGranted: Boolean = false,
    val isProcessing: Boolean = false,
    val detectedText: String = "",
    val error: AppError? = null,
    val showPermissionRationale: Boolean = false,
    val ocrState: OcrState = OcrState.Searching,
    val confidence: Float = 0f
)