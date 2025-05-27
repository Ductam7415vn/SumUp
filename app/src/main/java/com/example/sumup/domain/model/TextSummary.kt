package com.example.sumup.domain.model

import java.time.LocalDateTime

data class TextSummary(
    val id: String,
    val originalText: String,
    val summary: String,
    val createdAt: LocalDateTime,
    val charactersSaved: Int = originalText.length - summary.length,
    val summaryRatio: Float = summary.length.toFloat() / originalText.length.toFloat()
)