package com.example.sumup.presentation.screens.main

import com.example.sumup.domain.model.AppError
import com.example.sumup.domain.model.FileUploadState
import com.example.sumup.domain.model.Summary

data class MainUiState(
    val inputText: String = "",
    val selectedPdfUri: String? = null,
    val selectedPdfName: String? = null,
    val inputType: InputType = InputType.TEXT,
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val summary: Summary? = null,
    val canSummarize: Boolean = false,
    val showProcessingScreen: Boolean = false,
    val processingProgress: Float = 0f,
    val processingMessage: String = "",
    val showClearDialog: Boolean = false,
    val showInfoDialog: Boolean = false,
    val navigateToProcessing: Boolean = false,
    // PDF upload state
    val fileUploadState: FileUploadState? = null,
    // Draft recovery
    val showDraftRecoveryDialog: Boolean = false,
    val recoverableDraftText: String = "",
    val autoSaveEnabled: Boolean = true
) {
    enum class InputType {
        TEXT, PDF, OCR
    }
    
    val characterCount: Int get() = inputText.length
    val isTextInput: Boolean get() = inputType == InputType.TEXT
    val isPdfInput: Boolean get() = inputType == InputType.PDF
    val isInputValid: Boolean get() = when (inputType) {
        InputType.TEXT -> inputText.trim().length >= 50
        InputType.PDF -> selectedPdfUri != null
        InputType.OCR -> inputText.trim().length >= 50
    }
    val isInputOverLimit: Boolean get() = inputText.length > 5000
    val hasContent: Boolean get() = when (inputType) {
        InputType.TEXT -> inputText.isNotEmpty()
        InputType.PDF -> selectedPdfUri != null
        InputType.OCR -> inputText.isNotEmpty()
    }
}