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
        android.util.Log.e("SmartErrorHandler", "=== ERROR DETECTED ===")
        android.util.Log.e("SmartErrorHandler", "Error type: ${it.javaClass.simpleName}")
        android.util.Log.e("SmartErrorHandler", "Error message: ${it.message}")
        android.util.Log.e("SmartErrorHandler", "Is form context: $isFormContext")
        
        if (!isFormContext || error !is AppError.TextTooShortError) {
            android.util.Log.e("SmartErrorHandler", "Showing error dialog")
            AdvancedErrorDialog(
                error = it,
                onDismiss = onDismiss,
                onRetry = onRetry,
                onReportIssue = {
                    // TODO: Implement issue reporting
                    // For now, we'll just log it
                    android.util.Log.d("SmartErrorHandler", "User reported issue: $error")
                },
                hapticManager = hapticManager
            )
        }
    }
    
    content()
}