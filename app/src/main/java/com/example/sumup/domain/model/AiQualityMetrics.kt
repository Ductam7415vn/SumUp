package com.example.sumup.domain.model

/**
 * AI-generated quality metrics for summary analysis
 */
data class AiQualityMetrics(
    // Content Quality (0-100)
    val coherenceScore: Int,           // How well the summary flows logically
    val contextPreservation: Int,      // How well original context is maintained
    val informationRetention: Int,     // Percentage of key info retained
    val accuracyScore: Int,            // How accurate the summary is
    
    // Readability Metrics
    val readabilityLevel: ReadabilityLevel,
    val averageSentenceLength: Int,    // Words per sentence
    val vocabularyComplexity: VocabularyLevel,
    val clarityScore: Int,             // 0-100
    
    // Content Analysis
    val informationDensity: DensityLevel,
    val topicCoverage: Int,            // Percentage of main topics covered
    val redundancyScore: Int,          // Lower is better (0-100)
    val focusScore: Int,               // How focused the summary is (0-100)
    
    // Structural Analysis
    val structureQuality: Int,         // 0-100
    val transitionQuality: Int,        // How smooth transitions are
    val hierarchyScore: Int,           // How well organized the info is
    
    // Confidence Metrics
    val overallConfidence: Float,      // AI's confidence in the summary
    val processingDifficulty: DifficultyLevel,
    val contentSuitability: SuitabilityLevel
)

enum class ReadabilityLevel {
    ELEMENTARY,    // Grade 1-6
    MIDDLE_SCHOOL, // Grade 7-9
    HIGH_SCHOOL,   // Grade 10-12
    COLLEGE,       // Undergraduate
    GRADUATE,      // Post-graduate
    PROFESSIONAL   // Expert level
}

enum class VocabularyLevel {
    SIMPLE,        // Common everyday words
    MODERATE,      // Some advanced vocabulary
    ADVANCED,      // Many complex terms
    TECHNICAL      // Specialized jargon
}

enum class DensityLevel {
    SPARSE,        // Very spread out information
    LIGHT,         // Easy to digest
    MODERATE,      // Balanced density
    DENSE,         // Packed with information
    VERY_DENSE     // Extremely information-rich
}

enum class DifficultyLevel {
    TRIVIAL,       // Very easy to process
    EASY,          // Simple content
    MODERATE,      // Average difficulty
    CHALLENGING,   // Complex content
    EXPERT         // Extremely difficult
}

enum class SuitabilityLevel {
    EXCELLENT,     // Perfect for summarization
    GOOD,          // Well-suited
    FAIR,          // Acceptable
    POOR           // Not ideal for summarization
}