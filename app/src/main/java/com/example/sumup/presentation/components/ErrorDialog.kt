package com.example.sumup.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import com.example.sumup.domain.model.AppError
import com.example.sumup.presentation.utils.getErrorInfo

@Composable
fun ErrorDialog(
    error: AppError,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    val errorInfo = getErrorInfo(error)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(errorInfo.icon, style = MaterialTheme.typography.displayMedium) },
        title = { Text(errorInfo.title) },
        text = { 
            Text(
                text = errorInfo.message,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Text("Try Again")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        },
        dismissButton = if (onRetry != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        } else null
    )
}