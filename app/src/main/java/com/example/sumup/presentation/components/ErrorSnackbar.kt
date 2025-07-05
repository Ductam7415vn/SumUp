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
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.haptic.HapticFeedbackManager
import com.example.sumup.utils.haptic.HapticFeedbackType
import kotlinx.coroutines.delay

@Composable
fun ErrorSnackbar(
    error: AppError,
    onDismiss: () -> Unit,
    onAction: (() -> Unit)? = null,
    hapticManager: HapticFeedbackManager? = null,
    modifier: Modifier = Modifier
) {
    val errorInfo = getSnackbarErrorInfo(error)
    
    // Auto-dismiss after 4 seconds
    LaunchedEffect(error) {
        hapticManager?.performHapticFeedback(HapticFeedbackType.WARNING)
        delay(4000)
        onDismiss()
    }
    
    // Slide in animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(error) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (errorInfo.severity) {
                    ErrorSeverity.LOW -> MaterialTheme.colorScheme.surfaceVariant
                    ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                    ErrorSeverity.HIGH -> MaterialTheme.colorScheme.errorContainer
                    ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                }
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Error icon with animation
                val iconScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (errorInfo.severity) {
                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.surface
                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.surface
                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.error
                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onError
                        }
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = errorInfo.icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = when (errorInfo.severity) {
                                ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurface
                                ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onSurface
                                ErrorSeverity.HIGH -> MaterialTheme.colorScheme.onError
                                ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
                
                // Error message
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = errorInfo.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = when (errorInfo.severity) {
                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onError
                        }
                    )
                    Text(
                        text = errorInfo.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (errorInfo.severity) {
                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                        }
                    )
                }
                
                // Action button or dismiss button
                if (onAction != null && errorInfo.canRetry) {
                    TextButton(
                        onClick = {
                            hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                            onAction()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = when (errorInfo.severity) {
                                ErrorSeverity.LOW -> MaterialTheme.colorScheme.primary
                                ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.primary
                                ErrorSeverity.HIGH -> MaterialTheme.colorScheme.error
                                ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onError
                            }
                        )
                    ) {
                        Text(
                            text = errorInfo.actionText,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                            visible = false
                            onDismiss()
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(18.dp),
                            tint = when (errorInfo.severity) {
                                ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                                ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
                                ErrorSeverity.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onError
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getSnackbarErrorInfo(error: AppError): EnhancedErrorInfo = when (error) {
    is AppError.NetworkError -> EnhancedErrorInfo(
        title = "No Internet",
        message = "Check your connection",
        illustration = "📡",
        icon = Icons.Default.SignalWifiOff,
        actionText = "Retry",
        canRetry = true,
        severity = ErrorSeverity.HIGH
    )
    is AppError.TextTooShortError -> EnhancedErrorInfo(
        title = "More text needed",
        message = "Add at least 50 characters",
        illustration = "✍️",
        icon = Icons.Default.ShortText,
        actionText = "OK",
        canRetry = false,
        severity = ErrorSeverity.LOW
    )
    is AppError.ServerError -> EnhancedErrorInfo(
        title = "Server Error",
        message = "Try again in a moment",
        illustration = "🔧",
        icon = Icons.Default.Build,
        actionText = "Retry",
        canRetry = true,
        severity = ErrorSeverity.HIGH
    )
    is AppError.OCRFailedError -> EnhancedErrorInfo(
        title = "Scan Failed",
        message = "Try better lighting",
        illustration = "📷",
        icon = Icons.Default.CameraAlt,
        actionText = "Retry",
        canRetry = true,
        severity = ErrorSeverity.MEDIUM
    )
    else -> EnhancedErrorInfo(
        title = "Error",
        message = error.message.take(50),
        illustration = "⚠️",
        icon = Icons.Default.ErrorOutline,
        actionText = "OK",
        canRetry = false,
        severity = ErrorSeverity.MEDIUM
    )
}