package com.example.sumup.domain.model

import java.time.LocalDateTime

data class SummaryHistory(
    val summaries: List<TextSummary>,
    val totalSummaries: Int,
    val totalCharactersSaved: Int,
    val averageSummaryRatio: Float
)