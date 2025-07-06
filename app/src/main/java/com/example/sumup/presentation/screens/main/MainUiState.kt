package com.example.sumup.presentation.screens.main

import com.example.sumup.domain.model.AppError
import com.example.sumup.domain.model.FileUploadState
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.ServiceInfo
import com.example.sumup.domain.model.Document
import com.example.sumup.domain.model.DocumentType
import com.example.sumup.domain.model.ProcessingOption
import com.example.sumup.domain.model.ProcessingStrategy

data class MainUiState(
    val inputText: String = "",
    // Document selection (supports PDF, DOC, DOCX, TXT, RTF)
    val selectedDocumentUri: String? = null,
    val selectedDocumentName: String? = null,
    val selectedDocument: Document? = null,
    // Legacy PDF fields (kept for compatibility)
    val selectedPdfUri: String? = null,
    val selectedPdfName: String? = null,
    val inputType: InputType = InputType.TEXT,
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val summary: Summary? = null,
    val summaryId: String? = null,
    val canSummarize: Boolean = false,
    val showProcessingScreen: Boolean = false,
    val processingProgress: Float = 0f,
    val processingMessage: String = "",
    val showClearDialog: Boolean = false,
    val showInfoDialog: Boolean = false,
    val navigateToProcessing: Boolean = false,
    val navigateToResult: Boolean = false,
    // PDF upload state
    val fileUploadState: FileUploadState? = null,
    // Draft recovery
    val showDraftRecoveryDialog: Boolean = false,
    val recoverableDraftText: String = "",
    val autoSaveEnabled: Boolean = true,
    // Stats
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val totalCount: Int = 0,
    // Summary length preference
    val summaryLength: SummaryLength = SummaryLength.STANDARD,
    // Large PDF warning
    val showLargePdfWarning: Boolean = false,
    val largePdfPageCount: Int = 0,
    val largePdfEstimatedTime: Long = 0L,
    // PDF preview
    val showPdfPreview: Boolean = false,
    val pdfPageCount: Int = 0,
    // DOCX preview
    val showDocxPreview: Boolean = false,
    // Service info
    val serviceInfo: ServiceInfo? = null,
    // Feature discovery
    val showFeatureDiscovery: Boolean = false,
    val currentFeatureTip: String? = null,
    // Enhanced Feature discovery
    val showEnhancedTooltips: Boolean = false,
    // API usage stats
    val apiUsageStats: com.example.sumup.domain.model.ApiUsageStats? = null,
    // Welcome card
    val showWelcomeCard: Boolean = false,
    // Processing method selection
    val showProcessingMethodDialog: Boolean = false,
    val processingOptions: List<ProcessingOption> = emptyList(),
    val selectedProcessingStrategy: ProcessingStrategy? = null,
    val pendingTextForProcessing: String = ""
) {
    enum class InputType {
        TEXT, DOCUMENT, OCR
    }
    
    enum class SummaryLength(val displayName: String, val multiplier: Float) {
        BRIEF("Brief (5%)", 0.05f),
        STANDARD("Standard (10%)", 0.10f),
        DETAILED("Detailed (20%)", 0.20f)
    }
    
    val characterCount: Int get() = inputText.length
    val isTextInput: Boolean get() = inputType == InputType.TEXT
    val isDocumentInput: Boolean get() = inputType == InputType.DOCUMENT
    val isPdfInput: Boolean get() = inputType == InputType.DOCUMENT && selectedDocument?.type == DocumentType.PDF
    val isDocxInput: Boolean get() = inputType == InputType.DOCUMENT && selectedDocument?.type == DocumentType.DOCX
    val isInputValid: Boolean get() = when (inputType) {
        InputType.TEXT -> inputText.trim().length >= 50
        InputType.DOCUMENT -> selectedDocumentUri != null
        InputType.OCR -> inputText.trim().length >= 50
    }
    val isInputOverLimit: Boolean get() = inputText.length > 5000
    val hasContent: Boolean get() = when (inputType) {
        InputType.TEXT -> inputText.isNotEmpty()
        InputType.DOCUMENT -> selectedDocumentUri != null
        InputType.OCR -> inputText.isNotEmpty()
    }
}