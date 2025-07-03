package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * Pull to refresh wrapper for ResultScreen
 */
@Composable
fun PullToRefreshResult(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    
    // Pull state
    var pullOffset by remember { mutableStateOf(0f) }
    var isActivated by remember { mutableStateOf(false) }
    val pullThreshold = with(density) { 80.dp.toPx() }
    val maxPull = with(density) { 120.dp.toPx() }
    
    // Animation
    val animatedPullOffset by animateFloatAsState(
        targetValue = if (isRefreshing) pullThreshold else pullOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pull_offset"
    )
    
    // Handle refresh completion
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && pullOffset > 0) {
            pullOffset = 0f
            isActivated = false
        }
    }
    
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.Drag && available.y < 0 && pullOffset > 0) {
                    // Consume upward scroll when pulled
                    val consumed = min(pullOffset, -available.y)
                    pullOffset -= consumed
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }
            
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (source == NestedScrollSource.Drag && available.y > 0 && !isRefreshing) {
                    // Pull down
                    val pull = available.y * 0.5f // Damping factor
                    pullOffset = (pullOffset + pull).coerceAtMost(maxPull)
                    
                    // Check activation
                    if (pullOffset >= pullThreshold && !isActivated) {
                        isActivated = true
                        hapticFeedback()
                    } else if (pullOffset < pullThreshold && isActivated) {
                        isActivated = false
                    }
                    
                    return Offset(0f, pull)
                }
                return Offset.Zero
            }
            
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (isActivated && !isRefreshing) {
                    onRefresh()
                    hapticFeedback()
                } else if (!isRefreshing) {
                    coroutineScope.launch {
                        animate(
                            initialValue = pullOffset,
                            targetValue = 0f,
                            animationSpec = spring()
                        ) { value, _ ->
                            pullOffset = value
                        }
                    }
                }
                return Velocity.Zero
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // Content with offset
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = animatedPullOffset
                }
        ) {
            content()
        }
        
        // Pull indicator
        if (animatedPullOffset > 0 || isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { animatedPullOffset.toDp() })
                    .align(Alignment.TopCenter)
            ) {
                PullIndicator(
                    pullOffset = animatedPullOffset,
                    threshold = pullThreshold,
                    isRefreshing = isRefreshing,
                    isActivated = isActivated,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun PullIndicator(
    pullOffset: Float,
    threshold: Float,
    isRefreshing: Boolean,
    isActivated: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = (pullOffset / threshold).coerceIn(0f, 1.5f)
    
    val infiniteTransition = rememberInfiniteTransition(label = "refresh_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_rotation"
    )
    
    Canvas(
        modifier = modifier.size(40.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 3
        
        if (isRefreshing) {
            // Spinning circle when refreshing
            drawRefreshingIndicator(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                rotation = rotation
            )
        } else {
            // Pull progress indicator
            drawPullIndicator(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                progress = progress,
                isActivated = isActivated
            )
        }
    }
}

private fun DrawScope.drawPullIndicator(
    centerX: Float,
    centerY: Float,
    radius: Float,
    progress: Float,
    isActivated: Boolean
) {
    val color = if (isActivated) Color(0xFF4CAF50) else Color(0xFF2196F3)
    val strokeWidth = 3.dp.toPx()
    
    // Background circle
    drawCircle(
        color = color.copy(alpha = 0.2f),
        radius = radius,
        center = Offset(centerX, centerY),
        style = Stroke(strokeWidth)
    )
    
    // Progress arc
    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = 360f * progress,
        useCenter = false,
        topLeft = Offset(centerX - radius, centerY - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(strokeWidth, cap = StrokeCap.Round)
    )
    
    // Center icon
    if (progress > 0.5f) {
        val arrowProgress = ((progress - 0.5f) * 2f).coerceIn(0f, 1f)
        drawArrow(
            centerX = centerX,
            centerY = centerY,
            size = radius * 0.8f * arrowProgress,
            color = color,
            rotation = if (isActivated) 180f else 0f
        )
    }
}

private fun DrawScope.drawRefreshingIndicator(
    centerX: Float,
    centerY: Float,
    radius: Float,
    rotation: Float
) {
    rotate(rotation, Offset(centerX, centerY)) {
        // Gradient spinning arc
        val gradient = Brush.sweepGradient(
            colors = listOf(
                Color(0xFF2196F3),
                Color(0xFF4CAF50),
                Color(0xFF2196F3)
            ),
            center = Offset(centerX, centerY)
        )
        
        drawArc(
            brush = gradient,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawArrow(
    centerX: Float,
    centerY: Float,
    size: Float,
    color: Color,
    rotation: Float
) {
    rotate(rotation, Offset(centerX, centerY)) {
        val path = Path().apply {
            moveTo(centerX, centerY - size / 2)
            lineTo(centerX - size / 3, centerY + size / 4)
            lineTo(centerX, centerY)
            lineTo(centerX + size / 3, centerY + size / 4)
            close()
        }
        
        drawPath(
            path = path,
            color = color
        )
    }
}