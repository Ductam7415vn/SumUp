package com.example.sumup.data.mapper

import com.example.sumup.data.local.entity.SummaryEntity
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryMetrics
import com.example.sumup.domain.model.SummaryPersona
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryMapper @Inject constructor() {
    
    fun entityToDomain(entity: SummaryEntity): Summary {
        return Summary(
            id = entity.id,
            originalText = entity.originalText,
            bulletPoints = entity.bullets,
            persona = SummaryPersona.valueOf(entity.persona),
            metrics = SummaryMetrics(
                originalWordCount = entity.originalWordCount,
                summaryWordCount = entity.summaryWordCount,
                originalReadingTime = entity.originalReadingTime,
                summaryReadingTime = entity.summaryReadingTime,
                reductionPercentage = entity.reductionPercent,
                confidenceScore = entity.confidence
            ),
            createdAt = entity.createdAt,
            isFavorite = entity.isFavorite
        )
    }
    
    fun domainToEntity(domain: Summary): SummaryEntity {
        return SummaryEntity(
            id = domain.id,
            originalText = domain.originalText,
            summaryText = domain.summaryText,
            bullets = domain.bulletPoints,
            persona = domain.persona.name,
            originalWordCount = domain.metrics.originalWordCount,
            summaryWordCount = domain.metrics.summaryWordCount,
            originalReadingTime = domain.metrics.originalReadingTime,
            summaryReadingTime = domain.metrics.summaryReadingTime,
            reductionPercent = domain.metrics.reductionPercentage,
            confidence = domain.metrics.confidenceScore,
            createdAt = domain.createdAt,
            isFavorite = domain.isFavorite
        )
    }
}