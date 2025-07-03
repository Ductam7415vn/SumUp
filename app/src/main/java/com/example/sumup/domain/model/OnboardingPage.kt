package com.example.sumup.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPage(
    val id: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val lottieAnimation: String? = null,
    val backgroundColor: String,
    val textColor: String,
    val action: OnboardingAction? = null,
    val actionButtonText: String? = null
)

enum class OnboardingAction {
    REQUEST_API_KEY,
    GRANT_PERMISSIONS,
    SHOW_FEATURES
}

object OnboardingData {
    val pages = listOf(
        OnboardingPage(
            id = 1,
            title = "Welcome to SumUp",
            description = "Transform lengthy text into concise summaries powered by AI. Save time and get the key insights instantly.",
            icon = Icons.Default.AutoAwesome,
            backgroundColor = "#6366F1", // Indigo
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 2,
            title = "Set Up AI Engine",
            description = "To use real AI summaries, you'll need a Gemini API key. It's free and takes just a minute to set up.",
            icon = Icons.Default.Key,
            backgroundColor = "#EC4899", // Pink
            textColor = "#FFFFFF",
            action = OnboardingAction.REQUEST_API_KEY,
            actionButtonText = "Get API Key"
        ),
        OnboardingPage(
            id = 3,
            title = "Multiple Input Methods",
            description = "Type text directly, upload PDF documents, or capture text with your camera. Choose what works best for you.",
            icon = Icons.Default.Input,
            backgroundColor = "#10B981", // Emerald
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 4,
            title = "Smart Summaries",
            description = "Get summaries tailored to your needs - brief key points, standard overview, or detailed analysis.",
            icon = Icons.Default.Psychology,
            backgroundColor = "#F59E0B", // Amber
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 5,
            title = "Ready to Start?",
            description = "You're all set! Start summarizing text and discover how SumUp can boost your productivity.",
            icon = Icons.Default.RocketLaunch,
            backgroundColor = "#06B6D4", // Cyan
            textColor = "#FFFFFF"
        )
    )
}