package com.example.sumup.presentation.screens.result

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.presentation.screens.result.components.ExportFormat
import com.example.sumup.presentation.screens.result.export.SummaryExportService
import com.example.sumup.utils.clipboard.ClipboardManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.example.sumup.domain.model.Achievement
import com.example.sumup.domain.model.AchievementDefinitions
import com.example.sumup.domain.usecase.AchievementManager

@HiltViewModel
class ResultViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val clipboardManager: ClipboardManager,
    private val summaryRepository: com.example.sumup.domain.repository.SummaryRepository,
    private val settingsRepository: com.example.sumup.domain.repository.SettingsRepository,
    private val achievementManager: AchievementManager
) : ViewModel() {
    
    private val exportService = SummaryExportService(context)
    
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()
    
    init {
        // Load summary from navigation args or saved state
        val summaryId = savedStateHandle.get<String>("summaryId")
        if (summaryId != null) {
            loadSummary(summaryId)
        } else {
            loadSummary()
        }
        
        // Load saved summary view mode preference
        viewModelScope.launch {
            settingsRepository.summaryViewMode.collect { savedMode ->
                _uiState.update { it.copy(savedViewMode = savedMode) }
            }
        }
    }
    
    private fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Try to get summary ID from navigation args first
            val summaryId = savedStateHandle.get<String>("summaryId")
            android.util.Log.d("ResultViewModel", "Loading summary with ID: $summaryId")
            
            if (!summaryId.isNullOrEmpty()) {
                // Load specific summary
                val summary = summaryRepository.getSummaryById(summaryId)
                android.util.Log.d("ResultViewModel", "Loaded summary by ID: ${summary?.id}")
                if (summary != null) {
                    updateUiStateWithSummary(summary)
                } else {
                    android.util.Log.w("ResultViewModel", "No summary found for ID: $summaryId")
                    _uiState.update { it.copy(isLoading = false) }
                }
            } else {
                // Fallback: Get the latest summary from repository (use first() to get a single emission)
                try {
                    android.util.Log.d("ResultViewModel", "No summaryId provided, loading latest summary")
                    val summaries = summaryRepository.getAllSummaries()
                        .first() // Get only the first emission
                    android.util.Log.d("ResultViewModel", "Found ${summaries.size} summaries in database")
                    val latestSummary = summaries.maxByOrNull { it.createdAt }
                    if (latestSummary != null) {
                        android.util.Log.d("ResultViewModel", "Loading latest summary: ${latestSummary.id}")
                        updateUiStateWithSummary(latestSummary)
                    } else {
                        android.util.Log.w("ResultViewModel", "No summaries found in database")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ResultViewModel", "Error loading summaries", e)
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
    
    fun loadSummary(summaryId: String) {
        viewModelScope.launch {
            summaryRepository.getSummaryById(summaryId)?.let { summary ->
                updateUiStateWithSummary(summary)
            }
        }
    }
    
    private fun updateUiStateWithSummary(summary: Summary) {
        android.util.Log.d("ResultViewModel", "Updating UI with summary: ${summary.id}")
        android.util.Log.d("ResultViewModel", "Summary text: ${summary.summary}")
        android.util.Log.d("ResultViewModel", "Bullet points: ${summary.bulletPoints.size}")
        
        _uiState.update { 
            it.copy(
                summary = summary,
                selectedPersona = summary.persona,
                currentPersona = summary.persona,
                isFavorite = summary.isFavorite,
                summaryText = summary.bulletPoints.joinToString("\n") { "• $it" },
                summaryTitle = "Summary - ${summary.persona.displayName}",
                generatedAt = summary.createdAt,
                keyPointsCount = summary.bulletPoints.size,
                summaryWordCount = summary.bulletPoints.joinToString(" ").split(" ").size,
                originalWordCount = summary.originalText?.split(" ")?.size ?: 0,
                compressionRatio = if (summary.originalText != null) {
                    val original = summary.originalText.split(" ").size.toFloat()
                    val compressed = summary.bulletPoints.joinToString(" ").split(" ").size.toFloat()
                    (1 - compressed / original) * 100
                } else 0f,
                isLoading = false // Ensure loading is set to false
            )
        }
        
        android.util.Log.d("ResultViewModel", "UI state updated. isLoading: ${_uiState.value.isLoading}, hasSummary: ${_uiState.value.summary != null}")
    }
    
    fun selectPersona(persona: SummaryPersona) {
        _uiState.update { it.copy(selectedPersona = persona) }
        regenerateSummaryWithPersona(persona)
    }
    
    fun copySummary(context: Context) {
        val summaryText = buildSummaryText(uiState.value.summary)
        clipboardManager.copyToClipboard(summaryText)
        _uiState.update { it.copy(showCopySuccess = true) }
    }
    
    fun saveSummary() {
        viewModelScope.launch {
            uiState.value.summary?.let { summary ->
                val updatedSummary = summary.copy(isFavorite = !summary.isFavorite)
                summaryRepository.updateSummary(updatedSummary)
                _uiState.update { 
                    it.copy(summary = updatedSummary)
                }
            }
        }
    }
    
    fun toggleFavorite() {
        saveSummary()
    }
    
    fun shareSummary() {
        // Share functionality will be handled by the UI layer
    }
    
    fun exportSummary(format: ExportFormat) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, exportError = null) }
            
            val summary = _uiState.value.summary
            if (summary == null) {
                _uiState.update { 
                    it.copy(
                        isExporting = false, 
                        exportError = "No summary to export"
                    ) 
                }
                return@launch
            }
            
            val result = when (format) {
                ExportFormat.PDF -> exportService.exportToPdf(summary, _uiState.value.selectedPersona)
                ExportFormat.IMAGE -> exportService.exportToImage(summary, _uiState.value.selectedPersona)
                ExportFormat.TEXT -> exportService.exportToText(summary, _uiState.value.selectedPersona)
                ExportFormat.MARKDOWN -> exportService.exportToMarkdown(summary, _uiState.value.selectedPersona)
                ExportFormat.JSON -> exportService.exportToJson(summary, _uiState.value.selectedPersona)
                ExportFormat.DOCX -> exportService.exportToMarkdown(summary, _uiState.value.selectedPersona) // Export as markdown, can be converted to DOCX
            }
            
            result.fold(
                onSuccess = { file ->
                    _uiState.update { 
                        it.copy(
                            isExporting = false,
                            exportedFile = file,
                            showExportSuccess = true
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isExporting = false,
                            exportError = error.message ?: "Export failed"
                        ) 
                    }
                }
            )
        }
    }
    
    
    fun shareExportedFile(file: File) {
        val uri = exportService.getFileUri(file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = when (file.extension) {
                "pdf" -> "application/pdf"
                "png" -> "image/png"
                "txt" -> "text/plain"
                "md" -> "text/markdown"
                else -> "*/*"
            }
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, "Share summary")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
    
    fun dismissExportSuccess() {
        _uiState.update { it.copy(showExportSuccess = false, exportedFile = null) }
    }
    
    fun clearExportError() {
        _uiState.update { it.copy(exportError = null) }
    }
    
    fun updateSummaryViewMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.updateSummaryViewMode(mode)
        }
    }
    
    fun changePersona(persona: SummaryPersona) {
        selectPersona(persona)
    }
    
    fun regenerateSummary() {
        _uiState.value.summary?.let { currentSummary ->
            regenerateSummaryWithPersona(_uiState.value.selectedPersona)
        }
    }
    
    private fun regenerateSummaryWithPersona(persona: SummaryPersona) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegenerating = true, isLoading = true) }
            
            // Simulate API delay
            kotlinx.coroutines.delay(2000)
            
            _uiState.value.summary?.let { currentSummary ->
                // Mock different summaries based on persona
                val regeneratedSummary = when (persona) {
                    SummaryPersona.GENERAL -> currentSummary.copy(
                        summary = "This content provides a comprehensive overview suitable for general audiences. ${currentSummary.summary}",
                        briefOverview = "A balanced summary covering all key aspects in an accessible manner.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "For general readers: $it" 
                        },
                        bulletPoints = currentSummary.bulletPoints.map { point ->
                            "• General insight: $point"
                        },
                        persona = persona
                    )
                    
                    SummaryPersona.TECHNICAL -> currentSummary.copy(
                        summary = "Technical analysis reveals: ${currentSummary.summary} Implementation details and architectural considerations are highlighted.",
                        briefOverview = "Technical deep-dive with focus on implementation specifics and system design.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Technical breakdown: $it\n\nCode implications and performance considerations included." 
                        },
                        bulletPoints = listOf(
                            "• Technical architecture and design patterns identified",
                            "• Performance metrics and optimization opportunities",
                            "• Implementation challenges and solutions"
                        ) + currentSummary.bulletPoints.map { point -> "• Tech detail: $point" },
                        keyInsights = listOf(
                            "System architecture follows best practices",
                            "Performance optimization opportunities identified",
                            "Security considerations addressed"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.STUDY -> currentSummary.copy(
                        summary = "Study guide summary: ${currentSummary.summary} Key learning objectives and concepts are emphasized.",
                        briefOverview = "Study-focused summary with emphasis on learning outcomes and key concepts.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Educational breakdown: $it\n\nKey learning points and study tips included." 
                        },
                        bulletPoints = listOf(
                            "• Key concepts and definitions to remember",
                            "• Important relationships between ideas",
                            "• Practice questions and review points"
                        ) + currentSummary.bulletPoints.map { point -> "• Study note: $point" },
                        keyInsights = listOf(
                            "Core concepts clearly identified",
                            "Learning objectives mapped",
                            "Study guide structure provided"
                        ),
                        actionItems = listOf(
                            "Review key definitions",
                            "Create flashcards for main concepts",
                            "Test understanding with practice questions"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.BUSINESS -> currentSummary.copy(
                        summary = "Executive summary: ${currentSummary.summary} Key business impacts and ROI considerations are emphasized.",
                        briefOverview = "Business-focused analysis highlighting strategic value and market implications.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Business impact analysis: $it\n\nROI projections and market positioning included." 
                        },
                        bulletPoints = listOf(
                            "• Strategic business value and competitive advantages",
                            "• Cost-benefit analysis and ROI projections",
                            "• Market positioning and growth opportunities"
                        ) + currentSummary.bulletPoints.map { point -> "• Business impact: $point" },
                        keyInsights = listOf(
                            "Strong market differentiation potential",
                            "Clear path to revenue growth",
                            "Operational efficiency gains expected"
                        ),
                        actionItems = listOf(
                            "Schedule stakeholder alignment meeting",
                            "Prepare detailed ROI analysis",
                            "Develop go-to-market strategy"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.LEGAL -> currentSummary.copy(
                        summary = "Legal analysis: ${currentSummary.summary} Key legal terms, obligations, and implications are highlighted.",
                        briefOverview = "Legal-focused summary emphasizing terms, conditions, and compliance requirements.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Legal analysis: $it\n\nCompliance requirements and risk factors included." 
                        },
                        bulletPoints = listOf(
                            "• Key legal terms and definitions",
                            "• Obligations and compliance requirements",
                            "• Risk factors and liabilities"
                        ) + currentSummary.bulletPoints.map { point -> "• Legal note: $point" },
                        keyInsights = listOf(
                            "Legal implications clearly identified",
                            "Compliance requirements outlined",
                            "Risk assessment provided"
                        ),
                        actionItems = listOf(
                            "Review with legal counsel",
                            "Ensure compliance with regulations",
                            "Document risk mitigation strategies"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.QUICK -> currentSummary.copy(
                        summary = currentSummary.bulletPoints.take(3).joinToString(". "),
                        briefOverview = "Ultra-concise summary with only the most essential points.",
                        detailedSummary = null, // No detailed summary for quick read
                        bulletPoints = currentSummary.bulletPoints.take(5),
                        keyInsights = currentSummary.keyInsights?.take(2),
                        actionItems = currentSummary.actionItems?.take(2),
                        persona = persona
                    )
                }
                
                // Update UI with regenerated summary
                _uiState.update { 
                    it.copy(
                        summary = regeneratedSummary,
                        isRegenerating = false,
                        isLoading = false,
                        currentPersona = persona
                    )
                }
                
                // Save the regenerated summary
                summaryRepository.saveSummary(regeneratedSummary)
                
                // Trigger achievement for trying different personas
                if (_uiState.value.personaChangeCount >= 2) {
                    val personaExplorerAchievement = AchievementDefinitions.getAllAchievements()
                        .find { it.id == "persona_explorer" }
                    if (personaExplorerAchievement != null) {
                        achievementManager.unlock(personaExplorerAchievement.type)
                        _uiState.update { it.copy(showAchievement = personaExplorerAchievement) }
                    }
                }
                _uiState.update { it.copy(personaChangeCount = it.personaChangeCount + 1) }
            }
        }
    }
    
    fun toggleShowAllBullets() {
        _uiState.update { it.copy(showAllBullets = !it.showAllBullets) }
    }
    
    fun dismissCopySuccess() {
        _uiState.update { it.copy(showCopySuccess = false) }
    }
    
    private fun buildSummaryText(summary: Summary?): String {
        return summary?.bulletPoints?.joinToString("\n") { "• $it" } ?: ""
    }
    
    fun dismissAchievement() {
        _uiState.update { it.copy(showAchievement = null) }
    }
    
    fun showMoreOptions() {
        _uiState.update { it.copy(showMoreOptionsMenu = true) }
    }
    
    fun dismissMoreOptions() {
        _uiState.update { it.copy(showMoreOptionsMenu = false) }
    }
    
    fun exportToClipboard() {
        copySummary(context)
    }
    
    fun viewInsights() {
        _uiState.update { it.copy(showInsightsDialog = true) }
    }
    
    fun dismissInsights() {
        _uiState.update { it.copy(showInsightsDialog = false) }
    }
    
    fun toggleAutoSave() {
        val newAutoSaveEnabled = !_uiState.value.autoSaveEnabled
        _uiState.update { it.copy(autoSaveEnabled = newAutoSaveEnabled) }
        
        if (newAutoSaveEnabled) {
            // Start auto-save coroutine
            viewModelScope.launch {
                while (_uiState.value.autoSaveEnabled) {
                    kotlinx.coroutines.delay(30000) // Auto-save every 30 seconds
                    _uiState.value.summary?.let { summary ->
                        summaryRepository.updateSummary(summary)
                        _uiState.update { it.copy(lastAutoSaveTime = System.currentTimeMillis()) }
                    }
                }
            }
        }
    }
}

