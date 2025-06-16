package com.example.sumup.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.AppError
import com.example.sumup.domain.model.FileUploadState
import com.example.sumup.domain.usecase.SummarizeTextUseCase
import com.example.sumup.domain.usecase.ExtractPdfTextUseCase
import com.example.sumup.domain.usecase.ProcessPdfUseCase
import com.example.sumup.utils.drafts.DraftManager
import com.example.sumup.utils.drafts.DraftInputType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val summarizeTextUseCase: SummarizeTextUseCase,
    private val extractPdfTextUseCase: ExtractPdfTextUseCase,
    private val processPdfUseCase: ProcessPdfUseCase,
    private val pdfRepository: com.example.sumup.domain.repository.PdfRepository,
    private val draftManager: DraftManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Draft management
    val draftFlow = draftManager.getCurrentDraft()
    val hasUnsavedDraft = draftManager.hasUnsavedDraft()
    
    init {
        checkForRecoverableDraft()
    }
    
    private fun checkForRecoverableDraft() {
        viewModelScope.launch {
            val savedDraft = draftManager.getLatestDraft()
            if (savedDraft != null && savedDraft.content.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        showDraftRecoveryDialog = true,
                        recoverableDraftText = savedDraft.content
                    )
                }
            }
        }
    }
    
    fun recoverDraft() {
        val draftText = uiState.value.recoverableDraftText
        _uiState.update {
            it.copy(
                inputText = draftText,
                canSummarize = draftText.trim().length >= 50,
                showDraftRecoveryDialog = false,
                recoverableDraftText = ""
            )
        }
    }
    
    fun dismissDraftRecovery() {
        viewModelScope.launch {
            draftManager.clearDraft()
        }
        _uiState.update {
            it.copy(
                showDraftRecoveryDialog = false,
                recoverableDraftText = ""
            )
        }
    }
    
    fun toggleAutoSave() {
        _uiState.update {
            it.copy(autoSaveEnabled = !it.autoSaveEnabled)
        }
    }

    fun updateText(text: String) {
        // Enforce character limit
        val trimmedText = if (text.length > 5000) text.take(5000) else text
        
        _uiState.update { 
            it.copy(
                inputText = trimmedText,
                canSummarize = trimmedText.trim().length >= 50,
                error = null
            )
        }
        
        // Auto-save draft if enabled
        if (uiState.value.autoSaveEnabled && trimmedText.isNotEmpty()) {
            viewModelScope.launch {
                draftManager.saveDraft(trimmedText, DraftInputType.TEXT)
            }
        }
    }

    fun clearText() {
        _uiState.update { 
            it.copy(
                inputText = "",
                canSummarize = false,
                error = null,
                summary = null
            )
        }
    }
    
    fun showClearDialog() {
        if (uiState.value.inputText.isNotEmpty()) {
            _uiState.update { it.copy(showClearDialog = true) }
        }
    }
    
    fun confirmClear() {
        _uiState.update { 
            it.copy(
                inputText = "",
                canSummarize = false,
                showClearDialog = false,
                error = null,
                summary = null
            )
        }
    }

    fun dismissClearDialog() {
        _uiState.update { it.copy(showClearDialog = false) }
    }
    
    // PDF handling methods
    fun selectInputType(inputType: MainUiState.InputType) {
        _uiState.update { 
            it.copy(
                inputType = inputType,
                canSummarize = when (inputType) {
                    MainUiState.InputType.TEXT -> it.inputText.trim().length >= 50
                    MainUiState.InputType.PDF -> it.selectedPdfUri != null
                    MainUiState.InputType.OCR -> it.inputText.trim().length >= 50
                },
                error = null
            )
        }
    }
    
    fun selectPdf(uri: String, fileName: String) {
        _uiState.update { 
            it.copy(
                selectedPdfUri = uri,
                selectedPdfName = fileName,
                canSummarize = true,
                inputType = MainUiState.InputType.PDF,
                error = null
            )
        }
    }
    
    fun clearPdf() {
        _uiState.update { 
            it.copy(
                selectedPdfUri = null,
                selectedPdfName = null,
                canSummarize = false,
                error = null
            )
        }
    }

    fun summarize() {
        // Prevent multiple simultaneous summarizations
        if (_uiState.value.isLoading) {
            return
        }
        
        val currentState = _uiState.value
        
        // Validate input based on type
        when (currentState.inputType) {
            MainUiState.InputType.TEXT -> {
                val text = currentState.inputText.trim()
                if (text.length < 50) {
                    _uiState.update { 
                        it.copy(error = AppError.TextTooShortError)
                    }
                    return
                }
                processSummarization(text)
            }
            MainUiState.InputType.PDF -> {
                val pdfUri = currentState.selectedPdfUri
                if (pdfUri == null) {
                    _uiState.update { 
                        it.copy(error = AppError.InvalidInputError)
                    }
                    return
                }
                processPdfSummarization(pdfUri)
            }
            MainUiState.InputType.OCR -> {
                val text = currentState.inputText.trim()
                if (text.length < 50) {
                    _uiState.update { 
                        it.copy(error = AppError.TextTooShortError)
                    }
                    return
                }
                processSummarization(text)
            }
        }
    }
    
    // Job reference for cancellation
    private var summarizationJob: kotlinx.coroutines.Job? = null
    
    private fun processSummarization(text: String) {
        // Cancel any existing job
        summarizationJob?.cancel()
        
        summarizationJob = viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    navigateToProcessing = true,
                    error = null
                )
            }

            summarizeTextUseCase(text)
                .onSuccess { summary ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            summary = summary
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = AppError.UnknownError(exception.message ?: "Unknown error")
                        )
                    }
                }
        }
    }
    
    private fun processPdfSummarization(pdfUri: String) {
        viewModelScope.launch {
            processPdfUseCase(android.net.Uri.parse(pdfUri))
                .collect { state ->
                    when (state) {
                        is FileUploadState.Processing -> {
                            _uiState.update { 
                                it.copy(
                                    fileUploadState = state,
                                    processingProgress = state.progress,
                                    processingMessage = when (state.stage) {
                                        com.example.sumup.domain.model.ProcessingStage.READING_FILE -> "Reading PDF..."
                                        com.example.sumup.domain.model.ProcessingStage.EXTRACTING_TEXT -> "Extracting text..."
                                        com.example.sumup.domain.model.ProcessingStage.CLEANING_TEXT -> "Cleaning text..."
                                        com.example.sumup.domain.model.ProcessingStage.PREPARING_SUMMARY -> "Preparing summary..."
                                    }
                                )
                            }
                        }
                        is FileUploadState.Success -> {
                            _uiState.update { 
                                it.copy(
                                    fileUploadState = state,
                                    inputText = state.extractedText,
                                    inputType = MainUiState.InputType.TEXT,
                                    navigateToProcessing = true
                                )
                            }
                            // Now process the extracted text
                            processSummarization(state.extractedText)
                        }
                        is FileUploadState.Error -> {
                            _uiState.update { 
                                it.copy(
                                    fileUploadState = state,
                                    isLoading = false,
                                    error = when (state.error) {
                                        is com.example.sumup.domain.model.FileUploadError.FileTooLarge -> 
                                            AppError.UnknownError("File too large. Maximum size is 10MB")
                                        is com.example.sumup.domain.model.FileUploadError.NoTextFound -> 
                                            AppError.UnknownError("No text found in PDF. Try a text-based PDF")
                                        else -> AppError.UnknownError("Failed to process PDF")
                                    }
                                )
                            }
                        }
                        else -> {
                            _uiState.update { 
                                it.copy(fileUploadState = state)
                            }
                        }
                    }
                }
        }
    }

    private suspend fun simulateProgress() {
        // Simulate AI processing stages
        _uiState.update { 
            it.copy(
                processingProgress = 0.15f,
                processingMessage = "Reading your text..."
            )
        }
        delay(500)

        _uiState.update { 
            it.copy(
                processingProgress = 0.5f,
                processingMessage = "Understanding context..."
            )
        }
        delay(1000)

        _uiState.update { 
            it.copy(
                processingProgress = 0.85f,
                processingMessage = "Creating summary..."
            )
        }
        delay(500)

        _uiState.update { 
            it.copy(
                processingProgress = 0.95f,
                processingMessage = "Almost done..."
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun setScannedText(text: String) {
        updateText(text)
    }
    
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToProcessing = false) }
    }
    
    fun showInfoDialog() {
        _uiState.update { it.copy(showInfoDialog = true) }
    }
    
    fun dismissInfoDialog() {
        _uiState.update { it.copy(showInfoDialog = false) }
    }
    
}