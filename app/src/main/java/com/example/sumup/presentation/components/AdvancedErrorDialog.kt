package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.haptic.HapticFeedbackManager
import com.example.sumup.utils.haptic.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ErrorAction(
    val label: String,
    val icon: ImageVector? = null,
    val action: () -> Unit,
    val isPrimary: Boolean = false
)

data class AdvancedErrorInfo(
    val title: String,
    val message: String,
    val illustration: String,
    val icon: ImageVector,
    val actions: List<ErrorAction> = emptyList(),
    val severity: ErrorSeverity = ErrorSeverity.MEDIUM,
    val dismissible: Boolean = true,
    val autoRetryEnabled: Boolean = false,
    val retryDelaySeconds: Int = 30,
    val technicalDetails: String? = null,
    val troubleshootingSteps: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedErrorDialog(
    error: AppError,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    onReportIssue: (() -> Unit)? = null,
    hapticManager: HapticFeedbackManager? = null
) {
    val errorInfo = getAdvancedErrorInfo(error, onRetry, onReportIssue)
    var showTechnicalDetails by remember { mutableStateOf(false) }
    var isRetrying by remember { mutableStateOf(false) }
    var retryCountdown by remember { mutableStateOf(errorInfo.retryDelaySeconds) }
    val scope = rememberCoroutineScope()
    
    // Auto-retry logic
    LaunchedEffect(errorInfo) {
        if (errorInfo.autoRetryEnabled && onRetry != null) {
            while (retryCountdown > 0) {
                delay(1000)
                retryCountdown--
            }
            onRetry()
        }
    }
    
    // Animations
    val illustrationRotation by rememberInfiniteTransition().animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (isRetrying) 0.5f else 1f,
        animationSpec = tween(300)
    )
    
    LaunchedEffect(error) {
        when (errorInfo.severity) {
            ErrorSeverity.CRITICAL -> {
                hapticManager?.performHapticFeedback(HapticFeedbackType.ERROR)
                delay(100)
                hapticManager?.performHapticFeedback(HapticFeedbackType.ERROR)
            }
            ErrorSeverity.HIGH -> hapticManager?.performHapticFeedback(HapticFeedbackType.ERROR)
            ErrorSeverity.MEDIUM -> hapticManager?.performHapticFeedback(HapticFeedbackType.WARNING)
            ErrorSeverity.LOW -> hapticManager?.performHapticFeedback(HapticFeedbackType.TICK)
        }
    }

    BasicAlertDialog(
        onDismissRequest = { 
            if (errorInfo.dismissible && !isRetrying) {
                onDismiss()
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .graphicsLayer { alpha = contentAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated error illustration
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    // Background gradient circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        when (errorInfo.severity) {
                                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        },
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    
                    // Animated illustration
                    Text(
                        text = errorInfo.illustration,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = if (errorInfo.severity == ErrorSeverity.CRITICAL) 
                                illustrationRotation else 0f
                        }
                    )
                    
                    // Severity icon overlay
                    Icon(
                        imageVector = errorInfo.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(4.dp),
                        tint = when (errorInfo.severity) {
                            ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                            ErrorSeverity.HIGH -> MaterialTheme.colorScheme.error
                            ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiary
                            ErrorSeverity.LOW -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = errorInfo.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Message
                Text(
                    text = errorInfo.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Auto-retry countdown
                if (errorInfo.autoRetryEnabled && retryCountdown > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = 1f - (retryCountdown.toFloat() / errorInfo.retryDelaySeconds),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "Retrying in $retryCountdown seconds...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Troubleshooting steps
                if (errorInfo.troubleshootingSteps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Try these steps:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            errorInfo.troubleshootingSteps.forEachIndexed { index, step ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        "${index + 1}.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        step,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Technical details toggle
                if (errorInfo.technicalDetails != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showTechnicalDetails = !showTechnicalDetails }
                    ) {
                        Icon(
                            if (showTechnicalDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (showTechnicalDetails) "Hide Details" else "Show Technical Details",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    
                    AnimatedVisibility(visible = showTechnicalDetails) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorInfo.technicalDetails,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    errorInfo.actions.forEach { action ->
                        if (action.isPrimary) {
                            Button(
                                onClick = {
                                    hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                                    if (action.label.contains("Retry", ignoreCase = true)) {
                                        isRetrying = true
                                        scope.launch {
                                            delay(300)
                                            action.action()
                                        }
                                    } else {
                                        action.action()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isRetrying
                            ) {
                                if (isRetrying && action.label.contains("Retry", ignoreCase = true)) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    action.icon?.let {
                                        Icon(
                                            it,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(action.label)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                                    action.action()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isRetrying
                            ) {
                                action.icon?.let {
                                    Icon(
                                        it,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(action.label)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getAdvancedErrorInfo(
    error: AppError,
    onRetry: (() -> Unit)?,
    onReportIssue: (() -> Unit)?
): AdvancedErrorInfo = when (error) {
    is AppError.NetworkError -> AdvancedErrorInfo(
        title = "Connection Lost",
        message = "We couldn't reach our servers. This might be a temporary issue.",
        illustration = "📡",
        icon = Icons.Default.SignalWifiOff,
        actions = buildList {
            onRetry?.let {
                add(ErrorAction("Retry Connection", Icons.Default.Refresh, it, true))
            }
            add(ErrorAction("Check Settings", Icons.Default.Settings, {}))
            onReportIssue?.let {
                add(ErrorAction("Report Issue", Icons.Default.BugReport, it))
            }
        },
        severity = ErrorSeverity.HIGH,
        autoRetryEnabled = true,
        retryDelaySeconds = 10,
        technicalDetails = "Failed to establish connection to api.sumup.com",
        troubleshootingSteps = listOf(
            "Check if you're connected to WiFi or mobile data",
            "Try turning airplane mode on and off",
            "Restart the app if the problem persists"
        )
    )
    
    is AppError.RateLimitError -> AdvancedErrorInfo(
        title = "Daily Limit Reached",
        message = "You've used all 50 free summaries for today!",
        illustration = "⏰",
        icon = Icons.Default.HourglassEmpty,
        actions = listOf(
            ErrorAction("Upgrade to Premium", Icons.Default.Diamond, {}, true),
            ErrorAction("View History", Icons.Default.History, {}),
            ErrorAction("Set Reminder", Icons.Default.NotificationAdd, {})
        ),
        severity = ErrorSeverity.MEDIUM,
        dismissible = true,
        technicalDetails = "Rate limit: 50/day. Next reset: 00:00 UTC",
        troubleshootingSteps = listOf(
            "Upgrade to Premium for unlimited summaries",
            "Check your summary history to review past summaries",
            "Come back tomorrow when your limit resets"
        )
    )
    
    is AppError.ServerError -> AdvancedErrorInfo(
        title = "Server Error",
        message = "Our AI is experiencing technical difficulties.",
        illustration = "🔧",
        icon = Icons.Default.Build,
        actions = buildList {
            onRetry?.let {
                add(ErrorAction("Try Again", Icons.Default.Refresh, it, true))
            }
            add(ErrorAction("Use Offline Mode", Icons.Default.DownloadForOffline, {}))
            onReportIssue?.let {
                add(ErrorAction("Report Problem", Icons.Default.Flag, it))
            }
        },
        severity = ErrorSeverity.HIGH,
        autoRetryEnabled = false,
        technicalDetails = "HTTP 500 - Internal Server Error\nRequest ID: ${System.currentTimeMillis()}",
        troubleshootingSteps = listOf(
            "Wait a few minutes and try again",
            "Check our status page for updates",
            "Try using a shorter text"
        )
    )
    
    else -> AdvancedErrorInfo(
        title = "Something Went Wrong",
        message = error.message,
        illustration = "😕",
        icon = Icons.Default.ErrorOutline,
        actions = buildList {
            onRetry?.let {
                add(ErrorAction("Try Again", Icons.Default.Refresh, it, true))
            }
            add(ErrorAction("Dismiss", null, {}))
        },
        severity = ErrorSeverity.MEDIUM
    )
}