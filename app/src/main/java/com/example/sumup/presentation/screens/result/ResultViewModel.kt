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
                            "• $point"
                        },
                        persona = persona
                    )
                    
                    SummaryPersona.STUDY -> currentSummary.copy(
                        summary = "Study guide summary: ${currentSummary.summary} Key learning objectives and concepts are emphasized.",
                        briefOverview = "Study-focused summary with emphasis on learning outcomes and key concepts.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Educational breakdown: $it\n\nKey learning points and study tips included." 
                        },
                        bulletPoints = listOf(
                            "• 📚 Key concept: Define and understand the main terminology",
                            "• 🔍 Important: Note the relationships between different ideas",
                            "• ✏️ Remember: This will likely be on the exam"
                        ) + currentSummary.bulletPoints.map { point -> "• Study note: $point" },
                        keyInsights = listOf(
                            "Core concepts clearly identified for easy memorization",
                            "Learning objectives mapped to study goals",
                            "Study guide structure follows logical progression"
                        ),
                        actionItems = listOf(
                            "Review these key definitions before class",
                            "Create flashcards for the main concepts",
                            "Test your understanding with practice questions"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.PROFESSIONAL -> currentSummary.copy(
                        summary = "Executive summary: ${currentSummary.summary} Key business impacts and actionable insights are highlighted.",
                        briefOverview = "Professional analysis with focus on actionable outcomes and strategic implications.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Professional analysis: $it\n\nAction items and next steps included." 
                        },
                        bulletPoints = listOf(
                            "• Strategic insight: Identifies key business opportunities",
                            "• Action required: Implementation timeline needed",
                            "• Impact analysis: Potential ROI and resource requirements"
                        ) + currentSummary.bulletPoints.map { point -> "• Business context: $point" },
                        keyInsights = listOf(
                            "Clear business value proposition identified",
                            "Implementation roadmap can be developed",
                            "Resource allocation decisions needed"
                        ),
                        actionItems = listOf(
                            "Schedule stakeholder alignment meeting by EOW",
                            "Prepare implementation proposal with timeline",
                            "Identify resource requirements and budget impact"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.ACADEMIC -> currentSummary.copy(
                        summary = "Academic analysis: ${currentSummary.summary} Scholarly context and theoretical framework preserved.",
                        briefOverview = "Academic summary maintaining scholarly rigor and proper citations.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Scholarly analysis: $it\n\nTheoretical implications and research context included." 
                        },
                        bulletPoints = listOf(
                            "• Literature review: Connects to existing research (citation needed)",
                            "• Methodology: Follows established academic frameworks",
                            "• Findings: Statistical significance and limitations noted"
                        ) + currentSummary.bulletPoints.map { point -> "• Research note: $point" },
                        keyInsights = listOf(
                            "Research methodology appears sound and replicable",
                            "Findings contribute to existing body of knowledge",
                            "Further research recommended in specific areas"
                        ),
                        actionItems = listOf(
                            "Verify all citations and references",
                            "Consider peer review before publication",
                            "Identify areas for follow-up research"
                        ),
                        persona = persona
                    )
                    
                    SummaryPersona.SIMPLE -> currentSummary.copy(
                        summary = "Here's what this is about in simple terms: ${currentSummary.summary.take(200)}... It basically means ${currentSummary.bulletPoints.firstOrNull() ?: "the main point"}.",
                        briefOverview = "Easy-to-understand summary using everyday language.",
                        detailedSummary = currentSummary.detailedSummary?.let { 
                            "Let me explain this simply: $it\n\nThink of it like this - it's similar to everyday situations you already know." 
                        },
                        bulletPoints = listOf(
                            "• The main idea is simple: ${currentSummary.bulletPoints.firstOrNull()?.take(50) ?: "Key point"}",
                            "• In other words: This affects your daily life",
                            "• What this means for you: Easy to understand and apply"
                        ) + currentSummary.bulletPoints.take(2).map { point -> 
                            "• Simply put: ${point.take(50)}..." 
                        },
                        keyInsights = listOf(
                            "This is easier to understand than it first appears",
                            "The basic concept is something we all deal with",
                            "You can apply this in your everyday life"
                        ),
                        keywords = listOf("simple", "easy", "understand", "everyday", "basic"),
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