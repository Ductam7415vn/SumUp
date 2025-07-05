package com.example.sumup.presentation.screens.settings.components

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ClearHistoryDialog(
    summaryCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isClearing: Boolean = false
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showContent = true
    }
    
    Dialog(
        onDismissRequest = {
            if (!isClearing) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isClearing,
            dismissOnClickOutside = !isClearing
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(300)) + 
                    scaleIn(initialScale = 0.8f, animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )),
            exit = fadeOut(animationSpec = tween(150)) + 
                   scaleOut(targetScale = 0.8f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Animated icon
                    AnimatedIcon(isClearing = isClearing)
                    
                    // Title
                    Text(
                        text = if (isClearing) "Clearing History..." else "Clear History?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    // Summary count info
                    if (!isClearing) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This will permanently delete $summaryCount ${if (summaryCount == 1) "summary" else "summaries"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Additional info
                        Text(
                            text = "Your settings and preferences will be preserved",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Progress indicator when clearing
                    if (isClearing) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                    }
                    
                    // Action buttons
                    if (!isClearing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cancel button
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel")
                            }
                            
                            // Confirm button
                            Button(
                                onClick = onConfirm,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Clear History")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedIcon(isClearing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_animation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isClearing) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isClearing) 2000 else 1,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "icon_rotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isClearing) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "icon_scale"
    )
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                if (isClearing) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isClearing) Icons.Default.HourglassEmpty else Icons.Default.DeleteSweep,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .scale(scale)
                .rotate(rotation),
            tint = if (isClearing) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}