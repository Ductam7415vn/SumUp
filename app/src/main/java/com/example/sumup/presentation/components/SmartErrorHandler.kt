package com.example.sumup.presentation.components

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.haptic.HapticFeedbackManager

@Composable
fun SmartErrorHandler(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    isFormContext: Boolean = false,
    hapticManager: HapticFeedbackManager? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // Show inline error for form contexts
    if (error != null && isFormContext && error is AppError.TextTooShortError) {
        InlineErrorDisplay(
            error = error
        )
    }
    
    // Show dialog for other errors
    error?.let {
        if (!isFormContext || error !is AppError.TextTooShortError) {
            AdvancedErrorDialog(
                error = it,
                onDismiss = onDismiss,
                onRetry = onRetry,
                onReportIssue = {
                    // TODO: Implement issue reporting
                    // For now, we'll just log it
                    println("User reported issue: $error")
                },
                hapticManager = hapticManager
            )
        }
    }
    
    content()
}