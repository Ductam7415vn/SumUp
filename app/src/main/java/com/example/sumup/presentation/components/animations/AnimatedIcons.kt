package com.example.sumup.presentation.components.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * Animated favorite icon with heart beat effect
 */
@Composable
fun AnimatedFavoriteIcon(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = if (isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
) {
    val transition = updateTransition(targetState = isFavorite, label = "favorite")
    
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                keyframes {
                    durationMillis = 600
                    1f at 0
                    1.4f at 150
                    0.9f at 300
                    1.2f at 450
                    1f at 600
                }
            } else {
                tween(300)
            }
        },
        label = "favoriteScale"
    ) { favorite ->
        if (favorite) 1f else 0.8f
    }
    
    val rotation by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                keyframes {
                    durationMillis = 600
                    0f at 0
                    -15f at 150
                    15f at 300
                    -5f at 450
                    0f at 600
                }
            } else {
                tween(300)
            }
        },
        label = "favoriteRotation"
    ) { 0f }
    
    Icon(
        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = null,
        modifier = modifier
            .scale(scale)
            .rotate(rotation),
        tint = tint
    )
}

/**
 * Animated loading spinner with morphing shapes
 */
@Composable
fun MorphingLoadingIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "morphing")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "morphRotation"
    )
    
    val morphProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morphProgress"
    )
    
    Canvas(
        modifier = modifier
            .size(size)
            .rotate(rotation)
    ) {
        val radius = size.toPx() / 3
        val strokeWidth = size.toPx() / 12
        
        // Morph between square and circle
        val cornerRadius = radius * morphProgress
        
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = center.x - radius,
                y = center.y - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Animated check icon with draw-in effect
 */
@Composable
fun AnimatedCheckIcon(
    visible: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 24.dp
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(modifier = modifier.size(size)) {
            AnimatedCheckmark(
                isVisible = true,
                color = tint,
                size = size,
                strokeWidth = 2.dp
            )
        }
    }
}

/**
 * Animated scan icon for OCR
 */
@Composable
fun AnimatedScanIcon(
    isScanning: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(size * 0.7f),
            tint = tint
        )
        
        if (isScanning) {
            Canvas(modifier = Modifier.size(size)) {
                val lineY = size.toPx() * scanLineOffset
                
                drawLine(
                    color = tint.copy(alpha = 0.6f),
                    start = Offset(0f, lineY),
                    end = Offset(size.toPx(), lineY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                // Glow effect
                drawLine(
                    color = tint.copy(alpha = 0.3f),
                    start = Offset(0f, lineY),
                    end = Offset(size.toPx(), lineY),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Animated copy icon with success feedback
 */
@Composable
fun AnimatedCopyIcon(
    copied: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    val transition = updateTransition(targetState = copied, label = "copy")
    
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            } else {
                tween(300)
            }
        },
        label = "copyScale"
    ) { copied ->
        if (copied) 1.2f else 1f
    }
    
    val rotation by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                keyframes {
                    durationMillis = 500
                    0f at 0
                    180f at 250
                    360f at 500
                }
            } else {
                tween(300)
            }
        },
        label = "copyRotation"
    ) { 0f }
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .scale(scale)
                .graphicsLayer {
                    rotationY = if (copied) rotation else 0f
                },
            tint = if (copied) MaterialTheme.colorScheme.primary else tint
        )
    }
}

/**
 * Animated refresh icon with continuous rotation
 */
@Composable
fun AnimatedRefreshIcon(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refreshRotation"
    )
    
    Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = null,
        modifier = modifier
            .size(size)
            .rotate(if (isRefreshing) rotation else 0f),
        tint = tint
    )
}

/**
 * Animated arrow with direction morphing
 */
@Composable
fun AnimatedArrow(
    direction: ArrowDirection,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    val rotation by animateFloatAsState(
        targetValue = when (direction) {
            ArrowDirection.UP -> 0f
            ArrowDirection.RIGHT -> 90f
            ArrowDirection.DOWN -> 180f
            ArrowDirection.LEFT -> 270f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "arrowRotation"
    )
    
    Icon(
        imageVector = Icons.Default.KeyboardArrowUp,
        contentDescription = null,
        modifier = modifier
            .size(size)
            .rotate(rotation),
        tint = tint
    )
}

enum class ArrowDirection {
    UP, RIGHT, DOWN, LEFT
}

/**
 * Animated notification bell
 */
@Composable
fun AnimatedNotificationBell(
    hasNotification: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "notification")
    
    val shake by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0
                -15f at 100
                15f at 200
                -15f at 300
                15f at 400
                0f at 500
                0f at 2000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bellShake"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (hasNotification) Icons.Default.Notifications else Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .rotate(if (hasNotification) shake else 0f),
            tint = tint
        )
        
        if (hasNotification) {
            PulsingDot(
                color = MaterialTheme.colorScheme.error,
                size = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
            )
        }
    }
}