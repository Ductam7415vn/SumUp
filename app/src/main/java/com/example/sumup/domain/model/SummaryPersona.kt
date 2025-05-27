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
        description = "Key concepts and learning points",
        apiStyle = "educational"
    ),
    
    BUSINESS(
        displayName = "Business", 
        description = "Action items and insights",
        apiStyle = "actionable"
    ),
    
    LEGAL(
        displayName = "Legal",
        description = "Key terms and implications", 
        apiStyle = "precise"
    ),
    
    TECHNICAL(
        displayName = "Technical",
        description = "Technical details and specifications",
        apiStyle = "detailed"
    ),
    
    QUICK(
        displayName = "Quick Read",
        description = "Ultra-concise key points only",
        apiStyle = "minimal"
    );
    
    companion object {
        fun fromApiStyle(apiStyle: String): SummaryPersona {
            return values().find { it.apiStyle == apiStyle } ?: GENERAL
        }
    }
}
