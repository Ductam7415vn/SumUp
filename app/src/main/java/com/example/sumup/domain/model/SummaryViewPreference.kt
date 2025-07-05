package com.example.sumup.domain.model

/**
 * User preference for default summary view in Result Screen
 */
enum class SummaryViewPreference(
    val displayName: String,
    val description: String,
    val tabIndex: Int
) {
    BRIEF(
        displayName = "Brief",
        description = "Quick overview - perfect for busy schedules",
        tabIndex = 0
    ),
    STANDARD(
        displayName = "Standard", 
        description = "Balanced summary - ideal for most content",
        tabIndex = 1
    ),
    DETAILED(
        displayName = "Detailed",
        description = "In-depth analysis - great for research",
        tabIndex = 2
    );
    
    companion object {
        fun fromTabIndex(index: Int): SummaryViewPreference {
            return values().find { it.tabIndex == index } ?: STANDARD
        }
        
        fun fromString(value: String): SummaryViewPreference {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                STANDARD
            }
        }
    }
}