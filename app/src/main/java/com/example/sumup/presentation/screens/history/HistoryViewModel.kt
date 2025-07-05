package com.example.sumup.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        loadSummaries()
    }
    
    private fun loadSummaries() {
        viewModelScope.launch {
            combine(
                summaryRepository.getAllSummaries(),
                searchQuery
            ) { summaries, query ->
                val filtered = if (query.isEmpty()) {
                    summaries
                } else {
                    summaries.filter { summary ->
                        summary.originalText.contains(query, ignoreCase = true) ||
                        summary.summaryText.contains(query, ignoreCase = true)
                    }
                }
                
                val grouped = filtered
                    .sortedByDescending { it.createdAt }
                    .groupBy { summary ->
                        getTimeframeLabel(summary.createdAt)
                    }
                
                HistoryUiState(
                    groupedSummaries = grouped,
                    isLoading = false,
                    isEmpty = filtered.isEmpty(),
                    totalCount = summaries.size
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun deleteSummary(summaryId: String) {
        viewModelScope.launch {
            summaryRepository.deleteSummary(summaryId)
        }
    }
    
    fun deleteMultipleSummaries(summaryIds: Set<String>) {
        viewModelScope.launch {
            summaryIds.forEach { id ->
                summaryRepository.deleteSummary(id)
            }
            _uiState.update { it.copy(selectedItems = emptySet(), isSelectionMode = false) }
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            summaryRepository.deleteAllSummaries()
        }
    }
    
    fun toggleFavorite(summaryId: String) {
        viewModelScope.launch {
            summaryRepository.getSummaryById(summaryId)?.let { summary ->
                val updated = summary.copy(isFavorite = !summary.isFavorite)
                summaryRepository.updateSummary(updated)
            }
        }
    }
    
    fun enterSelectionMode(summaryId: String) {
        _uiState.update { 
            it.copy(
                isSelectionMode = true,
                selectedItems = setOf(summaryId)
            )
        }
    }
    
    fun exitSelectionMode() {
        _uiState.update { 
            it.copy(
                isSelectionMode = false,
                selectedItems = emptySet()
            )
        }
    }
    
    fun toggleItemSelection(summaryId: String) {
        _uiState.update { state ->
            val newSelection = if (summaryId in state.selectedItems) {
                state.selectedItems - summaryId
            } else {
                state.selectedItems + summaryId
            }
            state.copy(selectedItems = newSelection)
        }
    }
    
    fun selectAll() {
        _uiState.update { state ->
            val allIds = state.groupedSummaries.flatMap { it.value }.map { it.id }.toSet()
            state.copy(selectedItems = allIds)
        }
    }
    
    fun refreshHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Force reload from repository
            loadSummaries()
        }
    }
    
    suspend fun getDatabaseSize(): String {
        return summaryRepository.getDatabaseSize()
    }
    
    private fun getTimeframeLabel(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val today = getStartOfDay(now)
        val yesterday = today - TimeUnit.DAYS.toMillis(1)
        val thisWeek = today - TimeUnit.DAYS.toMillis(7)
        val thisMonth = today - TimeUnit.DAYS.toMillis(30)
        
        return when {
            timestamp >= today -> "Today"
            timestamp >= yesterday -> "Yesterday"
            timestamp >= thisWeek -> "This Week"
            timestamp >= thisMonth -> "This Month"
            else -> {
                val date = Date(timestamp)
                val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                monthFormat.format(date)
            }
        }
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

data class HistoryUiState(
    val groupedSummaries: Map<String, List<Summary>> = emptyMap(),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val totalCount: Int = 0,
    val isSelectionMode: Boolean = false,
    val selectedItems: Set<String> = emptySet(),
    val error: String? = null
)