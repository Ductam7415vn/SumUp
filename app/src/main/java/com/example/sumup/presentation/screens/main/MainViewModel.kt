package com.example.sumup.presentation.screens.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.AppError
import com.example.sumup.presentation.screens.main.MainUiState.InputType
import com.example.sumup.domain.model.ServiceType
import com.example.sumup.domain.model.FileUploadState
import com.example.sumup.domain.model.LargePdfOption
import com.example.sumup.domain.repository.PdfRepository
import com.example.sumup.domain.repository.SummaryRepository
import com.example.sumup.domain.usecase.SummarizeTextUseCase
import com.example.sumup.domain.usecase.ProcessPdfUseCase
import com.example.sumup.domain.usecase.DraftManager
import com.example.sumup.domain.usecase.FeatureDiscoveryUseCase
import com.example.sumup.domain.usecase.SmartSectioningUseCase
import com.example.sumup.utils.PdfUtils
import com.example.sumup.utils.InputValidator
import com.example.sumup.utils.EnhancedApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val summarizeTextUseCase: SummarizeTextUseCase,
    private val processPdfUseCase: ProcessPdfUseCase,
    private val smartSectioningUseCase: SmartSectioningUseCase,
    private val pdfRepository: PdfRepository,
    private val summaryRepository: SummaryRepository,
    private val draftManager: DraftManager,
    private val featureDiscovery: FeatureDiscoveryUseCase,
    private val apiKeyManager: EnhancedApiKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(
        inputType = InputType.TEXT
    ))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadDraft()
        loadServiceInfo()
        loadSummaryCount()
        observeDraftChanges()
        observeApiUsageStats()
    }

    private fun loadDraft() {
        viewModelScope.launch {
            val recoveredDraft = draftManager.recoverDraft()
            if (recoveredDraft.isNotEmpty()) {
                _uiState.update { 
                    it.copy(
                        inputText = recoveredDraft,
                        recoverableDraftText = recoveredDraft,
                        showDraftRecoveryDialog = true
                    )
                }
            }
        }
    }

    private fun loadServiceInfo() {
        viewModelScope.launch {
            // Check if we have a valid API key from the EnhancedApiKeyManager
            apiKeyManager.activeKeyId.collect { activeKeyId ->
                val hasValidKey = activeKeyId != null
                _uiState.update { 
                    it.copy(
                        serviceInfo = if (hasValidKey) {
                            com.example.sumup.domain.model.ServiceInfo(
                                type = ServiceType.REAL_API
                            )
                        } else {
                            com.example.sumup.domain.model.ServiceInfo(
                                type = ServiceType.MOCK_API
                            )
                        }
                    )
                }
            }
        }
    }

    private fun loadSummaryCount() {
        // Combine all count flows to prevent multiple coroutines
        viewModelScope.launch {
            combine(
                summaryRepository.getTotalCount(),
                summaryRepository.getTodayCount(),
                summaryRepository.getWeekCount()
            ) { totalCount, todayCount, weekCount ->
                Triple(totalCount, todayCount, weekCount)
            }.collect { (totalCount, todayCount, weekCount) ->
                _uiState.update { 
                    it.copy(
                        totalCount = totalCount,
                        todayCount = todayCount,
                        weekCount = weekCount
                    )
                }
            }
        }
    }

    private fun observeDraftChanges() {
        viewModelScope.launch {
            _uiState.map { it.inputText }
                .distinctUntilChanged()
                .debounce(2000) // 2 seconds debounce
                .collect { text ->
                    if (text.isNotEmpty()) {
                        draftManager.saveDraft(text, InputType.TEXT)
                    }
                }
        }
    }
    
    private fun observeApiUsageStats() {
        viewModelScope.launch {
            // Update API usage stats every 30 seconds
            while (true) {
                val stats = apiKeyManager.getUsageStats()
                _uiState.update { it.copy(apiUsageStats = stats) }
                delay(30000) // 30 seconds
            }
        }
    }

    fun updateText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun selectInputType(type: InputType) {
        _uiState.update { it.copy(inputType = type) }
    }

    fun selectPdf(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val pageCount = PdfUtils.getPageCount(context, uri)
            val fileName = PdfUtils.getFileName(context, uri)
            
            _uiState.update { 
                it.copy(
                    selectedPdfUri = uri.toString(),
                    selectedPdfName = fileName,
                    pdfPageCount = pageCount,
                    isLoading = false
                )
            }
        }
    }

    fun selectPdfWithPreview(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val pageCount = PdfUtils.getPageCount(context, uri)
            val fileName = PdfUtils.getFileName(context, uri)
            
            if (pageCount > 50) {
                // Show warning for large PDFs
                _uiState.update { 
                    it.copy(
                        selectedPdfUri = uri.toString(),
                        selectedPdfName = fileName,
                        pdfPageCount = pageCount,
                        showLargePdfWarning = true,
                        largePdfPageCount = pageCount,
                        largePdfEstimatedTime = (pageCount * 2).toLong(), // Rough estimate
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        selectedPdfUri = uri.toString(),
                        selectedPdfName = fileName,
                        pdfPageCount = pageCount,
                        showPdfPreview = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearPdf() {
        _uiState.update { 
            it.copy(
                selectedPdfUri = null,
                selectedPdfName = null,
                pdfPageCount = 0
            )
        }
    }

    fun clearText() {
        _uiState.update { it.copy(inputText = "") }
        viewModelScope.launch {
            draftManager.clearDraft()
        }
    }

    fun showPdfPreview() {
        _uiState.update { it.copy(showPdfPreview = true) }
    }

    fun hidePdfPreview() {
        _uiState.update { it.copy(showPdfPreview = false) }
    }

    fun processPdfWithPages(pageRange: IntRange?) {
        _uiState.update { 
            it.copy(
                showPdfPreview = false,
                navigateToProcessing = true
            )
        }
        // TODO: Store pageRange in navigation args or another way
    }

    fun onLargePdfOptionSelected(option: LargePdfOption) {
        when (option) {
            LargePdfOption.FIRST_50_PAGES -> {
                _uiState.update { 
                    it.copy(
                        showLargePdfWarning = false,
                        navigateToProcessing = true
                    )
                }
                // TODO: Pass page range 1..50 via navigation
            }
            LargePdfOption.CUSTOM_RANGE -> {
                _uiState.update { 
                    it.copy(
                        showLargePdfWarning = false,
                        showPdfPreview = true // Show preview to select custom range
                    )
                }
            }
            LargePdfOption.FULL_DOCUMENT -> {
                _uiState.update { 
                    it.copy(
                        showLargePdfWarning = false,
                        navigateToProcessing = true
                    )
                }
                // TODO: Pass null page range via navigation
            }
            LargePdfOption.CANCEL -> {
                _uiState.update { 
                    it.copy(
                        showLargePdfWarning = false,
                        selectedPdfUri = null,
                        selectedPdfName = null
                    )
                }
            }
        }
    }

    fun showInfoDialog() {
        _uiState.update { it.copy(showInfoDialog = true) }
    }

    fun dismissInfoDialog() {
        _uiState.update { it.copy(showInfoDialog = false) }
    }

    fun showClearDialog() {
        _uiState.update { it.copy(showClearDialog = true) }
    }

    fun dismissClearDialog() {
        _uiState.update { it.copy(showClearDialog = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun recoverDraft() {
        _uiState.update { 
            it.copy(
                inputText = it.recoverableDraftText ?: "",
                showDraftRecoveryDialog = false
            )
        }
    }

    fun dismissDraftRecovery() {
        _uiState.update { 
            it.copy(
                showDraftRecoveryDialog = false,
                recoverableDraftText = ""
            )
        }
        viewModelScope.launch {
            draftManager.clearDraft()
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToProcessing = false) }
        // Start processing after navigation is complete
        processSummarization()
    }

    fun checkAndShowFeatureDiscovery() {
        viewModelScope.launch {
            val allTips = listOf("summarize_button", "pdf_upload", "summary_length", "ocr_button")
            val unshownTips = featureDiscovery.getUnshownTips(allTips)
            if (unshownTips.isNotEmpty()) {
                _uiState.update { 
                    it.copy(
                        showFeatureDiscovery = true,
                        currentFeatureTip = unshownTips.first()
                    )
                }
            }
        }
    }

    fun dismissFeatureTip() {
        _uiState.value.currentFeatureTip?.let { tipId ->
            viewModelScope.launch {
                featureDiscovery.markTipAsShown(tipId)
            }
        }
        _uiState.update { 
            it.copy(
                showFeatureDiscovery = false,
                currentFeatureTip = null
            )
        }
    }

    val canSummarize: Boolean
        get() = when (_uiState.value.inputType) {
            InputType.TEXT -> _uiState.value.inputText.isNotBlank()
            InputType.PDF -> _uiState.value.selectedPdfUri != null
            InputType.OCR -> false // OCR handled separately
        }

    val navigateToProcessing: Boolean
        get() = _uiState.value.navigateToProcessing

    val summaryId: String?
        get() = _uiState.value.summaryId

    
    fun summarize() {
        viewModelScope.launch {
            when (_uiState.value.inputType) {
                InputType.TEXT -> {
                    // Validate text input
                    val validationResult = InputValidator.validateTextInput(_uiState.value.inputText)
                    when (validationResult) {
                        is InputValidator.ValidationResult.Error -> {
                            _uiState.update { 
                                it.copy(error = AppError.UnknownError(validationResult.message))
                            }
                        }
                        is InputValidator.ValidationResult.Warning -> {
                            // Show warning but proceed
                            _uiState.update { it.copy(navigateToProcessing = true) }
                        }
                        is InputValidator.ValidationResult.Success -> {
                            _uiState.update { it.copy(navigateToProcessing = true) }
                        }
                    }
                }
                InputType.PDF -> {
                    _uiState.value.selectedPdfUri?.let { uriString ->
                        // Validate PDF
                        val uri = android.net.Uri.parse(uriString)
                        val validationResult = InputValidator.validatePdfFile(context, uri)
                        when (validationResult) {
                            is InputValidator.ValidationResult.Error -> {
                                _uiState.update { 
                                    it.copy(error = AppError.UnknownError(validationResult.message))
                                }
                            }
                            else -> {
                                // Additional page count validation happens after PDF is loaded
                                _uiState.update { it.copy(navigateToProcessing = true) }
                            }
                        }
                    }
                }
                InputType.OCR -> {
                    // OCR handled separately
                }
            }
        }
    }
    
    private fun processSummarization() {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Starting summarization process")
                _uiState.update { it.copy(
                    isLoading = true, 
                    error = null,
                    processingProgress = 0f,
                    processingMessage = "Initializing..."
                ) }
                
                // Simulate progress updates while processing
                val progressJob = launch {
                    val messages = listOf(
                        "Reading your text...",
                        "Analyzing content...",
                        "Understanding context...",
                        "Identifying key points...",
                        "Creating summary...",
                        "Polishing results..."
                    )
                    
                    messages.forEachIndexed { index, message ->
                        _uiState.update { it.copy(
                            processingProgress = (index + 1) / messages.size.toFloat() * 0.8f,
                            processingMessage = message
                        ) }
                        delay(500)
                    }
                }
                
                when (_uiState.value.inputType) {
                    InputType.TEXT, InputType.OCR -> {
                        android.util.Log.d("MainViewModel", "Processing text summarization")
                        val text = _uiState.value.inputText
                        
                        // Check if smart sectioning is needed
                        if (text.length >= SmartSectioningUseCase.SECTION_THRESHOLD) {
                            android.util.Log.d("MainViewModel", "Using smart sectioning for long text (${text.length} chars)")
                            progressJob.cancel()
                            
                            smartSectioningUseCase(
                                text = text,
                                persona = com.example.sumup.domain.model.SummaryPersona.GENERAL
                            ).collect { sectioningResult ->
                                when (sectioningResult) {
                                    is SmartSectioningUseCase.SectioningResult.Progress -> {
                                        val progress = sectioningResult.currentSection.toFloat() / sectioningResult.totalSections
                                        _uiState.update { it.copy(
                                            processingProgress = 0.2f + (progress * 0.6f),
                                            processingMessage = "Processing section ${sectioningResult.currentSection} of ${sectioningResult.totalSections}..."
                                        ) }
                                    }
                                    is SmartSectioningUseCase.SectioningResult.Success -> {
                                        val summary = sectioningResult.sectionedSummary.overallSummary
                                        android.util.Log.d("MainViewModel", "Smart sectioning successful, ID: ${summary.id}")
                                        _uiState.update { it.copy(
                                            isLoading = false,
                                            summary = summary,
                                            summaryId = summary.id,
                                            processingProgress = 1f,
                                            processingMessage = "Done!",
                                            navigateToResult = true
                                        ) }
                                    }
                                    is SmartSectioningUseCase.SectioningResult.Error -> {
                                        android.util.Log.e("MainViewModel", "Smart sectioning failed: ${sectioningResult.message}")
                                        _uiState.update { it.copy(
                                            isLoading = false,
                                            error = AppError.UnknownError(sectioningResult.message),
                                            processingProgress = 0f,
                                            processingMessage = ""
                                        ) }
                                    }
                                }
                            }
                        } else {
                            // Use regular summarization for shorter texts
                            val result = summarizeTextUseCase(
                                text = text,
                                persona = com.example.sumup.domain.model.SummaryPersona.GENERAL,
                                lengthMultiplier = _uiState.value.summaryLength.multiplier
                            )
                            
                            // Cancel progress simulation
                            progressJob.cancel()
                            
                            result.fold(
                                onSuccess = { summary ->
                                    android.util.Log.d("MainViewModel", "Summarization successful, ID: ${summary.id}")
                                    _uiState.update { it.copy(
                                        isLoading = false,
                                        summary = summary,
                                        summaryId = summary.id,
                                        processingProgress = 1f,
                                        processingMessage = "Done!",
                                        navigateToResult = true
                                    ) }
                                },
                                onFailure = { exception ->
                                    android.util.Log.e("MainViewModel", "Summarization failed", exception)
                                    _uiState.update { it.copy(
                                        isLoading = false,
                                        error = AppError.UnknownError(exception.message ?: "Summarization failed"),
                                        processingProgress = 0f,
                                        processingMessage = ""
                                    ) }
                                }
                            )
                        }
                    }
                    InputType.PDF -> {
                        android.util.Log.d("MainViewModel", "Processing PDF summarization")
                        _uiState.value.selectedPdfUri?.let { uriString ->
                            val uri = Uri.parse(uriString)
                            processPdfUseCase(fileUri = uri).collect { state ->
                                when (state) {
                                    is FileUploadState.Processing -> {
                                        _uiState.update { it.copy(
                                            processingProgress = state.progress,
                                            processingMessage = state.stage.displayName
                                        ) }
                                    }
                                    is FileUploadState.Success -> {
                                        // Process the extracted text with summarization
                                        progressJob.cancel()
                                        val extractedText = state.extractedText
                                        
                                        // Check if smart sectioning is needed for PDF
                                        if (extractedText.length >= SmartSectioningUseCase.SECTION_THRESHOLD) {
                                            android.util.Log.d("MainViewModel", "Using smart sectioning for long PDF (${extractedText.length} chars)")
                                            
                                            smartSectioningUseCase(
                                                text = extractedText,
                                                persona = com.example.sumup.domain.model.SummaryPersona.GENERAL
                                            ).collect { sectioningResult ->
                                                when (sectioningResult) {
                                                    is SmartSectioningUseCase.SectioningResult.Progress -> {
                                                        val progress = sectioningResult.currentSection.toFloat() / sectioningResult.totalSections
                                                        _uiState.update { it.copy(
                                                            processingProgress = 0.5f + (progress * 0.4f),
                                                            processingMessage = "Processing PDF section ${sectioningResult.currentSection} of ${sectioningResult.totalSections}..."
                                                        ) }
                                                    }
                                                    is SmartSectioningUseCase.SectioningResult.Success -> {
                                                        val summary = sectioningResult.sectionedSummary.overallSummary
                                                        android.util.Log.d("MainViewModel", "PDF smart sectioning successful, ID: ${summary.id}")
                                                        _uiState.update { it.copy(
                                                            isLoading = false,
                                                            summary = summary,
                                                            summaryId = summary.id,
                                                            processingProgress = 1f,
                                                            processingMessage = "Done!",
                                                            navigateToResult = true
                                                        ) }
                                                    }
                                                    is SmartSectioningUseCase.SectioningResult.Error -> {
                                                        android.util.Log.e("MainViewModel", "PDF smart sectioning failed: ${sectioningResult.message}")
                                                        _uiState.update { it.copy(
                                                            isLoading = false,
                                                            error = AppError.UnknownError(sectioningResult.message),
                                                            processingProgress = 0f,
                                                            processingMessage = ""
                                                        ) }
                                                    }
                                                }
                                            }
                                        } else {
                                            // Use regular summarization for shorter PDFs
                                            val result = summarizeTextUseCase(
                                                text = extractedText,
                                                persona = com.example.sumup.domain.model.SummaryPersona.GENERAL,
                                                lengthMultiplier = _uiState.value.summaryLength.multiplier
                                            )
                                            
                                            result.fold(
                                                onSuccess = { summary ->
                                                    android.util.Log.d("MainViewModel", "PDF summarization successful, ID: ${summary.id}")
                                                    _uiState.update { it.copy(
                                                        isLoading = false,
                                                        summary = summary,
                                                        summaryId = summary.id,
                                                        processingProgress = 1f,
                                                        processingMessage = "Done!",
                                                        navigateToResult = true
                                                    ) }
                                                },
                                                onFailure = { exception ->
                                                    android.util.Log.e("MainViewModel", "PDF summarization failed", exception)
                                                    _uiState.update { it.copy(
                                                        isLoading = false,
                                                        error = AppError.UnknownError(exception.message ?: "PDF summarization failed"),
                                                        processingProgress = 0f,
                                                        processingMessage = ""
                                                    ) }
                                                }
                                            )
                                        }
                                    }
                                    is FileUploadState.Error -> {
                                        progressJob.cancel()
                                        android.util.Log.e("MainViewModel", "PDF processing error: ${state.error.message}")
                                        _uiState.update { it.copy(
                                            isLoading = false,
                                            error = AppError.UnknownError(state.error.message),
                                            processingProgress = 0f,
                                            processingMessage = ""
                                        ) }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Unexpected error during summarization", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    error = AppError.UnknownError(e.message ?: "An unexpected error occurred"),
                    processingProgress = 0f,
                    processingMessage = ""
                ) }
            }
        }
    }
    
    val retrySummarization: () -> Unit = {
        processSummarization()
    }
    
    val cancelSummarization: () -> Unit = {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = false,
                processingProgress = 0f,
                processingMessage = "",
                error = null
            ) }
        }
    }
    
    fun onResultNavigationHandled() {
        _uiState.update { it.copy(navigateToResult = false, summaryId = null) }
    }
    
    // Extension functions
    fun String.summarize(size: Int = 50): String {
        return if (length <= size) this
        else "${take(size)}..."
    }
}

data class FeatureTip(
    val id: String,
    val title: String,
    val description: String,
    val targetElement: String
)

// Make extension function public
fun String.summarize(size: Int = 50): String {
    return if (length <= size) this
    else "${take(size)}..."
}