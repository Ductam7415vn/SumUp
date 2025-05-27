package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    progress: Float,
    message: String,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeoutLevel by remember { mutableStateOf(0) }
    var showTimeoutMessage by remember { mutableStateOf(false) }
    
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
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            AnimatedProcessingIcon(
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress message
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Smart progress bar
            SmartProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Timeout messages
            AnimatedVisibility(
                visible = showTimeoutMessage,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        text = when (timeoutLevel) {
                            1 -> "⏱ This is taking longer than usual..."
                            2 -> "Almost there... Large texts take more time"
                            3 -> "Still processing. You can try with shorter text"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
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

@Composable
private fun SmartProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )
    
    // Shimmer effect
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier.height(8.dp),
        color = getProgressColor(animatedProgress),
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

private fun getProgressColor(progress: Float): Color {
    return when {
        progress < 0.3f -> Color(0xFF4CAF50)  // Green
        progress < 0.7f -> Color(0xFF2196F3)  // Blue  
        else -> Color(0xFF9C27B0)             // Purple
    }
}