package com.example.sumup.data.mapper

import com.example.sumup.data.local.entity.SummaryEntity
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryMetrics
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.model.ProcessingStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryMapper @Inject constructor() {
    
    fun entityToDomain(entity: SummaryEntity): Summary {
        return Summary(
            id = entity.id,
            originalText = entity.originalText,
            summary = entity.summaryText,
            bulletPoints = entity.bullets,
            persona = try {
                SummaryPersona.valueOf(entity.persona)
            } catch (e: IllegalArgumentException) {
                SummaryPersona.GENERAL
            },
            metrics = SummaryMetrics(
                originalWordCount = entity.originalWordCount,
                summaryWordCount = entity.summaryWordCount,
                originalReadingTime = entity.originalReadingTime,
                summaryReadingTime = entity.summaryReadingTime,
                reductionPercentage = entity.reductionPercent,
                confidenceScore = entity.confidence
            ),
            createdAt = entity.createdAt,
            isFavorite = entity.isFavorite,
            confidence = entity.confidence,
            // Multi-tier content
            briefOverview = entity.briefOverview,
            detailedSummary = entity.detailedSummary,
            keyInsights = entity.keyInsights,
            actionItems = entity.actionItems,
            keywords = entity.keywords,
            // Streaming support
            isPartial = entity.isPartial,
            processedSections = entity.processedSections,
            totalSections = entity.totalSections,
            processingStatus = try {
                ProcessingStatus.valueOf(entity.processingStatus)
            } catch (e: IllegalArgumentException) {
                ProcessingStatus.COMPLETED
            }
        )
    }
    
    fun domainToEntity(domain: Summary): SummaryEntity {
        return SummaryEntity(
            id = domain.id,
            originalText = domain.originalText,
            summaryText = domain.summary.ifEmpty { domain.summaryText },
            bullets = domain.bulletPoints,
            persona = domain.persona.name,
            originalWordCount = domain.metrics.originalWordCount,
            summaryWordCount = domain.metrics.summaryWordCount,
            originalReadingTime = domain.metrics.originalReadingTime,
            summaryReadingTime = domain.metrics.summaryReadingTime,
            reductionPercent = domain.metrics.reductionPercentage,
            confidence = domain.confidence,
            createdAt = domain.createdAt,
            isFavorite = domain.isFavorite,
            // Multi-tier content
            briefOverview = domain.briefOverview,
            detailedSummary = domain.detailedSummary,
            keyInsights = domain.keyInsights,
            actionItems = domain.actionItems,
            keywords = domain.keywords,
            // Streaming support
            isPartial = domain.isPartial,
            processedSections = domain.processedSections,
            totalSections = domain.totalSections,
            processingStatus = domain.processingStatus.name
        )
    }
}