package com.example.sumup.presentation.components.tooltip.positioning

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Engine for calculating optimal tooltip positions dynamically
 */
object DynamicPositioningEngine {
    
    // Minimum margin from screen edges
    private const val SCREEN_EDGE_MARGIN = 16
    
    // Preferred distance from target element
    private const val PREFERRED_DISTANCE = 8
    
    /**
     * Calculate optimal position for tooltip relative to target element
     */
    fun calculateOptimalPosition(
        targetBounds: Rect,
        screenSize: IntSize,
        tooltipSize: IntSize,
        preferredPosition: TooltipPosition.Preference = TooltipPosition.Preference.AUTO
    ): TooltipPosition {
        // Try positions in order of preference
        val positions = when (preferredPosition) {
            TooltipPosition.Preference.AUTO -> listOf(
                PositionCandidate.BOTTOM,
                PositionCandidate.TOP,
                PositionCandidate.RIGHT,
                PositionCandidate.LEFT
            )
            TooltipPosition.Preference.ABOVE -> listOf(
                PositionCandidate.TOP,
                PositionCandidate.BOTTOM,
                PositionCandidate.RIGHT,
                PositionCandidate.LEFT
            )
            TooltipPosition.Preference.BELOW -> listOf(
                PositionCandidate.BOTTOM,
                PositionCandidate.TOP,
                PositionCandidate.RIGHT,
                PositionCandidate.LEFT
            )
            TooltipPosition.Preference.LEFT -> listOf(
                PositionCandidate.LEFT,
                PositionCandidate.RIGHT,
                PositionCandidate.TOP,
                PositionCandidate.BOTTOM
            )
            TooltipPosition.Preference.RIGHT -> listOf(
                PositionCandidate.RIGHT,
                PositionCandidate.LEFT,
                PositionCandidate.TOP,
                PositionCandidate.BOTTOM
            )
        }
        
        // Find best position
        for (candidate in positions) {
            val position = calculatePosition(targetBounds, tooltipSize, candidate)
            if (isPositionValid(position, screenSize, tooltipSize)) {
                return position
            }
        }
        
        // Fallback: center on screen with arrow pointing to target
        return calculateFallbackPosition(targetBounds, screenSize, tooltipSize)
    }
    
    /**
     * Calculate position for specific placement
     */
    private fun calculatePosition(
        targetBounds: Rect,
        tooltipSize: IntSize,
        placement: PositionCandidate
    ): TooltipPosition {
        return when (placement) {
            PositionCandidate.TOP -> {
                val x = (targetBounds.left + targetBounds.right - tooltipSize.width) / 2
                val y = targetBounds.top - tooltipSize.height - PREFERRED_DISTANCE
                
                TooltipPosition(
                    offset = IntOffset(x.toInt(), y.toInt()),
                    placement = TooltipPlacement.TOP,
                    arrowOffset = IntOffset(
                        (tooltipSize.width / 2 - 8).toInt(),
                        tooltipSize.height
                    ),
                    arrowRotation = 180f
                )
            }
            PositionCandidate.BOTTOM -> {
                val x = (targetBounds.left + targetBounds.right - tooltipSize.width) / 2
                val y = targetBounds.bottom + PREFERRED_DISTANCE
                
                TooltipPosition(
                    offset = IntOffset(x.toInt(), y.toInt()),
                    placement = TooltipPlacement.BOTTOM,
                    arrowOffset = IntOffset(
                        (tooltipSize.width / 2 - 8).toInt(),
                        -16
                    ),
                    arrowRotation = 0f
                )
            }
            PositionCandidate.LEFT -> {
                val x = targetBounds.left - tooltipSize.width - PREFERRED_DISTANCE
                val y = (targetBounds.top + targetBounds.bottom - tooltipSize.height) / 2
                
                TooltipPosition(
                    offset = IntOffset(x.toInt(), y.toInt()),
                    placement = TooltipPlacement.LEFT,
                    arrowOffset = IntOffset(
                        tooltipSize.width,
                        (tooltipSize.height / 2 - 8).toInt()
                    ),
                    arrowRotation = 90f
                )
            }
            PositionCandidate.RIGHT -> {
                val x = targetBounds.right + PREFERRED_DISTANCE
                val y = (targetBounds.top + targetBounds.bottom - tooltipSize.height) / 2
                
                TooltipPosition(
                    offset = IntOffset(x.toInt(), y.toInt()),
                    placement = TooltipPlacement.RIGHT,
                    arrowOffset = IntOffset(
                        -16,
                        (tooltipSize.height / 2 - 8).toInt()
                    ),
                    arrowRotation = -90f
                )
            }
        }
    }
    
