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
    val textColor: String
)

object OnboardingData {
    val pages = listOf(
        OnboardingPage(
            id = 1,
            title = "✨ Welcome to SumUp",
            description = "Transform lengthy text into concise summaries powered by AI. Save time and get the key insights instantly.",
            icon = Icons.Default.AutoAwesome,
            backgroundColor = "#6366F1", // Indigo
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 2,
            title = "📝 Multiple Input Ways", 
            description = "Type text directly, upload PDF documents, or capture text with your camera. Choose what works best for you.",
            icon = Icons.Default.Input,
            backgroundColor = "#EC4899", // Pink
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 3,
            title = "📸 Smart OCR Camera",
            description = "Point your camera at any text - books, documents, signs. Our AI will extract and summarize it instantly.",
            icon = Icons.Default.CameraAlt,
            backgroundColor = "#10B981", // Emerald
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 4,
            title = "🎯 Multiple Personas",
            description = "Get summaries tailored to your needs - academic, business, casual, or detailed. Perfect for any context.",
            icon = Icons.Default.Psychology,
            backgroundColor = "#F59E0B", // Amber
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 5,
            title = "📚 Smart History",
            description = "All your summaries are saved automatically. Search, favorite, and organize your content with ease.",
            icon = Icons.Default.History,
            backgroundColor = "#8B5CF6", // Violet
            textColor = "#FFFFFF"
        ),
        OnboardingPage(
            id = 6,
            title = "🚀 Ready to Start?",
            description = "You're all set! Start summarizing text and discover how SumUp can boost your productivity.",
            icon = Icons.Default.RocketLaunch,
            backgroundColor = "#06B6D4", // Cyan
            textColor = "#FFFFFF"
        )
    )
}