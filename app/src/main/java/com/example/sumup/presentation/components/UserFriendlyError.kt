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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.sumup.domain.model.AppError
import com.example.sumup.ui.theme.extendedColorScheme

/**
 * User-friendly error display with helpful suggestions
 */
@Composable
fun UserFriendlyErrorCard(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errorInfo = getUserFriendlyErrorInfo(error)
    
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (errorInfo.severity) {
                    ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                    ErrorSeverity.MEDIUM -> MaterialTheme.extendedColorScheme.warningContainer
                    ErrorSeverity.LOW -> MaterialTheme.extendedColorScheme.infoContainer
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated icon
                val rotation by rememberInfiniteTransition(label = "icon").animateFloat(
                    initialValue = -10f,
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "icon_rotation"
                )
                
                Icon(
                    imageVector = errorInfo.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer { rotationZ = rotation },
                    tint = when (errorInfo.severity) {
                        ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                        ErrorSeverity.MEDIUM -> MaterialTheme.extendedColorScheme.warning
                        ErrorSeverity.LOW -> MaterialTheme.extendedColorScheme.info
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Title
                Text(
                    text = errorInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = when (errorInfo.severity) {
                        ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onErrorContainer
                        ErrorSeverity.MEDIUM -> MaterialTheme.extendedColorScheme.onWarningContainer
                        ErrorSeverity.LOW -> MaterialTheme.extendedColorScheme.onInfoContainer
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Message
                Text(
                    text = errorInfo.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = when (errorInfo.severity) {
                        ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        ErrorSeverity.MEDIUM -> MaterialTheme.extendedColorScheme.onWarningContainer.copy(alpha = 0.8f)
                        ErrorSeverity.LOW -> MaterialTheme.extendedColorScheme.onInfoContainer.copy(alpha = 0.8f)
                    }
                )
                
                // Suggestions
                if (errorInfo.suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Try this:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        errorInfo.suggestions.forEach { suggestion ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                
                // Actions
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onRetry != null) {
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (errorInfo.severity) {
                                    ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                                    ErrorSeverity.MEDIUM -> MaterialTheme.extendedColorScheme.warning
                                    ErrorSeverity.LOW -> MaterialTheme.extendedColorScheme.info
                                }
                            )
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Try Again")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Got it")
                    }
                }
            }
        }
    }
}


private data class UserFriendlyErrorInfo(
    val icon: ImageVector,
    val title: String,
    val message: String,
    val suggestions: List<String>,
    val severity: ErrorSeverity
)

private fun getUserFriendlyErrorInfo(error: AppError): UserFriendlyErrorInfo {
    return when (error) {
        is AppError.NetworkError -> UserFriendlyErrorInfo(
            icon = Icons.Default.WifiOff,
            title = "Connection Issue",
            message = "We couldn't connect to our servers. This might be a temporary issue.",
            suggestions = listOf(
                "Check your internet connection",
                "Try again in a few seconds",
                "Switch between WiFi and mobile data"
            ),
            severity = ErrorSeverity.MEDIUM
        )
        
        is AppError.ModelLoadingError -> UserFriendlyErrorInfo(
            icon = Icons.Default.CloudOff,
            title = "Model Loading",
            message = "The AI model is loading. This may take a moment.",
            suggestions = listOf(
                "Wait a few seconds and try again",
                "Check your internet connection",
                "Restart the app if this persists"
            ),
            severity = ErrorSeverity.LOW
        )
        
        is AppError.InvalidInputError -> UserFriendlyErrorInfo(
            icon = Icons.Default.Info,
            title = "Input Issue",
            message = "There's an issue with your input format.",
            suggestions = listOf(
                "Check for special characters or formatting",
                "Try plain text without special formatting",
                "Remove any unusual symbols"
            ),
            severity = ErrorSeverity.LOW
        )
        
        is AppError.ServerError -> UserFriendlyErrorInfo(
            icon = Icons.Default.CloudOff,
            title = "Server Busy",
            message = "Our servers are experiencing high demand right now.",
            suggestions = listOf(
                "Wait a moment and try again",
                "Try with a shorter text",
                "Come back in a few minutes"
            ),
            severity = ErrorSeverity.MEDIUM
        )
        
        is AppError.UnknownError -> UserFriendlyErrorInfo(
            icon = Icons.Default.Warning,
            title = "Something Went Wrong",
            message = "An unexpected error occurred. Don't worry, your data is safe.",
            suggestions = listOf(
                "Try again",
                "Restart the app if the problem persists",
                "Contact support if this keeps happening"
            ),
            severity = ErrorSeverity.HIGH
        )
        
        is AppError.TextTooShortError -> UserFriendlyErrorInfo(
            icon = Icons.Default.ShortText,
            title = "Text Too Short",
            message = "Your text is too short to summarize effectively.",
            suggestions = listOf(
                "Add more content (at least 50 characters)",
                "Combine multiple paragraphs",
                "Include more details"
            ),
            severity = ErrorSeverity.LOW
        )
        
        is AppError.StorageFullError -> UserFriendlyErrorInfo(
            icon = Icons.Default.Storage,
            title = "Storage Full",
            message = "Not enough storage space.",
            suggestions = listOf(
                "Clear some old summaries",
                "Free up device storage",
                "Delete unused files"
            ),
            severity = ErrorSeverity.MEDIUM
        )
        
        is AppError.OCRFailedError -> UserFriendlyErrorInfo(
            icon = Icons.Default.CameraAlt,
            title = "OCR Failed",
            message = "Failed to extract text from image.",
            suggestions = listOf("Make sure the image contains clear text", "Try with better lighting"),
            severity = ErrorSeverity.MEDIUM
        )
        
        is AppError.RateLimitError -> UserFriendlyErrorInfo(
            icon = Icons.Default.Timer,
            title = "Rate Limit Reached",
            message = "Too many requests. Please wait before trying again.",
            suggestions = listOf("Wait a moment before trying again", "Consider upgrading your API plan"),
            severity = ErrorSeverity.MEDIUM
        )
        
        is AppError.ApiKeyError -> UserFriendlyErrorInfo(
            icon = Icons.Default.Key,
            title = "API Key Required",
            message = "You need to add your Gemini API key to use real summaries.",
            suggestions = listOf("Go to Settings and add your API key", "Get a free API key from Google"),
            severity = ErrorSeverity.HIGH
        )
        
        is AppError.InvalidApiKeyError -> UserFriendlyErrorInfo(
            icon = Icons.Default.VpnKey,
            title = "Invalid API Key",
            message = "The API key you provided is not valid.",
            suggestions = listOf("Check your API key for typos", "Generate a new key from Google"),
            severity = ErrorSeverity.HIGH
        )
        
        is AppError.UnknownError -> UserFriendlyErrorInfo(
            icon = Icons.Default.ErrorOutline,
            title = "Something Went Wrong",
            message = error.originalMessage,
            suggestions = listOf("Try again", "Contact support if this persists"),
            severity = ErrorSeverity.HIGH
        )
    }
}