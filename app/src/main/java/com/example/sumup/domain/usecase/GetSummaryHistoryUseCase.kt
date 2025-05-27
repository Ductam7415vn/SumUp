package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.HistoryItem
import com.example.sumup.domain.model.HistorySection
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSummaryHistoryUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    operator fun invoke(): Flow<List<HistorySection>> =
        repository.getAllSummaries().map { summaries ->
            summaries.groupBy { summary ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = summary.createdAt
                when {
                    isToday(calendar) -> "TODAY"
                    isYesterday(calendar) -> "YESTERDAY"
                    isThisWeek(calendar) -> "THIS WEEK"
                    else -> SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        .format(Date(summary.createdAt))
                }
            }.map { (sectionTitle, summaries) ->
                HistorySection(
                    title = sectionTitle,
                    items = summaries.map { summary ->
                        HistoryItem(
                            id = summary.id,
                            title = generateTitle(summary.originalText),
                            preview = summary.bulletPoints.firstOrNull() ?: summary.summaryText,
                            timestamp = formatTimestamp(summary.createdAt),
                            wordReduction = "${summary.metrics.originalWordCount}→${summary.metrics.summaryWordCount} words",
                            createdAt = summary.createdAt
                        )
                    }
                )
            }.sortedByDescending { section ->
                section.items.maxOfOrNull { it.createdAt } ?: 0L
            }
        }
    private fun isToday(calendar: Calendar): Boolean {
        val today = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(calendar: Calendar): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(calendar: Calendar): Boolean {
        val weekAgo = Calendar.getInstance()
        weekAgo.add(Calendar.WEEK_OF_YEAR, -1)
        return calendar.timeInMillis > weekAgo.timeInMillis
    }

    private fun generateTitle(text: String): String {
        return text.take(50).let { truncated ->
            if (text.length > 50) "$truncated..." else truncated
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}