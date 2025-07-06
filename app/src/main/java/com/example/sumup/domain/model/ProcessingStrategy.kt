package com.example.sumup.domain.model

/**
 * Defines different strategies for processing text/documents
 */
enum class ProcessingStrategy {
    /**
     * Single API request - fastest, most cost-effective
     * Used for texts < 30,000 characters
     */
    SINGLE,
    
    /**
     * Dual processing - balanced approach
     * Splits text into 2 parts for texts 30,000-100,000 characters
     */
    DUAL,
    
    /**
     * Multi-section processing - comprehensive analysis
     * Used for very large texts > 100,000 characters
     * Or when user explicitly chooses deep analysis
     */
    MULTI
}

/**
 * Represents a processing option that users can choose
 */
data class ProcessingOption(
    val strategy: ProcessingStrategy,
    val title: String,
    val description: String,
    val estimatedRequests: Int,
    val benefits: List<String>,
    val drawbacks: List<String>,
    val isRecommended: Boolean = false
)

/**
 * Configuration for adaptive processing
 */
data class ProcessingConfig(
    val strategy: ProcessingStrategy,
    val maxSectionSize: Int,
    val targetSections: Int,
    val preserveOutputRatio: Boolean = true,
    val finalOutputPercentage: Float = 0.10f // Default 10%
)

/**
 * Result from processing method selection
 */
sealed class ProcessingMethodResult {
    data class Selected(val strategy: ProcessingStrategy) : ProcessingMethodResult()
    object Cancelled : ProcessingMethodResult()
}