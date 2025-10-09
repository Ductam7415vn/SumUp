package com.example.sumup.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import com.example.sumup.presentation.screens.main.MainUiState.InputType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val analyticsManager: com.example.sumup.analytics.AnalyticsManager,
    private val crashlyticsManager: com.example.sumup.analytics.CrashlyticsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(SummaryFilters())
    val filters: StateFlow<SummaryFilters> = _filters.asStateFlow()

    // Undo support - temporarily store deleted items
    private var recentlyDeletedSummary: Summary? = null
    private var recentlyDeletedSummaries: List<Summary>? = null

    init {
        // Analytics: Log screen view
        analyticsManager.logScreenView(com.example.sumup.analytics.AnalyticsManager.SCREEN_HISTORY)
        crashlyticsManager.setCurrentScreen(com.example.sumup.analytics.AnalyticsManager.SCREEN_HISTORY)

        loadSummaries()
    }
    
    private fun loadSummaries() {
        viewModelScope.launch {
            combine(
                summaryRepository.getAllSummaries(),
                searchQuery.debounce(300), // 300ms debounce per UC-007 AC-007.1
                filters
            ) { summaries, query, activeFilters ->
                // Apply search filter
                var filtered = if (query.isEmpty()) {
                    summaries
                } else {
                    summaries.filter { summary ->
                        matchesSearch(summary, query)
                    }
                }

                // Apply date filter
                val dateRange = DateFilter.getFilterRange(
                    activeFilters.dateFilter,
                    activeFilters.customDateStart,
                    activeFilters.customDateEnd
                )
                if (dateRange != null) {
                    filtered = filtered.filter { it.createdAt in dateRange }
                }

                // Apply persona filter
                if (activeFilters.selectedPersonas.isNotEmpty()) {
                    filtered = filtered.filter { it.persona in activeFilters.selectedPersonas }
                }

                // Apply input type filter
                if (activeFilters.selectedInputTypes.isNotEmpty()) {
                    filtered = filtered.filter { summary ->
                        getInputType(summary) in activeFilters.selectedInputTypes
                    }
                }

                // Apply favorites filter
                if (activeFilters.favoritesOnly) {
                    filtered = filtered.filter { it.isFavorite }
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
                    totalCount = summaries.size,
                    filteredCount = filtered.size,
                    filters = activeFilters,
                    searchQuery = query
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    /**
     * Fuzzy search matching (UC-007 AC-007.1)
     * Searches across: original text, summary, tags
     */
    private fun matchesSearch(summary: Summary, query: String): Boolean {
        val lowerQuery = query.lowercase()
        return summary.originalText.lowercase().contains(lowerQuery) ||
                summary.summaryText.lowercase().contains(lowerQuery) ||
                summary.bulletPoints.any { it.lowercase().contains(lowerQuery) } ||
                summary.keywords.orEmpty().any { it.lowercase().contains(lowerQuery) }
    }

    /**
     * Determine input type from summary
     */
    private fun getInputType(summary: Summary): InputType {
        return when {
            summary.originalText.startsWith("[PDF]") -> InputType.DOCUMENT
            summary.originalText.startsWith("[OCR]") -> InputType.OCR
            else -> InputType.TEXT
        }
    }
    
    fun updateSearchQuery(query: String) {
        // Analytics: Track search usage
        if (query.isNotEmpty() && query.length >= 3) {
            analyticsManager.logShare(
                method = "search",
                contentType = "query"
            )
            crashlyticsManager.logAction("Search query", "Length: ${query.length}")
        }

        _searchQuery.value = query
    }

    fun deleteSummary(summaryId: String) {
        viewModelScope.launch {
            // Store summary for potential undo
            recentlyDeletedSummary = summaryRepository.getSummaryById(summaryId)

            // Analytics: Track single summary deletion
            analyticsManager.logShare(
                method = "delete_single",
                contentType = "summary"
            )
            crashlyticsManager.logAction("Delete summary", "ID: $summaryId")

            summaryRepository.deleteSummary(summaryId)
        }
    }

    fun undoDelete(summaryId: String) {
        viewModelScope.launch {
            recentlyDeletedSummary?.let { summary ->
                if (summary.id == summaryId) {
                    summaryRepository.saveSummary(summary)
                    recentlyDeletedSummary = null

                    // Analytics: Track undo
                    crashlyticsManager.logAction("Undo delete", "ID: $summaryId")
                }
            }
        }
    }

    fun deleteMultipleSummaries(summaryIds: Set<String>) {
        viewModelScope.launch {
            // Analytics: Track bulk deletion
            analyticsManager.logShare(
                method = "delete_multiple",
                contentType = "summary"
            )
            crashlyticsManager.logAction("Delete multiple summaries", "Count: ${summaryIds.size}")

            summaryIds.forEach { id ->
                summaryRepository.deleteSummary(id)
            }
            _uiState.update { it.copy(selectedItems = emptySet(), isSelectionMode = false) }
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            // Store all summaries for potential undo
            summaryRepository.getAllSummaries().first().let { summaries ->
                recentlyDeletedSummaries = summaries
            }

            // Analytics: Track history clear
            crashlyticsManager.logAction("Clear all history", "Action confirmed")

            summaryRepository.deleteAllSummaries()
        }
    }

    fun undoClearAll() {
        viewModelScope.launch {
            recentlyDeletedSummaries?.let { summaries ->
                summaries.forEach { summary ->
                    summaryRepository.saveSummary(summary)
                }
                recentlyDeletedSummaries = null

                // Analytics: Track undo
                crashlyticsManager.logAction("Undo clear all", "Count: ${summaries.size}")
            }
        }
    }

    fun toggleFavorite(summaryId: String) {
        viewModelScope.launch {
            summaryRepository.getSummaryById(summaryId)?.let { summary ->
                val updated = summary.copy(isFavorite = !summary.isFavorite)

                // Analytics: Track favorite toggle
                val action = if (updated.isFavorite) "add_favorite" else "remove_favorite"
                analyticsManager.logShare(
                    method = action,
                    contentType = "summary"
                )
                crashlyticsManager.logAction("Toggle favorite", "ID: $summaryId, Favorite: ${updated.isFavorite}")

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

    // Filter management methods (UC-007 AC-007.2)

    fun updateDateFilter(dateFilter: DateFilter, customStart: Long? = null, customEnd: Long? = null) {
        // Analytics: Track filter usage
        crashlyticsManager.logAction("Apply date filter", "Filter: ${dateFilter.displayName}")

        _filters.update {
            it.copy(
                dateFilter = dateFilter,
                customDateStart = customStart,
                customDateEnd = customEnd
            )
        }
    }

    fun togglePersonaFilter(persona: SummaryPersona) {
        // Analytics: Track persona filter
        crashlyticsManager.logAction("Toggle persona filter", "Persona: ${persona.name}")

        _filters.update { it.togglePersona(persona) }
    }

    fun toggleInputTypeFilter(type: InputType) {
        // Analytics: Track input type filter
        crashlyticsManager.logAction("Toggle input type filter", "Type: ${type.name}")

        _filters.update { it.toggleInputType(type) }
    }

    fun toggleFavoritesFilter() {
        // Analytics: Track favorites filter
        val newValue = !_filters.value.favoritesOnly
        crashlyticsManager.logAction("Toggle favorites filter", "Enabled: $newValue")

        _filters.update { it.copy(favoritesOnly = newValue) }
    }

    fun clearAllFilters() {
        // Analytics: Track filter clear
        crashlyticsManager.logAction("Clear all filters", "Active filters: ${_filters.value.activeFilterCount}")

        _filters.update { it.clearAll() }
    }

    fun showFilterBottomSheet() {
        _uiState.update { it.copy(showFilterBottomSheet = true) }
    }

    fun hideFilterBottomSheet() {
        _uiState.update { it.copy(showFilterBottomSheet = false) }
    }

    fun clearSearch() {
        _searchQuery.value = ""
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
    val filteredCount: Int = 0,
    val isSelectionMode: Boolean = false,
    val selectedItems: Set<String> = emptySet(),
    val error: String? = null,
    // Search & Filter (UC-007)
    val searchQuery: String = "",
    val filters: SummaryFilters = SummaryFilters(),
    val showFilterBottomSheet: Boolean = false
) {
    /**
     * Check if search or filter is active
     */
    val hasActiveSearchOrFilter: Boolean
        get() = searchQuery.isNotEmpty() || filters.hasActiveFilters

    /**
     * Get result count message for UI display (AC-007.4)
     */
    val resultCountMessage: String
        get() = when {
            isEmpty && hasActiveSearchOrFilter -> "No results found"
            isEmpty -> "No summaries yet"
            hasActiveSearchOrFilter -> "$filteredCount results"
            else -> "$totalCount summaries"
        }
}