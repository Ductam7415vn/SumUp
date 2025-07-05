package com.example.sumup.presentation.components.tooltip

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import com.example.sumup.domain.model.EnhancedFeatureTip
import com.example.sumup.domain.model.*
import com.example.sumup.utils.haptic.HapticFeedbackManager

/**
 * Data class for tooltip sequences
 */
data class TooltipSequence(
    val id: String,
    val name: String,
    val tips: List<EnhancedFeatureTip>,
    val category: SequenceCategory = SequenceCategory.GENERAL
)

/**
 * Tooltip section types for rich content
 */
sealed class TooltipSection {
    data class BulletPoints(val points: List<String>) : TooltipSection()
    data class Code(
        val code: String, 
        val language: String = "kotlin"
    ) : TooltipSection()
    data class Image(
        val resourceId: Int, 
        val description: String
    ) : TooltipSection()
}

/**
 * Interactive elements within tooltips
 */
sealed class InteractiveElement {
    data class Button(
        val label: String,
        val action: String,
        val icon: ImageVector? = null,
        val style: ButtonStyle = ButtonStyle.PRIMARY
    ) : InteractiveElement()
    
    data class Checkbox(
        val label: String,
        val action: String,
        val initialValue: Boolean = false
    ) : InteractiveElement()
    
    enum class ButtonStyle {
        PRIMARY,
        SECONDARY,
        TEXT
    }
}

/**
 * Tooltip position data
 */
data class TooltipPosition(
    val offset: IntOffset,
    val placement: TooltipPlacement,
    val arrowOffset: IntOffset,
    val arrowRotation: Float
) {
    enum class Preference {
        AUTO,
        ABOVE,
        BELOW,
        LEFT,
        RIGHT
    }
}

/**
 * Tooltip placement relative to target
 */
enum class TooltipPlacement {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    CENTER
}

/**
 * Extension to convert android Path to Compose Path
 */
fun androidx.compose.ui.graphics.Path.asComposePath(): androidx.compose.ui.graphics.Shape {
    return object : androidx.compose.ui.graphics.Shape {
        override fun createOutline(
            size: androidx.compose.ui.geometry.Size,
            layoutDirection: androidx.compose.ui.unit.LayoutDirection,
            density: androidx.compose.ui.unit.Density
        ): androidx.compose.ui.graphics.Outline {
            return androidx.compose.ui.graphics.Outline.Generic(this@asComposePath)
        }
    }
}