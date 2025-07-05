package com.example.sumup.presentation.screens.main.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback

/**
 * Improved Feature Discovery Tooltip with progress indicator
 */
@Composable
fun ImprovedFeatureDiscoveryTooltip(
    isVisible: Boolean,
    title: String,
    description: String,
    currentIndex: Int = 0,
    totalTips: Int = 1,
    targetOffsetX: Int = 0,
    targetOffsetY: Int = 0,
    onDismiss: () -> Unit,
    onNextTip: (() -> Unit)? = null,
    onSkipAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticManager = rememberHapticFeedback()
    
    // Animation values
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "tooltip_alpha"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tooltip_scale"
    )
    
    // Pulse animation for attention
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    if (isVisible || animatedAlpha > 0.01f) {
        Popup(
            alignment = Alignment.TopStart,
            offset = IntOffset(targetOffsetX, targetOffsetY),
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = modifier
                    .widthIn(max = 280.dp)
                    .graphicsLayer {
                        alpha = animatedAlpha
                        scaleX = animatedScale * pulseScale
                        scaleY = animatedScale * pulseScale
                    }
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            
                            // Progress indicator
                            if (totalTips > 1) {
                                Text(
                                    text = "Tip ${currentIndex + 1} of $totalTips",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                        
                        IconButton(
                            onClick = {
                                hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                onDismiss()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Description
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                    
                    // Progress bar
                    if (totalTips > 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / totalTips },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp)),
                            color = MaterialTheme.colorScheme.onPrimary,
                            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                        )
                    }
                    
                    // Action buttons
                    if (onNextTip != null || totalTips > 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                    onSkipAll()
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            ) {
                                Text("Skip All")
                            }
                            
                            if (onNextTip != null && currentIndex < totalTips - 1) {
                                Button(
                                    onClick = {
                                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                        onNextTip()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onPrimary,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        "Next",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onPrimary,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        "Got it",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Arrow pointer
                Box(
                    modifier = Modifier
                        .offset(x = 20.dp, y = (-8).dp)
                        .size(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .graphicsLayer {
                            rotationZ = 45f
                        }
                )
            }
        }
    }
}