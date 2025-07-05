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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError
import java.text.SimpleDateFormat
import java.util.*

data class ContextualError(
    val error: AppError,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val actions: List<ContextualErrorAction>
)

data class ContextualErrorAction(
    val label: String,
    val action: () -> Unit,
    val isPrimary: Boolean = false
)

@Composable
fun ContextualErrorHandler(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    onNavigateToSettings: (() -> Unit)? = null,
    onOpenHelp: (() -> Unit)? = null,
    isFormContext: Boolean = false
) {
    error?.let { appError ->
        val contextualError = getContextualError(
            appError = appError,
            onDismiss = onDismiss,
            onRetry = onRetry,
            onNavigateToSettings = onNavigateToSettings,
            onOpenHelp = onOpenHelp
        )
        
        if (isFormContext || shouldShowInline(appError)) {
            InlineContextualError(contextualError)
        } else {
            ContextualErrorDialog(contextualError)
        }
    }
}

@Composable
fun InlineContextualError(
    contextualError: ContextualError,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = contextualError.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contextualError.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = contextualError.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                    
                    if (contextualError.actions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            contextualError.actions.forEach { action ->
                                if (action.isPrimary) {
                                    Button(
                                        onClick = action.action,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = action.label,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                } else {
                                    TextButton(
                                        onClick = action.action,
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = action.label,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContextualErrorDialog(
    contextualError: ContextualError
) {
    AlertDialog(
        onDismissRequest = contextualError.actions.firstOrNull { !it.isPrimary }?.action ?: {},
        icon = {
            Icon(
                imageVector = contextualError.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = contextualError.title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(text = contextualError.description)
        },
        confirmButton = {
            contextualError.actions.firstOrNull { it.isPrimary }?.let { action ->
                Button(onClick = action.action) {
                    Text(action.label)
                }
            }
        },
        dismissButton = {
            contextualError.actions.firstOrNull { !it.isPrimary }?.let { action ->
                TextButton(onClick = action.action) {
                    Text(action.label)
                }
            }
        }
    )
}

private fun getContextualError(
    appError: AppError,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?,
    onNavigateToSettings: (() -> Unit)?,
    onOpenHelp: (() -> Unit)?
): ContextualError {
    return when (appError) {
        is AppError.NetworkError -> ContextualError(
            error = appError,
            title = "No Internet Connection",
            description = "Please check your Wi-Fi or mobile data connection and try again. SumUp needs internet to process summaries.",
            icon = Icons.Default.WifiOff,
            actions = buildList {
                onRetry?.let {
                    add(ContextualErrorAction("Try Again", it, isPrimary = true))
                }
                add(ContextualErrorAction("Cancel", onDismiss))
            }
        )
        
        is AppError.ServerError -> ContextualError(
            error = appError,
            title = "Service Temporarily Unavailable",
            description = "Our AI service is experiencing issues. This usually resolves within a few minutes.",
            icon = Icons.Default.CloudOff,
            actions = buildList {
                onRetry?.let {
                    add(ContextualErrorAction("Retry", it, isPrimary = true))
                }
                onOpenHelp?.let {
                    add(ContextualErrorAction("Get Help", it))
                }
                add(ContextualErrorAction("OK", onDismiss))
            }
        )
        
        is AppError.RateLimitError -> {
            val resetDate = Date(appError.resetTime)
            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            ContextualError(
                error = appError,
                title = "Daily Limit Reached",
                description = "You've used all your free summaries for today. Your limit will reset at ${formatter.format(resetDate)}.",
                icon = Icons.Default.HourglassEmpty,
                actions = buildList {
                    add(ContextualErrorAction("Set Reminder", {
                        // TODO: Set reminder for reset time
                        onDismiss()
                    }, isPrimary = true))
                    add(ContextualErrorAction("Learn More", {
                        onOpenHelp?.invoke()
                    }))
                    add(ContextualErrorAction("OK", onDismiss))
                }
            )
        }
        
        is AppError.TextTooShortError -> ContextualError(
            error = appError,
            title = "Text Too Short",
            description = "Please provide at least 50 characters (about 10 words) for a meaningful summary.",
            icon = Icons.Default.ShortText,
            actions = listOf(
                ContextualErrorAction("Got It", onDismiss, isPrimary = true)
            )
        )
        
        is AppError.InvalidInputError -> ContextualError(
            error = appError,
            title = "Invalid Text Format",
            description = "The text contains unsupported characters or formatting. Try copying plain text instead.",
            icon = Icons.Default.Error,
            actions = buildList {
                add(ContextualErrorAction("OK", onDismiss, isPrimary = true))
                onOpenHelp?.let {
                    add(ContextualErrorAction("Learn More", it))
                }
            }
        )
        
        is AppError.OCRFailedError -> ContextualError(
            error = appError,
            title = "Couldn't Read Text",
            description = "Make sure the image has clear text and good lighting. Try taking another photo.",
            icon = Icons.Default.CameraAlt,
            actions = buildList {
                onRetry?.let {
                    add(ContextualErrorAction("Try Again", it, isPrimary = true))
                }
                add(ContextualErrorAction("Cancel", onDismiss))
            }
        )
        
        is AppError.StorageFullError -> ContextualError(
            error = appError,
            title = "Storage Full",
            description = "Your summary history is full. Delete some old summaries to make space.",
            icon = Icons.Default.Storage,
            actions = buildList {
                add(ContextualErrorAction("Manage Storage", {
                    // TODO: Navigate to history with delete mode
                    onDismiss()
                }, isPrimary = true))
                add(ContextualErrorAction("Later", onDismiss))
            }
        )
        
        is AppError.ModelLoadingError -> ContextualError(
            error = appError,
            title = "AI Service Starting",
            description = "The AI model is loading. This usually takes a few seconds on first use.",
            icon = Icons.Default.Psychology,
            actions = buildList {
                onRetry?.let {
                    add(ContextualErrorAction("Try Again", it, isPrimary = true))
                }
                add(ContextualErrorAction("Cancel", onDismiss))
            }
        )
        
        is AppError.ApiKeyError -> ContextualError(
            error = appError,
            title = "API Key Required",
            description = "To use real AI summaries, you need to add your Gemini API key in settings.",
            icon = Icons.Default.Key,
            actions = buildList {
                onNavigateToSettings?.let {
                    add(ContextualErrorAction("Add API Key", it, isPrimary = true))
                }
                add(ContextualErrorAction("Learn More", {
                    onOpenHelp?.invoke()
                }))
                add(ContextualErrorAction("Use Demo", onDismiss))
            }
        )
        
        is AppError.InvalidApiKeyError -> ContextualError(
            error = appError,
            title = "Invalid API Key",
            description = "The API key you provided is not valid. Please check it and try again.",
            icon = Icons.Default.VpnKey,
            actions = buildList {
                onNavigateToSettings?.let {
                    add(ContextualErrorAction("Fix API Key", it, isPrimary = true))
                }
                add(ContextualErrorAction("Get New Key", {
                    onOpenHelp?.invoke()
                }))
                add(ContextualErrorAction("Cancel", onDismiss))
            }
        )
        
        is AppError.UnknownError -> ContextualError(
            error = appError,
            title = "Something Went Wrong",
            description = "An unexpected error occurred: ${appError.originalMessage}",
            icon = Icons.Default.ErrorOutline,
            actions = buildList {
                onRetry?.let {
                    add(ContextualErrorAction("Try Again", it, isPrimary = true))
                }
                add(ContextualErrorAction("Dismiss", onDismiss))
            }
        )
    }
}

private fun shouldShowInline(error: AppError): Boolean {
    return when (error) {
        is AppError.TextTooShortError,
        is AppError.InvalidInputError -> true
        else -> false
    }
}