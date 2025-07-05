package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.ui.theme.extendedColorScheme
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import kotlinx.coroutines.delay

/**
 * Beautiful success animation overlay
 */
@Composable
fun SuccessFeedbackOverlay(
    isVisible: Boolean,
    message: String = "Success!",
    icon: ImageVector = Icons.Default.CheckCircle,
    duration: Long = 2000L,
    onDismiss: () -> Unit = {}
) {
    val hapticManager = rememberHapticFeedback()
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 0.9f)
    ) {
        LaunchedEffect(Unit) {
            hapticManager.performHapticFeedback(HapticFeedbackType.SUCCESS)
            delay(duration)
            onDismiss()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .padding(32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated success icon
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background circle with pulse animation
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.9f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_scale"
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(scale)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.extendedColorScheme.successContainer
                                )
                        )
                        
                        // Icon with check animation
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.extendedColorScheme.onSuccessContainer
                        )
                    }
                    
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Inline success message for forms
 */
@Composable
fun InlineSuccessMessage(
    message: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColorScheme.successContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.extendedColorScheme.success
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColorScheme.onSuccessContainer
                )
            }
        }
    }
}

/**
 * Toast-style success notification
 */
@Composable
fun SuccessToast(
    message: String,
    isVisible: Boolean,
    position: Alignment = Alignment.BottomCenter,
    duration: Long = 2000L,
    onDismiss: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = when (position) {
            Alignment.TopCenter -> slideInVertically { -it } + fadeIn()
            Alignment.BottomCenter -> slideInVertically { it } + fadeIn()
            else -> fadeIn()
        },
        exit = when (position) {
            Alignment.TopCenter -> slideOutVertically { -it } + fadeOut()
            Alignment.BottomCenter -> slideOutVertically { it } + fadeOut()
            else -> fadeOut()
        }
    ) {
        LaunchedEffect(Unit) {
            delay(duration)
            onDismiss()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = position
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColorScheme.success,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Animated checkmark for completion
 */
@Composable
fun AnimatedCheckmark(
    isVisible: Boolean,
    size: Int = 24,
    color: Color = MaterialTheme.extendedColorScheme.success
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn()
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier
                .size(size.dp)
                .graphicsLayer {
                    val bounce = spring<Float>(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                    scaleX = 1.2f
                    scaleY = 1.2f
                },
            tint = color
        )
    }
}