data class ResultUiState(
    val summary: Summary? = null,
    val selectedPersona: SummaryPersona = SummaryPersona.GENERAL,
    val showAllBullets: Boolean = false,
    val showCopySuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRegenerating: Boolean = false,
    val isExporting: Boolean = false,
    val exportError: String? = null,
    val exportedFile: File? = null,
    val showExportSuccess: Boolean = false,
    val isFavorite: Boolean = false,
    val currentPersona: SummaryPersona = SummaryPersona.GENERAL,
    val availablePersonas: List<SummaryPersona> = SummaryPersona.entries,
    val summaryTitle: String = "Summary",
    val summaryText: String = "",
    val summaryLanguage: String = "en",
    val generatedAt: Long = System.currentTimeMillis(),
    val originalWordCount: Int = 0,
    val summaryWordCount: Int = 0,
    val compressionRatio: Float = 0f,
    val keyPointsCount: Int = 0,
    val personaChangeCount: Int = 0,
    val showAchievement: Achievement? = null,
    val showMoreOptionsMenu: Boolean = false,
    val showInsightsDialog: Boolean = false,
    val autoSaveEnabled: Boolean = false,
    val lastAutoSaveTime: Long? = null,
    val savedViewMode: String = "STANDARD"
) {
    init {
        summary?.let {
            // Update derived fields when summary changes
        }
    }
}