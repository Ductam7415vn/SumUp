package com.example.sumup.presentation.screens.history

import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.presentation.screens.main.MainUiState.InputType

/**
 * Filter models for UC-007: Search and Filter
 *
 * Supports filtering by:
 * - Date range (Today, Yesterday, Last 7/30 days, Custom)
 * - Persona (General, Student, Professional, etc.)
 * - Input type (TEXT, PDF, DOCX, OCR)
 * - Favorites only
 */

/**
 * Date filter options
 */
enum class DateFilter(val displayName: String) {
    ALL("All Time"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    CUSTOM("Custom Range");

    companion object {
        fun getFilterRange(filter: DateFilter, customStart: Long? = null, customEnd: Long? = null): LongRange? {
            val now = System.currentTimeMillis()
            val today = getStartOfDay(now)

            return when (filter) {
                ALL -> null // No filter
                TODAY -> today..now
                YESTERDAY -> {
                    val yesterdayStart = today - 86400000L // 24 hours
                    yesterdayStart until today
                }
                LAST_7_DAYS -> {
                    val sevenDaysAgo = today - (7 * 86400000L)
                    sevenDaysAgo..now
                }
                LAST_30_DAYS -> {
                    val thirtyDaysAgo = today - (30 * 86400000L)
                    thirtyDaysAgo..now
                }
                CUSTOM -> {
                    if (customStart != null && customEnd != null) {
                        customStart..customEnd
                    } else null
                }
            }
        }

        private fun getStartOfDay(timestamp: Long): Long {
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = timestamp
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
    }
}

/**
 * Active filters state
 */
data class SummaryFilters(
    val dateFilter: DateFilter = DateFilter.ALL,
    val customDateStart: Long? = null,
    val customDateEnd: Long? = null,
    val selectedPersonas: Set<SummaryPersona> = emptySet(),
    val selectedInputTypes: Set<InputType> = emptySet(),
    val favoritesOnly: Boolean = false
) {
    /**
     * Check if any filters are active
     */
    val hasActiveFilters: Boolean
        get() = dateFilter != DateFilter.ALL ||
                selectedPersonas.isNotEmpty() ||
                selectedInputTypes.isNotEmpty() ||
                favoritesOnly

    /**
     * Get count of active filters
     */
    val activeFilterCount: Int
        get() {
            var count = 0
            if (dateFilter != DateFilter.ALL) count++
            if (selectedPersonas.isNotEmpty()) count++
            if (selectedInputTypes.isNotEmpty()) count++
            if (favoritesOnly) count++
            return count
        }

    /**
     * Clear all filters
     */
    fun clearAll(): SummaryFilters {
        return SummaryFilters()
    }

    /**
     * Toggle persona filter
     */
    fun togglePersona(persona: SummaryPersona): SummaryFilters {
        return copy(
            selectedPersonas = if (persona in selectedPersonas) {
                selectedPersonas - persona
            } else {
                selectedPersonas + persona
            }
        )
    }

    /**
     * Toggle input type filter
     */
    fun toggleInputType(type: InputType): SummaryFilters {
        return copy(
            selectedInputTypes = if (type in selectedInputTypes) {
                selectedInputTypes - type
            } else {
                selectedInputTypes + type
            }
        )
    }
}

/**
 * Filter chip for UI display
 */
data class FilterChip(
    val label: String,
    val onRemove: () -> Unit
)
