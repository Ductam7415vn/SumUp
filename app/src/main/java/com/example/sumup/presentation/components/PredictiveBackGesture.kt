package com.example.sumup.presentation.components

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Predictive back gesture state
 */
enum class BackGestureState {
    IDLE,
    PREPARING,
    PROGRESSING,
    COMMITTING,
    CANCELLING
}

/**
 * Enhanced predictive back gesture handler with visual feedback
 */
@Composable
fun PredictiveBackGestureHandler(
    enabled: Boolean = true,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var gestureState by remember { mutableStateOf(BackGestureState.IDLE) }
    var gestureProgress by remember { mutableFloatStateOf(0f) }
    
    // Visual feedback animations
    val scaleProgress by animateFloatAsState(
        targetValue = when (gestureState) {
            BackGestureState.PROGRESSING -> 0.95f - (gestureProgress * 0.05f)
            BackGestureState.COMMITTING -> 0.85f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale_progress"
    )
    
    val alphaProgress by animateFloatAsState(
        targetValue = when (gestureState) {
            BackGestureState.PROGRESSING -> 1f - (gestureProgress * 0.2f)
            BackGestureState.COMMITTING -> 0.6f
            else -> 1f
        },
        animationSpec = tween(200),
        label = "alpha_progress"
    )
    
    val rotationProgress by animateFloatAsState(
        targetValue = when (gestureState) {
            BackGestureState.PROGRESSING -> gestureProgress * 3f
            BackGestureState.COMMITTING -> 5f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation_progress"
    )

    // Register back press callback
    DisposableEffect(enabled, dispatcher) {
        val callback = object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        
        dispatcher?.addCallback(callback)
        
        onDispose {
            callback.remove()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scaleProgress
                scaleY = scaleProgress
                alpha = alphaProgress
                rotationZ = rotationProgress
            }
    ) {
        content()
        
        // Back gesture indicator
        AnimatedVisibility(
            visible = gestureState == BackGestureState.PROGRESSING,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 1.2f),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            BackGestureIndicator(
                progress = gestureProgress,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/**
 * Visual indicator for back gesture progress
 */
@Composable
private fun BackGestureIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val indicatorAlpha by animateFloatAsState(
        targetValue = 0.8f + (progress * 0.2f),
        animationSpec = tween(100),
        label = "indicator_alpha"
    )
    
    val indicatorScale by animateFloatAsState(
        targetValue = 0.8f + (progress * 0.4f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "indicator_scale"
    )

    Card(
        modifier = modifier
            .scale(indicatorScale)
            .alpha(indicatorAlpha),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = (8 + progress * 4).dp
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Progress ring
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            // Back arrow icon
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        translationX = -progress * 8.dp.toPx()
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Enhanced back handler with confirmation for important actions
 */
@Composable
fun ConfirmationBackHandler(
    enabled: Boolean = true,
    requiresConfirmation: Boolean = false,
    confirmationMessage: String = "Are you sure you want to go back?",
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showConfirmation by remember { mutableStateOf(false) }
    
    PredictiveBackGestureHandler(
        enabled = enabled,
        onBackPressed = {
            if (requiresConfirmation && !showConfirmation) {
                showConfirmation = true
            } else {
                onBackPressed()
            }
        },
        modifier = modifier
    ) {
        content()
        
        if (showConfirmation) {
            AlertDialog(
                onDismissRequest = { showConfirmation = false },
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {
                    Text("Confirm Navigation")
                },
                text = {
                    Text(confirmationMessage)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmation = false
                            onBackPressed()
                        }
                    ) {
                        Text("Leave")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmation = false }
                    ) {
                        Text("Stay")
                    }
                }
            )
        }
    }
}

/**
 * Swipe back gesture detector for custom navigation
 */
@Composable
fun SwipeBackDetector(
    onSwipeBack: () -> Unit,
    threshold: Float = 0.3f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var dragProgress by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) 0.98f - (dragProgress * 0.02f) else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "drag_scale"
    )
    
    val dragAlpha by animateFloatAsState(
        targetValue = if (isDragging) 1f - (dragProgress * 0.1f) else 1f,
        animationSpec = tween(100),
        label = "drag_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = dragScale
                scaleY = dragScale
                alpha = dragAlpha
                translationX = if (isDragging) dragProgress * size.width * 0.1f else 0f
            }
    ) {
        content()
        
        // Edge swipe indicator
        AnimatedVisibility(
            visible = isDragging && dragProgress > 0.1f,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            SwipeBackIndicator(
                progress = dragProgress,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

/**
 * Swipe back gesture indicator
 */
@Composable
private fun SwipeBackIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.8f + (progress * 0.2f)
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = (4 + progress * 4).dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Swipe back",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        translationX = -progress * 4.dp.toPx()
                        alpha = 0.6f + (progress * 0.4f)
                    },
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Custom back animation for screen transitions
 */
@Composable
fun AnimatedBackTransition(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        modifier = modifier
    ) {
        content()
    }
}