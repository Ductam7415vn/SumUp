package com.example.sumup.presentation.screens.ocr.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOcrReviewDialog(
    detectedText: String,
    confidence: Float,
    onDismiss: () -> Unit,
    onRetake: () -> Unit,
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var editedText by remember { mutableStateOf(detectedText) }
    var isEditing by remember { mutableStateOf(false) }
    
    val wordCount = editedText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    val charCount = editedText.length
    
    // Animated values
    val confidenceProgress by animateFloatAsState(
        targetValue = confidence,
        animationSpec = spring(dampingRatio = 0.6f)
    )
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = 0.8f)
            ),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(200)
            )
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with confidence indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 Review Text",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Confidence and stats card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                confidence > 0.8f -> MaterialTheme.colorScheme.primaryContainer
                                confidence > 0.6f -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.errorContainer
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Detection Quality",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = when {
                                            confidence > 0.8f -> "Excellent"
                                            confidence > 0.6f -> "Good"
                                            confidence > 0.3f -> "Fair"
                                            else -> "Poor"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${(confidenceProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    LinearProgressIndicator(
                                        progress = { confidenceProgress },
                                        modifier = Modifier.width(80.dp),
                                        color = when {
                                            confidence > 0.8f -> MaterialTheme.colorScheme.primary
                                            confidence > 0.6f -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$wordCount words",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$charCount characters",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Text content with edit functionality
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            label = { Text("Edit detected text") },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            SelectionContainer {
                                Text(
                                    text = editedText.ifEmpty { "No text detected" },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (editedText.isEmpty()) 
                                        MaterialTheme.colorScheme.onSurfaceVariant 
                                    else MaterialTheme.colorScheme.onSurface,
                                    textAlign = if (editedText.isEmpty()) TextAlign.Center else TextAlign.Start
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Edit button
                    TextButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Done editing" else "Edit text",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEditing) "Done Editing" else "Edit Text")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRetake,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Retake",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retake")
                        }
                        
                        Button(
                            onClick = { 
                                onContinue(editedText)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = editedText.isNotBlank(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Continue",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue")
                        }
                    }
                    
                    // Tip for better results
                    if (confidence < 0.6f) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = "Tip",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "💡 Try better lighting or hold the camera steady for clearer text detection",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}