    /**
     * Check if position is valid (within screen bounds)
     */
    private fun isPositionValid(
        position: TooltipPosition,
        screenSize: IntSize,
        tooltipSize: IntSize
    ): Boolean {
        val left = position.offset.x
        val top = position.offset.y
        val right = left + tooltipSize.width
        val bottom = top + tooltipSize.height
        
        return left >= SCREEN_EDGE_MARGIN &&
                top >= SCREEN_EDGE_MARGIN &&
                right <= screenSize.width - SCREEN_EDGE_MARGIN &&
                bottom <= screenSize.height - SCREEN_EDGE_MARGIN
    }
    
    /**
     * Calculate fallback position when no ideal position works
     */
    private fun calculateFallbackPosition(
        targetBounds: Rect,
        screenSize: IntSize,
        tooltipSize: IntSize
    ): TooltipPosition {
        // Center tooltip on screen
        val x = (screenSize.width - tooltipSize.width) / 2
        val y = (screenSize.height - tooltipSize.height) / 2
        
        // Calculate arrow direction
        val targetCenterX = (targetBounds.left + targetBounds.right) / 2
        val targetCenterY = (targetBounds.top + targetBounds.bottom) / 2
        val tooltipCenterX = x + tooltipSize.width / 2
        val tooltipCenterY = y + tooltipSize.height / 2
        
        val angle = kotlin.math.atan2(
            (targetCenterY - tooltipCenterY).toDouble(),
            (targetCenterX - tooltipCenterX).toDouble()
        ) * 180 / Math.PI
        
        return TooltipPosition(
            offset = IntOffset(x, y),
            placement = TooltipPlacement.CENTER,
            arrowOffset = IntOffset.Zero, // No arrow for center placement
            arrowRotation = angle.toFloat()
        )
    }
    
    /**
     * Adjust position to avoid overlapping with other UI elements
     */
    fun adjustForCollisions(
        position: TooltipPosition,
        tooltipSize: IntSize,
        obstacles: List<Rect>
    ): TooltipPosition {
        var adjustedOffset = position.offset
        val tooltipRect = Rect(
            adjustedOffset.x.toFloat(),
            adjustedOffset.y.toFloat(),
            (adjustedOffset.x + tooltipSize.width).toFloat(),
            (adjustedOffset.y + tooltipSize.height).toFloat()
        )
        
        for (obstacle in obstacles) {
            if (tooltipRect.overlaps(obstacle)) {
                // Calculate push direction
                val overlapLeft = tooltipRect.right - obstacle.left
                val overlapRight = obstacle.right - tooltipRect.left
                val overlapTop = tooltipRect.bottom - obstacle.top
                val overlapBottom = obstacle.bottom - tooltipRect.top
                
                // Find minimum push distance
                val minOverlap = minOf(overlapLeft, overlapRight, overlapTop, overlapBottom)
                
                adjustedOffset = when (minOverlap) {
                    overlapLeft -> adjustedOffset.copy(x = (obstacle.left - tooltipSize.width - PREFERRED_DISTANCE).toInt())
                    overlapRight -> adjustedOffset.copy(x = (obstacle.right + PREFERRED_DISTANCE).toInt())
                    overlapTop -> adjustedOffset.copy(y = (obstacle.top - tooltipSize.height - PREFERRED_DISTANCE).toInt())
                    else -> adjustedOffset.copy(y = (obstacle.bottom + PREFERRED_DISTANCE).toInt())
                }
            }
        }
        
        return position.copy(offset = adjustedOffset)
    }
    
    /**
     * Calculate smooth animation path between positions
     */
    fun calculateAnimationPath(
        from: TooltipPosition,
        to: TooltipPosition,
        progress: Float
    ): IntOffset {
        val x = lerp(from.offset.x.toFloat(), to.offset.x.toFloat(), progress)
        val y = lerp(from.offset.y.toFloat(), to.offset.y.toFloat(), progress)
        return IntOffset(x.toInt(), y.toInt())
    }
    
    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }
}

/**
 * Data class representing tooltip position
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
 * Internal position candidates
 */
private enum class PositionCandidate {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}