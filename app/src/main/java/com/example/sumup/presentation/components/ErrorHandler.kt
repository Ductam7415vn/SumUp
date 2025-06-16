package com.example.sumup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.haptic.HapticFeedbackManager

enum class ErrorDisplayMode {
    DIALOG,      // Full dialog for critical errors
    SNACKBAR,    // Less intrusive for minor errors
    INLINE       // For field-specific errors
}

@Composable
fun ErrorHandler(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    displayMode: ErrorDisplayMode = ErrorDisplayMode.DIALOG,
    hapticManager: HapticFeedbackManager? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        
        error?.let { currentError ->
            when (displayMode) {
                ErrorDisplayMode.DIALOG -> {
                    EnhancedErrorDialog(
                        error = currentError,
                        onDismiss = onDismiss,
                        onRetry = onRetry,
                        hapticManager = hapticManager
                    )
                }
                ErrorDisplayMode.SNACKBAR -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ErrorSnackbar(
                            error = currentError,
                            onDismiss = onDismiss,
                            onAction = onRetry,
                            hapticManager = hapticManager
                        )
                    }
                }
                ErrorDisplayMode.INLINE -> {
                    // For inline errors - you can implement field-specific error display
                    // This would be used in form fields
                }
            }
        }
    }
}

/**
 * Smart error display mode selection based on error type and context
 */
fun getErrorDisplayMode(error: AppError, isFormContext: Boolean = false): ErrorDisplayMode {
    return when {
        isFormContext && (error is AppError.TextTooShortError || error is AppError.InvalidInputError) -> 
            ErrorDisplayMode.INLINE
        error is AppError.NetworkError || error is AppError.ServerError || error is AppError.RateLimitError -> 
            ErrorDisplayMode.DIALOG
        error is AppError.TextTooShortError || error is AppError.OCRFailedError -> 
            ErrorDisplayMode.SNACKBAR
        else -> ErrorDisplayMode.DIALOG
    }
}

/**
 * Composable wrapper that automatically selects the best error display mode
 */
@Composable
fun SmartErrorHandler(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    isFormContext: Boolean = false,
    hapticManager: HapticFeedbackManager? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val displayMode = error?.let { getErrorDisplayMode(it, isFormContext) } ?: ErrorDisplayMode.DIALOG
    
    ErrorHandler(
        error = error,
        onDismiss = onDismiss,
        onRetry = onRetry,
        displayMode = displayMode,
        hapticManager = hapticManager,
        modifier = modifier,
        content = content
    )
}