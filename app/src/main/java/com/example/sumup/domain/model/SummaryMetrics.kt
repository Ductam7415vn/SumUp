package com.example.sumup.domain.model

/**
 * Metrics displayed in success header
 * Based on UI wireframes showing reduction %, time saved, word count
 */
data class SummaryMetrics(
    val originalWordCount: Int,
    val summaryWordCount: Int,
    val reductionPercentage: Int,
    val originalReadingTime: Int, // in minutes
    val summaryReadingTime: Int,  // in minutes
    val confidenceScore: Float = 0.95f  // confidence in summary quality
) {
    companion object {
        /**
         * Calculate metrics from original and summary text
         * Uses 200 words per minute reading speed
         */
        fun calculate(originalText: String, summaryText: String): SummaryMetrics {
            val originalWords = originalText.split("\\s+".toRegex()).size
            val summaryWords = summaryText.split("\\s+".toRegex()).size
            
            // Average reading speed: 200 words per minute
            val originalTime = (originalWords / 200.0).toInt().coerceAtLeast(1)
            val summaryTime = (summaryWords / 200.0).toInt().coerceAtLeast(1)
            
            val reduction = ((originalWords - summaryWords) * 100 / originalWords).coerceIn(0, 100)
            val timeSaved = (originalTime - summaryTime).coerceAtLeast(0)
            
            return SummaryMetrics(
                originalWordCount = originalWords,
                summaryWordCount = summaryWords,
                reductionPercentage = reduction,
                originalReadingTime = originalTime,
                summaryReadingTime = summaryTime,
                confidenceScore = 0.95f
            )
        }
    }
}
