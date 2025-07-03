package com.example.sumup.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AchievementType {
    FIRST_SUMMARY,
    SPEED_READER,
    POWER_USER,
    PDF_MASTER,
    OCR_WIZARD,
    STREAK_KEEPER,
    EXPORT_EXPERT,
    PERSONA_EXPLORER,
    TIME_SAVER,
    WORD_CRUNCHER
}

data class Achievement(
    val id: String,
    val type: AchievementType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val requirement: Int,
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val tier: AchievementTier = AchievementTier.BRONZE
)

enum class AchievementTier(val multiplier: Int, val color: Color) {
    BRONZE(1, Color(0xFFCD7F32)),
    SILVER(5, Color(0xFFC0C0C0)),
    GOLD(10, Color(0xFFFFD700)),
    PLATINUM(25, Color(0xFFE5E4E2))
}

object AchievementDefinitions {
    fun getAllAchievements(): List<Achievement> = listOf(
        // First steps
        Achievement(
            id = "first_summary",
            type = AchievementType.FIRST_SUMMARY,
            title = "First Steps",
            description = "Create your first summary",
            icon = Icons.Outlined.Star,
            color = Color(0xFF4CAF50),
            requirement = 1
        ),
        
        // Usage achievements
        Achievement(
            id = "speed_reader_bronze",
            type = AchievementType.SPEED_READER,
            title = "Speed Reader",
            description = "Summarize 10 texts",
            icon = Icons.Outlined.Speed,
            color = Color(0xFF2196F3),
            requirement = 10,
            tier = AchievementTier.BRONZE
        ),
        Achievement(
            id = "speed_reader_silver",
            type = AchievementType.SPEED_READER,
            title = "Speed Reader Pro",
            description = "Summarize 50 texts",
            icon = Icons.Outlined.Speed,
            color = Color(0xFF2196F3),
            requirement = 50,
            tier = AchievementTier.SILVER
        ),
        Achievement(
            id = "speed_reader_gold",
            type = AchievementType.SPEED_READER,
            title = "Speed Reader Master",
            description = "Summarize 100 texts",
            icon = Icons.Outlined.Speed,
            color = Color(0xFF2196F3),
            requirement = 100,
            tier = AchievementTier.GOLD
        ),
        
        // Feature usage
        Achievement(
            id = "pdf_master",
            type = AchievementType.PDF_MASTER,
            title = "PDF Master",
            description = "Process 10 PDF documents",
            icon = Icons.Outlined.PictureAsPdf,
            color = Color(0xFFE53935),
            requirement = 10
        ),
        Achievement(
            id = "ocr_wizard",
            type = AchievementType.OCR_WIZARD,
            title = "OCR Wizard",
            description = "Capture 10 texts with camera",
            icon = Icons.Outlined.CameraAlt,
            color = Color(0xFF9C27B0),
            requirement = 10
        ),
        
        // Consistency
        Achievement(
            id = "streak_keeper_bronze",
            type = AchievementType.STREAK_KEEPER,
            title = "Consistent User",
            description = "Use app for 7 days straight",
            icon = Icons.Outlined.TrendingUp,
            color = Color(0xFFFF9800),
            requirement = 7,
            tier = AchievementTier.BRONZE
        ),
        Achievement(
            id = "streak_keeper_silver",
            type = AchievementType.STREAK_KEEPER,
            title = "Dedicated User",
            description = "Use app for 30 days straight",
            icon = Icons.Outlined.TrendingUp,
            color = Color(0xFFFF9800),
            requirement = 30,
            tier = AchievementTier.SILVER
        ),
        
        // Export achievements
        Achievement(
            id = "export_expert",
            type = AchievementType.EXPORT_EXPERT,
            title = "Export Expert",
            description = "Export 10 summaries",
            icon = Icons.Outlined.Download,
            color = Color(0xFF00BCD4),
            requirement = 10
        ),
        
        // Exploration
        Achievement(
            id = "persona_explorer",
            type = AchievementType.PERSONA_EXPLORER,
            title = "Persona Explorer",
            description = "Try all 6 personas",
            icon = Icons.Outlined.People,
            color = Color(0xFF673AB7),
            requirement = 6
        ),
        
        // Impact
        Achievement(
            id = "time_saver_bronze",
            type = AchievementType.TIME_SAVER,
            title = "Time Saver",
            description = "Save 60 minutes of reading",
            icon = Icons.Outlined.Timer,
            color = Color(0xFF009688),
            requirement = 60,
            tier = AchievementTier.BRONZE
        ),
        Achievement(
            id = "time_saver_silver",
            type = AchievementType.TIME_SAVER,
            title = "Time Optimizer",
            description = "Save 5 hours of reading",
            icon = Icons.Outlined.Timer,
            color = Color(0xFF009688),
            requirement = 300,
            tier = AchievementTier.SILVER
        ),
        Achievement(
            id = "time_saver_gold",
            type = AchievementType.TIME_SAVER,
            title = "Time Master",
            description = "Save 24 hours of reading",
            icon = Icons.Outlined.Timer,
            color = Color(0xFF009688),
            requirement = 1440,
            tier = AchievementTier.GOLD
        ),
        
        // Word processing
        Achievement(
            id = "word_cruncher_bronze",
            type = AchievementType.WORD_CRUNCHER,
            title = "Word Cruncher",
            description = "Process 10,000 words",
            icon = Icons.Outlined.TextFields,
            color = Color(0xFF795548),
            requirement = 10000,
            tier = AchievementTier.BRONZE
        ),
        Achievement(
            id = "word_cruncher_silver",
            type = AchievementType.WORD_CRUNCHER,
            title = "Word Processor",
            description = "Process 100,000 words",
            icon = Icons.Outlined.TextFields,
            color = Color(0xFF795548),
            requirement = 100000,
            tier = AchievementTier.SILVER
        ),
        Achievement(
            id = "word_cruncher_gold",
            type = AchievementType.WORD_CRUNCHER,
            title = "Word Master",
            description = "Process 1,000,000 words",
            icon = Icons.Outlined.TextFields,
            color = Color(0xFF795548),
            requirement = 1000000,
            tier = AchievementTier.GOLD
        )
    )
    
    fun getAchievementsByType(type: AchievementType): List<Achievement> {
        return getAllAchievements().filter { it.type == type }
    }
    
    fun getNextTierAchievement(currentAchievement: Achievement): Achievement? {
        val sameTypeAchievements = getAchievementsByType(currentAchievement.type)
            .sortedBy { it.tier.multiplier }
        
        val currentIndex = sameTypeAchievements.indexOf(currentAchievement)
        return if (currentIndex < sameTypeAchievements.size - 1) {
            sameTypeAchievements[currentIndex + 1]
        } else null
    }
}