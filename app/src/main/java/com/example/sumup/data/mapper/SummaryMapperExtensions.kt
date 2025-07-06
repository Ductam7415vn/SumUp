package com.example.sumup.data.mapper

import com.example.sumup.data.local.entity.SummaryEntity
import com.example.sumup.domain.model.Summary

/**
 * Extension functions for mapping between Summary and SummaryEntity
 */
fun Summary.toEntity(): SummaryEntity {
    return SummaryEntity(
        id = id,
        originalText = originalText,
        summaryText = summary.ifEmpty { summaryText },
        bullets = bulletPoints,
        persona = persona.name,
        originalWordCount = metrics.originalWordCount,
        summaryWordCount = metrics.summaryWordCount,
        originalReadingTime = metrics.originalReadingTime,
        summaryReadingTime = metrics.summaryReadingTime,
        reductionPercent = metrics.reductionPercentage,
        confidence = confidence,
        createdAt = createdAt,
        isFavorite = isFavorite,
        // Multi-tier content
        briefOverview = briefOverview,
        detailedSummary = detailedSummary,
        keyInsights = keyInsights,
        actionItems = actionItems,
        keywords = keywords,
        // Streaming support
        isPartial = isPartial,
        processedSections = processedSections,
        totalSections = totalSections,
        processingStatus = processingStatus.name
    )
}

fun SummaryEntity.toSummary(): Summary {
    return Summary(
        id = id,
        originalText = originalText,
        summary = summaryText,
        bulletPoints = bullets,
        persona = try {
            com.example.sumup.domain.model.SummaryPersona.valueOf(persona)
        } catch (e: IllegalArgumentException) {
            com.example.sumup.domain.model.SummaryPersona.GENERAL
        },
        metrics = com.example.sumup.domain.model.SummaryMetrics(
            originalWordCount = originalWordCount,
            summaryWordCount = summaryWordCount,
            originalReadingTime = originalReadingTime,
            summaryReadingTime = summaryReadingTime,
            reductionPercentage = reductionPercent,
            confidenceScore = confidence
        ),
        createdAt = createdAt,
        isFavorite = isFavorite,
        confidence = confidence,
        // Multi-tier content
        briefOverview = briefOverview,
        detailedSummary = detailedSummary,
        keyInsights = keyInsights,
        actionItems = actionItems,
        keywords = keywords,
        // Streaming support
        isPartial = isPartial,
        processedSections = processedSections,
        totalSections = totalSections,
        processingStatus = try {
            com.example.sumup.domain.model.ProcessingStatus.valueOf(processingStatus)
        } catch (e: IllegalArgumentException) {
            com.example.sumup.domain.model.ProcessingStatus.COMPLETED
        }
    )
}