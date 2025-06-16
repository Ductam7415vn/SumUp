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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.AppError

@Composable
fun InlineErrorDisplay(
    error: AppError?,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    AnimatedVisibility(
        visible = error != null,
        enter = expandVertically(
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ) + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        error?.let { currentError ->
            val errorInfo = getInlineErrorInfo(currentError)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showIcon) {
                        Icon(
                            imageVector = errorInfo.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    Text(
                        text = errorInfo.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FieldErrorIndicator(
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = hasError,
        enter = scaleIn(
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.error
        )
    }
}

private data class InlineErrorInfo(
    val message: String,
    val icon: ImageVector
)

private fun getInlineErrorInfo(error: AppError): InlineErrorInfo = when (error) {
    is AppError.TextTooShortError -> InlineErrorInfo(
        message = "Add at least 50 characters for a meaningful summary",
        icon = Icons.Default.ShortText
    )
    is AppError.InvalidInputError -> InlineErrorInfo(
        message = "Text contains too many special characters",
        icon = Icons.Default.Warning
    )
    is AppError.StorageFullError -> InlineErrorInfo(
        message = "Storage limit reached - delete old summaries",
        icon = Icons.Default.Storage
    )
    else -> InlineErrorInfo(
        message = error.message,
        icon = Icons.Default.ErrorOutline
    )
}