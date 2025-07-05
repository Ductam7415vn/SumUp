package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.haptic.HapticFeedbackManager
import com.example.sumup.utils.haptic.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdaptiveErrorSnackbar(
    error: AppError?,
    onDismiss: () -> Unit,
    onAction: (() -> Unit)? = null,
    hapticManager: HapticFeedbackManager? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var showSnackbar by remember { mutableStateOf(false) }
    
    LaunchedEffect(error) {
        if (error != null) {
            showSnackbar = true
            hapticManager?.performHapticFeedback(HapticFeedbackType.WARNING)
            
            // Auto-dismiss after 5 seconds for non-critical errors
            if (error !is AppError.NetworkError && error !is AppError.ServerError) {
                delay(5000)
                showSnackbar = false
                delay(300) // Wait for animation
                onDismiss()
            }
        } else {
            showSnackbar = false
        }
    }
    
    AnimatedVisibility(
        visible = showSnackbar && error != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(),
        modifier = modifier
    ) {
        error?.let { currentError ->
            val errorInfo = getSnackbarErrorInfo(currentError)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (currentError) {
                        is AppError.NetworkError -> MaterialTheme.colorScheme.errorContainer
                        is AppError.RateLimitError -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated icon
                        val iconRotation by rememberInfiniteTransition().animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        
                        Icon(
                            imageVector = errorInfo.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer {
                                    rotationZ = if (currentError is AppError.NetworkError) iconRotation else 0f
                                },
                            tint = when (currentError) {
                                is AppError.NetworkError -> MaterialTheme.colorScheme.onErrorContainer
                                is AppError.RateLimitError -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = errorInfo.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (currentError) {
                                    is AppError.NetworkError -> MaterialTheme.colorScheme.onErrorContainer
                                    is AppError.RateLimitError -> MaterialTheme.colorScheme.onTertiaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Text(
                                text = errorInfo.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = when (currentError) {
                                    is AppError.NetworkError -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                    is AppError.RateLimitError -> MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                }
                            )
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Action button
                        if (onAction != null && errorInfo.actionLabel != null) {
                            TextButton(
                                onClick = {
                                    hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                                    onAction()
                                    scope.launch {
                                        showSnackbar = false
                                        delay(300)
                                        onDismiss()
                                    }
                                }
                            ) {
                                Text(
                                    errorInfo.actionLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Dismiss button
                        IconButton(
                            onClick = {
                                hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                                scope.launch {
                                    showSnackbar = false
                                    delay(300)
                                    onDismiss()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class SnackbarErrorInfo(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val actionLabel: String? = null
)

private fun getSnackbarErrorInfo(error: AppError): SnackbarErrorInfo = when (error) {
    is AppError.NetworkError -> SnackbarErrorInfo(
        title = "No Connection",
        message = "Check your internet and try again",
        icon = Icons.Default.WifiOff,
        actionLabel = "Retry"
    )
    is AppError.RateLimitError -> SnackbarErrorInfo(
        title = "Daily Limit",
        message = "50 summaries used today",
        icon = Icons.Default.HourglassEmpty,
        actionLabel = "Upgrade"
    )
    is AppError.TextTooShortError -> SnackbarErrorInfo(
        title = "Too Short",
        message = "Add more text (min 50 chars)",
        icon = Icons.Default.ShortText
    )
    else -> SnackbarErrorInfo(
        title = "Error",
        message = error.message,
        icon = Icons.Default.Warning
    )
}