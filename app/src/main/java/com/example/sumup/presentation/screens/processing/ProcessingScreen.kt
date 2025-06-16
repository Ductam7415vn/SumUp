package com.example.sumup.presentation.screens.processing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.ProcessingState
import com.example.sumup.presentation.components.AnimatedProcessingIcon
import com.example.sumup.presentation.components.ProgressMorph
import com.example.sumup.presentation.components.SharedElementKeys
import com.example.sumup.presentation.components.ContainerTransform
import com.example.sumup.presentation.components.ConfirmationBackHandler
import com.example.sumup.presentation.components.animations.LoadingDots
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    onCancel: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var message by remember { mutableStateOf("Reading your text...") }
    var timeoutLevel by remember { mutableIntStateOf(0) }
    var showTimeoutMessage by remember { mutableStateOf(false) }
    
    // Simulate progress
    LaunchedEffect(Unit) {
        // Initial phase: Reading text (0-30%)
        progress = 0.15f
        message = "Reading your text..."
        delay(1000)
        
        // Understanding context (30-50%)
        progress = 0.3f
        message = "Understanding context..."
        delay(1500)
        
        progress = 0.5f
        delay(1000)
        
        // Creating summary (50-85%)
        progress = 0.65f
        message = "Creating summary..."
        delay(1500)
        
        progress = 0.85f
        delay(1000)
        
        // Almost done (85-95%)
        progress = 0.95f
        message = "Almost ready..."
        delay(500)
        
        // Complete
        progress = 1f
        message = "Done!"
        delay(200)
        onComplete()
    }
    
    // Timeout management
    LaunchedEffect(Unit) {
        delay(5000)
        timeoutLevel = 1
        showTimeoutMessage = true
        
        delay(5000) // 10s total
        timeoutLevel = 2
        
        delay(5000) // 15s total
        timeoutLevel = 3
    }
    
    ConfirmationBackHandler(
        enabled = true,
        requiresConfirmation = progress > 0.5f, // Require confirmation if processing is more than 50% complete
        confirmationMessage = "Processing is in progress. Are you sure you want to cancel?",
        onBackPressed = onCancel
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ContainerTransform(
                visible = true,
                modifier = Modifier.fillMaxSize()
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            // Animated Logo with shared element key
            AnimatedProcessingIcon(
                modifier = Modifier
                    .size(120.dp)
                    .testTag(SharedElementKeys.PROCESSING_AI_ICON)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress message with loading dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (progress < 1f) {
                    LoadingDots(
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Enhanced progress bar with morphing
            ProgressMorph(
                isIndeterminate = progress < 0.1f,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .testTag(SharedElementKeys.PROCESSING_PROGRESS)
            )
            
            // Show percentage
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Timeout messages
            AnimatedVisibility(
                visible = showTimeoutMessage,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (timeoutLevel) {
                                1 -> "This is taking longer than usual..."
                                2 -> "Almost there... Large texts take more time"
                                3 -> "Still processing. You can try with shorter text"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    if (timeoutLevel >= 3) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Try Shorter Text")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Cancel button
            TextButton(
                onClick = onCancel,
                modifier = Modifier.alpha(0.7f)
            ) {
                Text("Cancel")
            }
            }
            }
        }
    }
}

