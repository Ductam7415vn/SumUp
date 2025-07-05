package com.example.sumup.domain.model

/**
 * App state for tooltip triggers
 */
data class AppState(
    val currentScreen: String = "",
    val summaryCount: Int = 0,
    val lastPdfSize: Long = 0,
    val textLength: Int = 0,
    val hasApiKey: Boolean = false,
    val userLevel: UserLevel = UserLevel.BEGINNER
)

/**
 * User experience level
 */
enum class UserLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

/**
 * User action for tracking
 */
data class UserAction(
    val type: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Tooltip sequence categories
 */
enum class SequenceCategory {
    ONBOARDING,
    GENERAL,
    ADVANCED,
    CONTEXTUAL
}