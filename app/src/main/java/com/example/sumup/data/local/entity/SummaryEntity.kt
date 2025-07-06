package com.example.sumup.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sumup.data.local.converter.StringListConverter
import java.util.UUID

@Entity(
    tableName = "summaries",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["isFavorite"]),
        Index(value = ["persona"])
    ]
)
@TypeConverters(StringListConverter::class)
data class SummaryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val summaryText: String,
    val bullets: List<String>,
    val persona: String,
    val originalWordCount: Int,
    val summaryWordCount: Int,
    val originalReadingTime: Int,
    val summaryReadingTime: Int,
    val reductionPercent: Int,
    val confidence: Float,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    // Multi-tier content fields
    val briefOverview: String? = null,
    val detailedSummary: String? = null,
    val keyInsights: List<String>? = null,
    val actionItems: List<String>? = null,
    val keywords: List<String>? = null,
    // Streaming and partial results support
    val isPartial: Boolean = false,
    val processedSections: Int = 0,
    val totalSections: Int = 0,
    val processingStatus: String = "COMPLETED"
)