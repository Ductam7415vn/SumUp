package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Calculate AI quality metrics for a summary
 * Uses various NLP-like heuristics to analyze summary quality
 */
@Singleton
class CalculateAiMetricsUseCase @Inject constructor() {
    
    operator fun invoke(summary: Summary): AiQualityMetrics {
        val originalWords = summary.originalText.split("\\s+".toRegex())
        val summaryWords = summary.summary.split("\\s+".toRegex())
        val sentences = summary.summary.split("[.!?]".toRegex()).filter { it.isNotBlank() }
        
        return AiQualityMetrics(
            // Content Quality
            coherenceScore = calculateCoherenceScore(summary),
            contextPreservation = calculateContextPreservation(summary),
            informationRetention = calculateInformationRetention(summary),
            accuracyScore = calculateAccuracyScore(summary),
            
            // Readability
            readabilityLevel = calculateReadabilityLevel(sentences, summaryWords),
            averageSentenceLength = if (sentences.isNotEmpty()) summaryWords.size / sentences.size else 0,
            vocabularyComplexity = calculateVocabularyComplexity(summaryWords),
            clarityScore = calculateClarityScore(summary),
            
            // Content Analysis
            informationDensity = calculateDensityLevel(summary),
            topicCoverage = calculateTopicCoverage(summary),
            redundancyScore = calculateRedundancy(summary),
            focusScore = calculateFocusScore(summary),
            
            // Structural Analysis
            structureQuality = calculateStructureQuality(summary),
            transitionQuality = calculateTransitionQuality(summary),
            hierarchyScore = calculateHierarchyScore(summary),
            
            // Confidence
            overallConfidence = calculateOverallConfidence(summary),
            processingDifficulty = calculateProcessingDifficulty(summary),
            contentSuitability = calculateContentSuitability(summary)
        )
    }
    
    private fun calculateCoherenceScore(summary: Summary): Int {
        // Check logical flow between bullet points
        var score = 70 // Base score
        
        // Bonus for having brief, standard, and detailed versions
        if (summary.briefOverview != null) score += 5
        if (summary.detailedSummary != null) score += 5
        
        // Check bullet point consistency
        val avgBulletLength = summary.bulletPoints.map { it.length }.average()
        val variance = summary.bulletPoints.map { abs(it.length - avgBulletLength) }.average()
        
        // Lower variance = more consistent = higher score
        score += (20 - (variance / 10).coerceAtMost(20.0)).toInt()
        
        return score.coerceIn(0, 100)
    }
    
    private fun calculateContextPreservation(summary: Summary): Int {
        // Check how many key terms from original appear in summary
        val originalWords = summary.originalText.lowercase().split("\\s+".toRegex())
            .filter { it.length > 4 } // Only meaningful words
            .toSet()
        
        val summaryWords = (summary.summary + summary.bulletPoints.joinToString(" "))
            .lowercase().split("\\s+".toRegex()).toSet()
        
        val preserved = originalWords.intersect(summaryWords).size
        val preservationRate = if (originalWords.isNotEmpty()) {
            (preserved.toFloat() / originalWords.size * 100).toInt()
        } else 50
        
        return preservationRate.coerceIn(0, 100)
    }
    
    private fun calculateInformationRetention(summary: Summary): Int {
        // Based on compression ratio and bullet points
        val compressionRatio = summary.metrics.reductionPercentage
        
        return when {
            compressionRatio < 50 -> 95 // Kept most info
            compressionRatio < 70 -> 85 // Good retention
            compressionRatio < 85 -> 75 // Moderate retention
            else -> 65 // High compression, less retention
        }
    }
    
    private fun calculateAccuracyScore(summary: Summary): Int {
        // Based on confidence and processing metrics
        val baseScore = (summary.confidence * 100).toInt()
        
        // Adjust based on persona appropriateness
        val personaBonus = when (summary.persona) {
            SummaryPersona.ACADEMIC -> 5 // More accurate
            SummaryPersona.SIMPLE -> -5 // May lose nuance
            else -> 0
        }
        
        return (baseScore + personaBonus).coerceIn(0, 100)
    }
    
    private fun calculateReadabilityLevel(sentences: List<String>, words: List<String>): ReadabilityLevel {
        if (sentences.isEmpty() || words.isEmpty()) return ReadabilityLevel.MIDDLE_SCHOOL
        
        val avgSentenceLength = words.size.toFloat() / sentences.size
        val avgWordLength = words.map { it.length }.average()
        
        // Simple Flesch-Kincaid approximation
        val score = 206.835 - 1.015 * avgSentenceLength - 84.6 * (avgWordLength / 5)
        
        return when {
            score >= 90 -> ReadabilityLevel.ELEMENTARY
            score >= 80 -> ReadabilityLevel.MIDDLE_SCHOOL
            score >= 70 -> ReadabilityLevel.HIGH_SCHOOL
            score >= 60 -> ReadabilityLevel.COLLEGE
            score >= 50 -> ReadabilityLevel.GRADUATE
            else -> ReadabilityLevel.PROFESSIONAL
        }
    }
    
    private fun calculateVocabularyComplexity(words: List<String>): VocabularyLevel {
        val avgWordLength = words.map { it.length }.average()
        val longWords = words.count { it.length > 7 }
        val complexityRatio = longWords.toFloat() / words.size
        
        return when {
            avgWordLength < 4.5 && complexityRatio < 0.1 -> VocabularyLevel.SIMPLE
            avgWordLength < 5.5 && complexityRatio < 0.2 -> VocabularyLevel.MODERATE
            avgWordLength < 6.5 && complexityRatio < 0.3 -> VocabularyLevel.ADVANCED
            else -> VocabularyLevel.TECHNICAL
        }
    }
    
