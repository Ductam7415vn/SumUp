package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback

@Composable
fun ImprovedCharacterLimitIndicator(
    currentLength: Int,
    maxLength: Int,
    modifier: Modifier = Modifier,
    showWarningThreshold: Float = 0.9f,
    showErrorThreshold: Float = 1.0f,
    text: String = ""
) {
    val hapticManager = rememberHapticFeedback()
    val ratio = currentLength.toFloat() / maxLength
    val remainingChars = maxLength - currentLength
    val wordCount = remember(text) {
        if (text.trim().isEmpty()) 0 else text.trim().split("\\s+".toRegex()).size
    }
    
    // Trigger haptic feedback when approaching limit
    LaunchedEffect(ratio) {
        when {
            ratio >= 1.0f -> hapticManager.performHapticFeedback(HapticFeedbackType.ERROR)
            ratio >= 0.95f -> hapticManager.performHapticFeedback(HapticFeedbackType.WARNING)
        }
    }
    
    val (backgroundColor, textColor, progressColor) = when {
        ratio >= showErrorThreshold -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.error
        )
        ratio >= showWarningThreshold -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.tertiary
        )
        else -> Triple(
            Color.Transparent,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.primary
        )
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Character count with animated background
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = backgroundColor,
                modifier = Modifier.animateContentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Warning icon when approaching limit
                    AnimatedVisibility(
                        visible = ratio >= showWarningThreshold,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = textColor
                        )
                    }
                    
                    Column {
                        Text(
                            text = "$currentLength / $maxLength",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (ratio >= showWarningThreshold) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (wordCount > 0) {
                                Text(
                                    text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textColor.copy(alpha = 0.7f)
                                )
                            }
                            // Show estimated API requests for large texts
                            if (currentLength >= 30_000) {
                                val estimatedRequests = when {
                                    currentLength < 30_000 -> 1
                                    currentLength < 100_000 -> "2-3"
                                    else -> "4-6"
                                }
                                Text(
                                    text = "• $estimatedRequests API ${if (estimatedRequests == "1" || estimatedRequests == 1) "request" else "requests"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    // Remaining characters when approaching limit
                    AnimatedVisibility(
                        visible = ratio >= showWarningThreshold && remainingChars > 0,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        Text(
                            text = "($remainingChars left)",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
        
        // Progress bar
        AnimatedVisibility(
            visible = ratio > 0.7f,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = ratio.coerceIn(0f, 1f),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "progress"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(progressColor)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
        
        // Warning message
        AnimatedVisibility(
            visible = ratio >= showErrorThreshold,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Character limit reached! Text will be truncated.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterLimitWarningDialog(
    show: Boolean,
    currentLength: Int,
    maxLength: Int,
    onDismiss: () -> Unit,
    onTruncate: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Character Limit Exceeded")
            },
            text = {
                Column {
                    Text(
                        "Your text has ${currentLength} characters, but the limit is ${maxLength}."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The excess ${currentLength - maxLength} characters will be removed if you continue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onTruncate,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Truncate Text")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}