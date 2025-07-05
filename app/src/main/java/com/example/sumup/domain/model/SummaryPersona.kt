package com.example.sumup.domain.model

/**
 * Different persona types for summarization styles
 */
enum class SummaryPersona(
    val displayName: String,
    val description: String,
    val apiStyle: String
) {
    GENERAL(
        displayName = "General",
        description = "Balanced summary for general use",
        apiStyle = "balanced"
    ),
    
    STUDY(
        displayName = "Study",
        description = "Study notes with key concepts",
        apiStyle = "educational"
    ),
    
    PROFESSIONAL(
        displayName = "Professional", 
        description = "Business-focused with action items",
        apiStyle = "actionable"
    ),
    
    ACADEMIC(
        displayName = "Academic",
        description = "Scholarly with citations preserved", 
        apiStyle = "precise"
    ),
    
    SIMPLE(
        displayName = "Simple",
        description = "Easy to understand, plain language",
        apiStyle = "simplified"
    );
    
    companion object {
        fun fromApiStyle(apiStyle: String): SummaryPersona {
            return values().find { it.apiStyle == apiStyle } ?: GENERAL
        }
    }
}