    private fun calculateClarityScore(summary: Summary): Int {
        var score = 80 // Base score
        
        // Check for clear structure
        if (summary.briefOverview != null) score += 5
        if (summary.bulletPoints.size in 3..7) score += 10 // Optimal number
        if (summary.keywords?.isNotEmpty() == true) score += 5
        
        return score.coerceIn(0, 100)
    }
    
    private fun calculateDensityLevel(summary: Summary): DensityLevel {
        val bulletDensity = summary.bulletPoints.size.toFloat() / 
            (summary.summary.split(" ").size / 100f)
        
        return when {
            bulletDensity < 2 -> DensityLevel.SPARSE
            bulletDensity < 4 -> DensityLevel.LIGHT
            bulletDensity < 6 -> DensityLevel.MODERATE
            bulletDensity < 8 -> DensityLevel.DENSE
            else -> DensityLevel.VERY_DENSE
        }
    }
    
    private fun calculateTopicCoverage(summary: Summary): Int {
        // Estimate based on keywords and bullet points
        val keywordCount = summary.keywords?.size ?: 0
        val bulletCount = summary.bulletPoints.size
        
        return when {
            keywordCount >= 5 && bulletCount >= 5 -> 90
            keywordCount >= 3 && bulletCount >= 3 -> 75
            keywordCount >= 1 && bulletCount >= 1 -> 60
            else -> 40
        }
    }
    
    private fun calculateRedundancy(summary: Summary): Int {
        // Check for repeated phrases
        val allText = (listOf(summary.summary) + summary.bulletPoints).joinToString(" ")
        val words = allText.split("\\s+".toRegex())
        val uniqueWords = words.toSet()
        
        val redundancyRatio = 1 - (uniqueWords.size.toFloat() / words.size)
        return (redundancyRatio * 100).toInt().coerceIn(0, 100)
    }
    
    private fun calculateFocusScore(summary: Summary): Int {
        // Check topic consistency across bullet points
        val keywords = summary.keywords ?: return 70
        if (keywords.isEmpty()) return 70
        
        var score = 60
        val bulletText = summary.bulletPoints.joinToString(" ").lowercase()
        
        // Check how many keywords appear in bullets
        val keywordMatches = keywords.count { keyword ->
            bulletText.contains(keyword.lowercase())
        }
        
        score += (keywordMatches.toFloat() / keywords.size * 40).toInt()
        return score.coerceIn(0, 100)
    }
    
    private fun calculateStructureQuality(summary: Summary): Int {
        var score = 70
        
        // Check for presence of structure elements
        if (summary.briefOverview != null) score += 10
        if (summary.detailedSummary != null) score += 10
        if (summary.keyInsights?.isNotEmpty() == true) score += 5
        if (summary.actionItems?.isNotEmpty() == true) score += 5
        
        return score.coerceIn(0, 100)
    }
    
    private fun calculateTransitionQuality(summary: Summary): Int {
        // Simple heuristic based on bullet point structure
        if (summary.bulletPoints.size < 2) return 50
        
        // Check if bullets start with transition words
        val transitionWords = setOf("however", "therefore", "additionally", "furthermore", 
                                   "moreover", "consequently", "thus", "hence")
        
        val transitionsFound = summary.bulletPoints.count { bullet ->
            transitionWords.any { bullet.lowercase().startsWith(it) }
        }
        
        return 70 + (transitionsFound * 10).coerceAtMost(30)
    }
    
    private fun calculateHierarchyScore(summary: Summary): Int {
        // Check information hierarchy
        return when {
            summary.briefOverview != null && 
            summary.detailedSummary != null && 
            summary.bulletPoints.isNotEmpty() -> 95
            
            summary.briefOverview != null && 
            summary.bulletPoints.isNotEmpty() -> 80
            
            summary.bulletPoints.isNotEmpty() -> 65
            
            else -> 40
        }
    }
    
    private fun calculateOverallConfidence(summary: Summary): Float {
        // Combine various metrics
        val structureScore = calculateStructureQuality(summary)
        val clarityScore = calculateClarityScore(summary)
        val accuracyScore = calculateAccuracyScore(summary)
        
        val avgScore = (structureScore + clarityScore + accuracyScore) / 3f
        return (avgScore / 100f).coerceIn(0f, 1f)
    }
    
    private fun calculateProcessingDifficulty(summary: Summary): DifficultyLevel {
        val textLength = summary.originalText.length
        val complexity = calculateVocabularyComplexity(
            summary.originalText.split("\\s+".toRegex())
        )
        
        return when {
            textLength < 500 && complexity == VocabularyLevel.SIMPLE -> DifficultyLevel.TRIVIAL
            textLength < 2000 && complexity in setOf(VocabularyLevel.SIMPLE, VocabularyLevel.MODERATE) -> DifficultyLevel.EASY
            textLength < 5000 && complexity == VocabularyLevel.MODERATE -> DifficultyLevel.MODERATE
            textLength < 10000 || complexity == VocabularyLevel.ADVANCED -> DifficultyLevel.CHALLENGING
            else -> DifficultyLevel.EXPERT
        }
    }
    
    private fun calculateContentSuitability(summary: Summary): SuitabilityLevel {
        val hasGoodStructure = summary.bulletPoints.size in 3..10
        val hasReasonableLength = summary.originalText.length in 100..30000
        val hasKeywords = !summary.keywords.isNullOrEmpty()
        
        val score = listOf(hasGoodStructure, hasReasonableLength, hasKeywords).count { it }
        
        return when (score) {
            3 -> SuitabilityLevel.EXCELLENT
            2 -> SuitabilityLevel.GOOD
            1 -> SuitabilityLevel.FAIR
            else -> SuitabilityLevel.POOR
        }
    }
}