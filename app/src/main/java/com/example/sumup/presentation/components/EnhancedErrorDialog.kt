package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.haptic.HapticFeedbackManager
import com.example.sumup.utils.haptic.HapticFeedbackType

data class EnhancedErrorInfo(
    val title: String,
    val message: String,
    val illustration: String,
    val icon: ImageVector,
    val actionText: String = "Try Again",
    val canRetry: Boolean = true,
    val severity: ErrorSeverity = ErrorSeverity.MEDIUM
)

enum class ErrorSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Composable
fun EnhancedErrorDialog(
    error: AppError,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    hapticManager: HapticFeedbackManager? = null
) {
    val errorInfo = getEnhancedErrorInfo(error)
    
    // Animation for error illustration
    val illustrationScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(error) {
        when (errorInfo.severity) {
            ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> 
                hapticManager?.performHapticFeedback(HapticFeedbackType.ERROR)
            ErrorSeverity.MEDIUM -> 
                hapticManager?.performHapticFeedback(HapticFeedbackType.WARNING)
            ErrorSeverity.LOW -> 
                hapticManager?.performHapticFeedback(HapticFeedbackType.TICK)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Animated illustration
                Text(
                    text = errorInfo.illustration,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = illustrationScale
                            scaleY = illustrationScale
                        }
                )
                
                // Error severity indicator
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (errorInfo.severity) {
                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.surfaceVariant
                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.errorContainer
                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                        }
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = errorInfo.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = when (errorInfo.severity) {
                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onError
                        }
                    )
                }
            }
        },
        title = { 
            Text(
                text = errorInfo.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            ) 
        },
        text = { 
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = errorInfo.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Additional context based on error type
                when (error) {
                    is AppError.RateLimitError -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Resets at midnight",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    is AppError.TextTooShortError -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Minimum 50 characters needed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            if (errorInfo.canRetry && onRetry != null) {
                Button(
                    onClick = {
                        hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                        onRetry()
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorInfo.actionText)
                }
            } else {
                TextButton(
                    onClick = {
                        hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        },
        dismissButton = if (errorInfo.canRetry && onRetry != null) {
            {
                TextButton(
                    onClick = {
                        hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                        onDismiss()
                    }
                ) {
                    Text("Cancel")
                }
            }
        } else null
    )
}

fun getEnhancedErrorInfo(error: AppError): EnhancedErrorInfo = when (error) {
    is AppError.NetworkError -> EnhancedErrorInfo(
        title = "No Internet Connection",
        message = "Check your WiFi or mobile data connection and try again.",
        illustration = "📡",
        icon = Icons.Default.SignalWifiOff,
        actionText = "Retry",
        canRetry = true,
        severity = ErrorSeverity.HIGH
    )
    is AppError.RateLimitError -> EnhancedErrorInfo(
        title = "Daily Limit Reached",
        message = "You've made 50 summaries today! Your limit resets at midnight.",
        illustration = "⏰",
        icon = Icons.Default.HourglassEmpty,
        actionText = "Check Premium",
        canRetry = false,
        severity = ErrorSeverity.MEDIUM
    )
    is AppError.TextTooShortError -> EnhancedErrorInfo(
        title = "Need More Text",
        message = "Add more content to create a meaningful summary. We need at least 50 characters.",
        illustration = "✍️",
        icon = Icons.Default.ShortText,
        actionText = "Got it",
        canRetry = false,
        severity = ErrorSeverity.LOW
    )
    is AppError.OCRFailedError -> EnhancedErrorInfo(
        title = "Couldn't Read Text",
        message = "Try better lighting, hold your device steady, or make sure the text is clear and readable.",
        illustration = "📷",
        icon = Icons.Default.CameraAlt,
        actionText = "Try Again",
        canRetry = true,
        severity = ErrorSeverity.MEDIUM
    )
    is AppError.ServerError -> EnhancedErrorInfo(
        title = "Something Went Wrong",
        message = "Our servers are having issues right now. Please try again in a few minutes.",
        illustration = "🔧",
        icon = Icons.Default.Build,
        actionText = "Retry",
        canRetry = true,
        severity = ErrorSeverity.HIGH
    )
    is AppError.ModelLoadingError -> EnhancedErrorInfo(
        title = "AI is Warming Up",
        message = "First-time setup is happening. This usually takes about 10 seconds.",
        illustration = "🤖",
        icon = Icons.Default.Psychology,
        actionText = "Wait",
        canRetry = false,
        severity = ErrorSeverity.LOW
    )
    is AppError.StorageFullError -> EnhancedErrorInfo(
        title = "Storage Full",
        message = "You've reached the 100MB limit. Delete some old summaries to continue.",
        illustration = "💾",
        icon = Icons.Default.Storage,
        actionText = "Manage Storage",
        canRetry = false,
        severity = ErrorSeverity.MEDIUM
    )
    is AppError.InvalidInputError -> EnhancedErrorInfo(
        title = "Can't Process This Text",
        message = "The text contains too many special characters or unusual formatting.",
        illustration = "⚠️",
        icon = Icons.Default.Warning,
        actionText = "Edit Text",
        canRetry = false,
        severity = ErrorSeverity.MEDIUM
    )
    is AppError.UnknownError -> EnhancedErrorInfo(
        title = "Unexpected Error",
        message = error.originalMessage.ifEmpty { "Something unexpected happened. Please try again." },
        illustration = "😕",
        icon = Icons.Default.ErrorOutline,
        actionText = "Retry",
        canRetry = true,
        severity = ErrorSeverity.HIGH
    )
}