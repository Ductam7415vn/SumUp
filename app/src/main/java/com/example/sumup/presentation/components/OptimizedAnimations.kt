package com.example.sumup.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.sumup.ui.theme.Dimensions

/**
 * Optimized animation utilities to reduce recomposition
 */

/**
 * Conditional animation that only runs when enabled
 */
@Composable
fun ConditionalAnimation(
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    if (enabled) {
        content()
    } else {
        // Static content without animation
        Box { content() }
    }
}

/**
 * Pulse animation that can be paused
 */
@Composable
fun PausablePulseAnimation(
    enabled: Boolean = true,
    minScale: Float = 0.9f,
    maxScale: Float = 1.1f,
    duration: Int = 1000,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    
    LaunchedEffect(enabled) {
        if (enabled) {
            animate(
                initialValue = minScale,
                targetValue = maxScale,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration),
                    repeatMode = RepeatMode.Reverse
                )
            ) { value, _ ->
                scale = value
            }
        } else {
            scale = 1f
        }
    }
    
    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

/**
 * Optimized infinite rotation
 */
@Composable
fun OptimizedRotation(
    enabled: Boolean = true,
    duration: Int = 2000,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val rotation = if (enabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation_value"
        )
    } else {
        remember { mutableStateOf(0f) }
    }
    
    Box(
        modifier = modifier.graphicsLayer {
            rotationZ = rotation.value
        }
    ) {
        content()
    }
}

/**
 * Delayed visibility animation
 */
@Composable
fun DelayedVisibility(
    visible: Boolean,
    delay: Long = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var shouldShow by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible) {
            if (delay > 0) {
                kotlinx.coroutines.delay(delay)
            }
            shouldShow = true
        } else {
            shouldShow = false
        }
    }
    
    androidx.compose.animation.AnimatedVisibility(
        visible = shouldShow,
        modifier = modifier,
        enter = androidx.compose.animation.fadeIn(
            animationSpec = tween(Dimensions.animationMedium)
        ) + androidx.compose.animation.expandVertically(
            animationSpec = tween(Dimensions.animationMedium)
        ),
        exit = androidx.compose.animation.fadeOut(
            animationSpec = tween(Dimensions.animationShort)
        ) + androidx.compose.animation.shrinkVertically(
            animationSpec = tween(Dimensions.animationShort)
        )
    ) {
        content()
    }
}

/**
 * Shimmer effect that can be disabled
 */
@Composable
fun ConditionalShimmer(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (enabled) {
        ShimmerEffect(modifier = modifier) {
            content()
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }
}

/**
 * Performance-optimized shimmer
 */
@Composable
private fun ShimmerEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    Box(
        modifier = modifier.graphicsLayer {
            // Shimmer effect implementation
            alpha = 0.7f + (0.3f * translateAnim)
        }
    ) {
        content()
    }
}

/**
 * Staggered animation for lists
 */
@Composable
fun StaggeredAnimationScope(
    itemCount: Int,
    content: @Composable (index: Int, delay: Int) -> Unit
) {
    for (index in 0 until itemCount) {
        val delay = index * 50 // 50ms stagger
        content(index, delay)
    }
}

/**
 * Animation settings based on user preferences
 */
@Composable
fun rememberAnimationSettings(): AnimationSettings {
    // In the future, this could read from user preferences
    return remember {
        AnimationSettings(
            enableAnimations = true,
            reducedMotion = false,
            animationSpeed = 1f
        )
    }
}

data class AnimationSettings(
    val enableAnimations: Boolean,
    val reducedMotion: Boolean,
    val animationSpeed: Float
)