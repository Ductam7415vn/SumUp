package com.example.sumup.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sumup.data.local.converter.StringListConverter
import java.util.UUID

@Entity(tableName = "summaries")
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
    val isFavorite: Boolean = false
)