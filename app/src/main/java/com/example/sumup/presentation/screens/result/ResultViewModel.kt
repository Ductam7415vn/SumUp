package com.example.sumup.presentation.screens.result

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.utils.clipboard.ClipboardManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val clipboardManager: ClipboardManager,
    private val summaryRepository: com.example.sumup.domain.repository.SummaryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()
    
    init {
        // Load summary from navigation args or saved state
        loadSummary()
    }
    
    private fun loadSummary() {
        viewModelScope.launch {
            // Get the latest summary from repository
            summaryRepository.getAllSummaries()
                .collect { summaries ->
                    val latestSummary = summaries.maxByOrNull { it.createdAt }
                    latestSummary?.let { summary ->
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
                                } else 0f
                            )
                        }
                    }
                }
        }
    }
    
    fun selectPersona(persona: SummaryPersona) {
        _uiState.update { it.copy(selectedPersona = persona) }
        // TODO: Regenerate summary with new persona
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
    
    fun exportSummary() {
        // Export functionality will be handled by the UI layer
    }
    
    fun changePersona(persona: SummaryPersona) {
        selectPersona(persona)
        _uiState.update { it.copy(isRegenerating = true) }
        // TODO: Implement actual regeneration
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Mock delay
            _uiState.update { it.copy(isRegenerating = false) }
        }
    }
    
    fun regenerateSummary() {
        // TODO: Navigate back to processing with regenerate flag
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
}

data class ResultUiState(
    val summary: Summary? = null,
    val selectedPersona: SummaryPersona = SummaryPersona.GENERAL,
    val showAllBullets: Boolean = false,
    val showCopySuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRegenerating: Boolean = false,
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
    val keyPointsCount: Int = 0
) {
    init {
        summary?.let {
            // Update derived fields when summary changes
        }
    }
}