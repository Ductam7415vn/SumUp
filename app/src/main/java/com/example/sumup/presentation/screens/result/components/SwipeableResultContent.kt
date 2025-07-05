package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Wrapper component that adds swipe gestures to content
 */
@Composable
fun SwipeableResultContent(
    onSwipeBack: () -> Unit = {},
    onSwipeToHistory: () -> Unit = {},
    swipeEnabled: Boolean = true,
    hapticFeedback: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    
    // Swipe state
    var offsetX by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val swipeThreshold = with(density) { 100.dp.toPx() }
    
    // Animation
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "swipe_offset"
    )
    
    val draggableState = rememberDraggableState { delta ->
        if (swipeEnabled) {
            offsetX += delta
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = swipeEnabled,
                onDragStarted = { 
                    isDragging = true 
                },
                onDragStopped = { velocity ->
                    isDragging = false
                    when {
                        // Swipe right to go back
                        offsetX > swipeThreshold || velocity > 300f -> {
                            hapticFeedback()
                            offsetX = 0f
                            onSwipeBack()
                        }
                        // Swipe left to go to history
                        offsetX < -swipeThreshold || velocity < -300f -> {
                            hapticFeedback()
                            offsetX = 0f
                            onSwipeToHistory()
                        }
                        // Return to center
                        else -> {
                            coroutineScope.launch {
                                animate(
                                    initialValue = offsetX,
                                    targetValue = 0f,
                                    animationSpec = spring()
                                ) { value, _ ->
                                    offsetX = value
                                }
                            }
                        }
                    }
                }
            )
            .graphicsLayer {
                translationX = animatedOffset
                
                // Add slight rotation based on swipe
                rotationY = (animatedOffset / 10f).coerceIn(-15f, 15f)
                
                // Scale down slightly when swiping
                val scale = 1f - (abs(animatedOffset) / 2000f).coerceIn(0f, 0.1f)
                scaleX = scale
                scaleY = scale
                
                // Add shadow when swiping
                shadowElevation = (abs(animatedOffset) / 20f).coerceIn(0f, 16f)
            }
    ) {
        content()
        
        // Swipe indicators
        if (abs(offsetX) > 20f) {
            SwipeIndicators(
                offsetX = offsetX,
                threshold = swipeThreshold
            )
        }
    }
}

@Composable
private fun BoxScope.SwipeIndicators(
    offsetX: Float,
    threshold: Float
) {
    val progress = (abs(offsetX) / threshold).coerceIn(0f, 1f)
    val isSwipingRight = offsetX > 0
    
    // Left indicator (back)
    if (isSwipingRight) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .graphicsLayer {
                    alpha = progress
                    scaleX = progress
                    scaleY = progress
                }
        ) {
            SwipeHint(
                text = "Back",
                icon = "←"
            )
        }
    }
    
    // Right indicator (history)
    if (!isSwipingRight && offsetX < 0) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .graphicsLayer {
                    alpha = progress
                    scaleX = progress
                    scaleY = progress
                }
        ) {
            SwipeHint(
                text = "History",
                icon = "→"
            )
        }
    }
}

@Composable
private fun SwipeHint(
    text: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        androidx.compose.material3.Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        androidx.compose.material3.Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}