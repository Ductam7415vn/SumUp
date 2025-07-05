package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import com.example.sumup.ui.theme.Dimensions
import com.example.sumup.ui.theme.Spacing
import com.example.sumup.utils.haptic.HapticFeedbackManager
import com.example.sumup.utils.haptic.HapticFeedbackType

/**
 * Improved error handler with better UX
 */
@Composable
fun ImprovedErrorHandler(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    isFormContext: Boolean = false,
    modifier: Modifier = Modifier,
    hapticManager: HapticFeedbackManager? = null,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        
        error?.let { currentError ->
            when (getImprovedErrorDisplayMode(currentError, isFormContext)) {
                ErrorDisplayMode.INLINE -> {
                    // Don't show anything here - let the form field handle it
                }
                ErrorDisplayMode.SNACKBAR -> {
                    ErrorSnackbar(
                        error = currentError,
                        onDismiss = onDismiss,
                        onRetry = onRetry,
                        hapticManager = hapticManager
                    )
                }
                ErrorDisplayMode.DIALOG -> {
                    UserFriendlyErrorDialog(
                        error = currentError,
                        onDismiss = onDismiss,
                        onRetry = onRetry,
                        hapticManager = hapticManager
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorSnackbar(
    error: AppError,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?,
    hapticManager: HapticFeedbackManager?
) {
    LaunchedEffect(error) {
        hapticManager?.performHapticFeedback(HapticFeedbackType.ERROR)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.spacingMd),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(Dimensions.radiusMd),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = Dimensions.elevationMd
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.cardPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
                    ) {
                        Icon(
                            imageVector = getErrorIcon(error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(Dimensions.iconSizeMd)
                        )
                        Text(
                            text = getUserFriendlyMessage(error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
                    ) {
                        onRetry?.let {
                            TextButton(
                                onClick = {
                                    hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                                    it()
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                        
                        IconButton(
                            onClick = {
                                hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                                onDismiss()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss error"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserFriendlyErrorDialog(
    error: AppError,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?,
    hapticManager: HapticFeedbackManager?
) {
    LaunchedEffect(error) {
        hapticManager?.performHapticFeedback(HapticFeedbackType.ERROR)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = getErrorIcon(error),
                contentDescription = null,
                tint = getErrorColor(error),
                modifier = Modifier.size(Dimensions.iconSizeXl)
            )
        },
        title = {
            Text(
                text = getUserFriendlyTitle(error),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
            ) {
                Text(
                    text = getUserFriendlyMessage(error),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                getSuggestion(error)?.let { suggestion ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(Dimensions.radiusSm)
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimensions.paddingMd),
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Dimensions.iconSizeSm)
                            )
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (onRetry != null) {
                Button(
                    onClick = {
                        hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                        onRetry()
                        onDismiss()
                    }
                ) {
                    Text("Try Again")
                }
            } else {
                TextButton(
                    onClick = {
                        hapticManager?.performHapticFeedback(HapticFeedbackType.CLICK)
                        onDismiss()
                    }
                ) {
                    Text("Got it")
                }
            }
        },
        dismissButton = if (onRetry != null) {
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

private fun getImprovedErrorDisplayMode(error: AppError, isFormContext: Boolean): ErrorDisplayMode {
    return when (error) {
        is AppError.TextTooShortError -> if (isFormContext) ErrorDisplayMode.INLINE else ErrorDisplayMode.SNACKBAR
        is AppError.NetworkError -> ErrorDisplayMode.SNACKBAR
        is AppError.ServerError,
        is AppError.RateLimitError -> ErrorDisplayMode.DIALOG
        else -> ErrorDisplayMode.DIALOG
    }
}

private fun getUserFriendlyTitle(error: AppError): String {
    return when (error) {
        is AppError.NetworkError -> "Connection Issue"
        is AppError.ServerError -> "Server Problem"
        is AppError.TextTooShortError -> "Text Too Short"
        is AppError.RateLimitError -> "Too Many Requests"
        is AppError.OCRFailedError -> "OCR Processing Failed"
        is AppError.UnknownError -> "Something Went Wrong"
        else -> "Error"
    }
}

private fun getUserFriendlyMessage(error: AppError): String {
    return when (error) {
        is AppError.NetworkError -> "Please check your internet connection and try again."
        is AppError.ServerError -> "Our servers are having issues. Please try again later."
        is AppError.TextTooShortError -> "Please enter more text for better summarization."
        is AppError.RateLimitError -> "You've made too many requests. Please wait a moment."
        is AppError.OCRFailedError -> "We couldn't read text from the image. Please try a different file."
        is AppError.UnknownError -> "An unexpected error occurred. Please try again."
        else -> error.message
    }
}

private fun getSuggestion(error: AppError): String? {
    return when (error) {
        is AppError.NetworkError -> "Try switching between WiFi and mobile data"
        is AppError.TextTooShortError -> "Add more content for better summarization"
        is AppError.RateLimitError -> "Daily limit reached. Please try again tomorrow."
        is AppError.OCRFailedError -> "Make sure the image is clear and contains readable text"
        else -> null
    }
}

private fun getErrorIcon(error: AppError): ImageVector {
    return when (error) {
        is AppError.NetworkError -> Icons.Default.WifiOff
        is AppError.ServerError -> Icons.Default.Cloud
        is AppError.TextTooShortError -> Icons.Default.TextFields
        is AppError.RateLimitError -> Icons.Default.Timer
        is AppError.OCRFailedError -> Icons.Default.Image
        else -> Icons.Default.Warning
    }
}

@Composable
private fun getErrorColor(error: AppError): Color {
    return when (error) {
        is AppError.TextTooShortError -> MaterialTheme.colorScheme.tertiary
        is AppError.RateLimitError -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
}

