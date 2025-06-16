package com.example.sumup.presentation.components.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Enhanced FAB with morphing transition between FAB and Extended FAB
 */
@Composable
fun EnhancedFABTransition(
    isFAB: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
    fabContent: @Composable () -> Unit,
    buttonContent: @Composable RowScope.() -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val transition = updateTransition(targetState = isFAB, label = "fabTransition")
    
    val scale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "fabScale"
    ) { 1f }
    
    val rotation by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                keyframes {
                    durationMillis = 500
                    0f at 0
                    360f at 500
                }
            } else {
                keyframes {
                    durationMillis = 500
                    0f at 0
                    -360f at 500
                }
            }
        },
        label = "fabRotation"
    ) { 0f }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isFAB,
            transitionSpec = {
                if (targetState) {
                    scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() togetherWith
                    scaleOut(targetScale = 0.7f) + fadeOut()
                } else {
                    scaleIn(
                        initialScale = 0.7f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() togetherWith
                    scaleOut() + fadeOut()
                }
            },
            label = "fabContent"
        ) { isFab ->
            if (isFab) {
                AnimatedFab(
                    onClick = {
                        hapticFeedback.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                        onClick()
                    },
                    containerColor = containerColor,
                    contentColor = contentColor,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                ) {
                    fabContent()
                }
            } else {
                ExtendedFloatingActionButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                        onClick()
                    },
                    containerColor = containerColor,
                    contentColor = contentColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .scale(scale)
                        .graphicsLayer {
                            rotationZ = -rotation
                        }
                ) {
                    buttonContent()
                }
            }
        }
        
        // Add sparkle effect on transition
        if (transition.isRunning) {
            LaunchedEffect(Unit) {
                // Trigger sparkle effect
            }
        }
    }
}

/**
 * Animated Add FAB with plus icon rotation
 */
@Composable
fun AnimatedAddFab(
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "addRotation"
    )
    
    AnimatedFab(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add",
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            }
        )
    }
}