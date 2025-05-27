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
                                selectedPersona = summary.persona
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
    val isLoading: Boolean = false
)