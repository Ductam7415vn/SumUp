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
// import com.example.sumup.domain.usecase.EnhancedFeatureDiscoveryUseCase
// import com.example.sumup.domain.usecase.UserAction
// import com.example.sumup.domain.usecase.AppState
// import com.example.sumup.domain.usecase.UserLevel
import com.example.sumup.domain.usecase.SmartSectioningUseCase
import com.example.sumup.utils.PdfUtils
import com.example.sumup.utils.InputValidator
import com.example.sumup.utils.EnhancedApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Job
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
    // private val enhancedFeatureDiscovery: EnhancedFeatureDiscoveryUseCase,
    private val apiKeyManager: EnhancedApiKeyManager,
    private val analyticsHelper: com.example.sumup.utils.analytics.AnalyticsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(
        inputType = InputType.TEXT
    ))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private val stateMutex = Mutex()

    init {
        loadDraft()
        loadServiceInfo()
        loadSummaryCount()
        observeDraftChanges()
        observeApiUsageStats()
        checkWelcomeCardVisibility()
        // initializeEnhancedFeatureDiscovery()
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
                
                // Check if we should show welcome card when count updates
                checkWelcomeCardVisibility()
            }
        }
    }

    private fun observeDraftChanges() {
        viewModelScope.launch {
            _uiState
                .map { it.inputText }
                .distinctUntilChanged()
                .debounce(2000) // 2 seconds debounce
                .flowOn(Dispatchers.Default) // Process on default dispatcher
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
            while (isActive) { // Check if coroutine is still active
                try {
                    val stats = apiKeyManager.getUsageStats()
                    _uiState.update { it.copy(apiUsageStats = stats) }
                } catch (e: Exception) {
                    // Log error but continue
                    android.util.Log.e("MainViewModel", "Error updating API stats", e)
                }
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
                // Add all unshown tips to queue
                tooltipQueue.clear()
                tooltipQueue.addAll(unshownTips)
                
                // Show first tooltip
                showNextTooltip()
            }
        }
    }

    // Tooltip queue for sequential display
    private val tooltipQueue = mutableListOf<String>()
    
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
        
        // Show next tooltip after a delay
        viewModelScope.launch {
            delay(500) // Small delay for better UX
            showNextTooltip()
        }
    }
    
    private fun showNextTooltip() {
        if (tooltipQueue.isNotEmpty()) {
            val nextTip = tooltipQueue.removeAt(0)
            _uiState.update {
                it.copy(
                    showFeatureDiscovery = true,
                    currentFeatureTip = nextTip
                )
            }
        }
    }
    
    fun skipAllTooltips() {
        tooltipQueue.clear()
        _uiState.update { 
            it.copy(
                showFeatureDiscovery = false,
                currentFeatureTip = null
            )
        }
        // Mark all as shown
        viewModelScope.launch {
            val allTips = listOf("summarize_button", "pdf_upload", "summary_length", "ocr_button")
            allTips.forEach { tipId ->
                featureDiscovery.markTipAsShown(tipId)
            }
        }
    }
    
    private fun checkWelcomeCardVisibility() {
        viewModelScope.launch {
            val hasSeenWelcome = draftManager.getHasSeenWelcomeCard()
            val shouldShow = _uiState.value.totalCount == 0 && !hasSeenWelcome
            _uiState.update { it.copy(showWelcomeCard = shouldShow) }
        }
    }
    
    fun dismissWelcomeCard() {
        _uiState.update { it.copy(showWelcomeCard = false) }
        viewModelScope.launch {
            draftManager.setHasSeenWelcomeCard(true)
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
                
                // Use a separate job for progress simulation
                var progressJob: Job? = null
                
                // Only start progress job for non-PDF input
                if (_uiState.value.inputType != InputType.PDF) {
                    progressJob = launch(Dispatchers.Default) {
                        val messages = listOf(
                            "Reading your text...",
                            "Analyzing content...",
                            "Understanding context...",
                            "Identifying key points...",
                            "Creating summary...",
                            "Polishing results..."
                        )
                        
                        messages.forEachIndexed { index, message ->
                            if (isActive) {
                                _uiState.update { it.copy(
                                    processingProgress = (index + 1) / messages.size.toFloat() * 0.8f,
                                    processingMessage = message
                                ) }
                                delay(500)
                            }
                        }
                    }
                }
                
                when (_uiState.value.inputType) {
                    InputType.TEXT, InputType.OCR -> {
                        android.util.Log.d("MainViewModel", "Processing text summarization")
                        val text = _uiState.value.inputText
                        
                        // Check if smart sectioning is needed
                        if (text.length >= SmartSectioningUseCase.SECTION_THRESHOLD) {
                            android.util.Log.d("MainViewModel", "Using smart sectioning for long text (${text.length} chars)")
                            progressJob?.cancel()
                            
                            smartSectioningUseCase(
                                text = text,
                                persona = com.example.sumup.domain.model.SummaryPersona.GENERAL
                            )
                            .flowOn(Dispatchers.IO)
                            .onEach { delay(50) } // Small delay to prevent too rapid emissions
                            .collect { sectioningResult ->
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
                            progressJob?.cancel()
                            
                            result.fold(
                                onSuccess = { summary ->
                                    android.util.Log.d("MainViewModel", "Summarization successful, ID: ${summary.id}")
                                    
                                    // Track successful summarization
                                    analyticsHelper.logEvent(
                                        com.example.sumup.utils.analytics.AnalyticsEvent.FeatureUsed(
                                            featureName = "summarize_success",
                                            context = mapOf(
                                                "input_type" to _uiState.value.inputType.name,
                                                "text_length" to text.length,
                                                "persona" to "GENERAL",
                                                "summary_length" to _uiState.value.summaryLength.name
                                            )
                                        )
                                    )
                                    
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
                                    
                                    // Track failure
                                    analyticsHelper.logEvent(
                                        com.example.sumup.utils.analytics.AnalyticsEvent.SummarizeError(
                                            error = exception.message ?: "Unknown error"
                                        )
                                    )
                                    
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
                            processPdfUseCase(fileUri = uri)
                                .flowOn(Dispatchers.IO)
                                .onEach { delay(50) } // Small delay to prevent too rapid emissions
                                .collect { state ->
                                    when (state) {
                                        is FileUploadState.Processing -> {
                                            _uiState.update { it.copy(
                                                processingProgress = state.progress,
                                                processingMessage = state.stage.displayName
                                            ) }
                                        }
                                    is FileUploadState.Success -> {
                                        // Process the extracted text with summarization
                                        val extractedText = state.extractedText
                                        
                                        // Check if smart sectioning is needed for PDF
                                        if (extractedText.length >= SmartSectioningUseCase.SECTION_THRESHOLD) {
                                            android.util.Log.d("MainViewModel", "Using smart sectioning for long PDF (${extractedText.length} chars)")
                                            
                                            smartSectioningUseCase(
                                                text = extractedText,
                                                persona = com.example.sumup.domain.model.SummaryPersona.GENERAL
                                            )
                                            .flowOn(Dispatchers.IO)
                                            .onEach { delay(50) } // Small delay to prevent too rapid emissions
                                            .collect { sectioningResult ->
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
                                        // No progressJob for PDF processing
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
    
    fun setScannedText(text: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    inputText = text,
                    inputType = InputType.OCR
                )
            }
            // Save as draft
            draftManager.saveDraft(text, InputType.OCR)
        }
    }
    
    // Enhanced Feature Discovery Methods - COMMENTED OUT TEMPORARILY
    /*
    private fun initializeEnhancedFeatureDiscovery() {
        // Initialize the enhanced feature discovery system
        enhancedFeatureDiscovery.initialize(viewModelScope)
        
        // Update app state for contextual triggers
        viewModelScope.launch {
            // Track summary count changes
            summaryRepository.getTotalCount().collect { count ->
                enhancedFeatureDiscovery.updateAppState {
                    copy(summaryCount = count)
                }
            }
        }
        
        // Track API key status
        viewModelScope.launch {
            apiKeyManager.activeKeyId.collect { keyId ->
                enhancedFeatureDiscovery.updateAppState {
                    copy(hasApiKey = keyId != null)
                }
            }
        }
    }
    
    fun trackFeatureDiscoveryAction(action: String, target: String = "") {
        enhancedFeatureDiscovery.trackUserAction(
            UserAction(
                type = action,
                target = target,
                metadata = mapOf(
                    "screen" to "main",
                    "input_type" to _uiState.value.inputType.name
                )
            )
        )
    }
    
    fun showEnhancedTooltips() {
        viewModelScope.launch {
            // Update current app state
            enhancedFeatureDiscovery.updateAppState {
                copy(
                    currentScreen = "main",
                    textLength = _uiState.value.inputText.length,
                    lastPdfSize = _uiState.value.selectedPdfUri?.let { 
                        // Get PDF size if available
                        0L // Placeholder
                    } ?: 0L
                )
            }
            
            // Get smart suggestions
            val suggestions = enhancedFeatureDiscovery.getSmartSuggestions()
            if (suggestions.isNotEmpty()) {
                _uiState.update {
                    it.copy(showEnhancedTooltips = true)
                }
            }
        }
    }
    
    fun onTextFieldFocused() {
        trackFeatureDiscoveryAction("text_field_focused", "main_input")
        enhancedFeatureDiscovery.updateAppState {
            copy(textLength = _uiState.value.inputText.length)
        }
    }
    
    fun onPdfButtonClicked() {
        trackFeatureDiscoveryAction("pdf_button_clicked", "pdf_upload")
    }
    
    fun onOcrButtonClicked() {
        trackFeatureDiscoveryAction("ocr_button_clicked", "ocr_capture")
    }
    
    fun onLengthSelectorClicked() {
        trackFeatureDiscoveryAction("length_selector_clicked", "summary_length")
    }
    */
    
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