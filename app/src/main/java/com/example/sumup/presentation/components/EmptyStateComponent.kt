package com.example.sumup.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Different types of empty states
 */
enum class EmptyStateType {
    HISTORY_EMPTY,
    SEARCH_NO_RESULTS,
    OCR_NO_TEXT,
    PDF_PROCESSING_FAILED,
    NO_SUMMARIES,
    CAMERA_PERMISSION_DENIED,
    NO_NETWORK_CONNECTION
}

/**
 * Reusable empty state component with different variations
 */
@Composable
fun EmptyStateComponent(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null
) {
    val emptyStateData = getEmptyStateData(type)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = emptyStateData.icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = title ?: emptyStateData.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Description
        Text(
            text = description ?: emptyStateData.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Primary Action Button
        if (actionText != null && onActionClick != null) {
            Button(
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(actionText)
            }
        }
        
        // Secondary Action Button
        if (secondaryActionText != null && onSecondaryActionClick != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onSecondaryActionClick,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(secondaryActionText)
            }
        }
    }
}

/**
 * Compact empty state for smaller spaces
 */
@Composable
fun CompactEmptyState(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val emptyStateData = getEmptyStateData(type)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = emptyStateData.icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = emptyStateData.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = emptyStateData.description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * Empty state for list items with illustrations
 */
@Composable
fun IllustratedEmptyState(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val emptyStateData = getEmptyStateData(type)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Larger icon with background
            Card(
                modifier = Modifier.size(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = emptyStateData.icon,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = emptyStateData.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = emptyStateData.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            if (actionText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

/**
 * Data class for empty state configuration
 */
private data class EmptyStateData(
    val icon: ImageVector,
    val title: String,
    val description: String
)

/**
 * Get empty state data based on type
 */
private fun getEmptyStateData(type: EmptyStateType): EmptyStateData {
    return when (type) {
        EmptyStateType.HISTORY_EMPTY -> EmptyStateData(
            icon = Icons.Default.History,
            title = "No summaries yet",
            description = "Your summary history will appear here once you create your first summary."
        )
        EmptyStateType.SEARCH_NO_RESULTS -> EmptyStateData(
            icon = Icons.Default.SearchOff,
            title = "No results found",
            description = "Try adjusting your search terms or browse all summaries."
        )
        EmptyStateType.OCR_NO_TEXT -> EmptyStateData(
            icon = Icons.Default.DocumentScanner,
            title = "No text detected",
            description = "Make sure the image contains clear, readable text and try again."
        )
        EmptyStateType.PDF_PROCESSING_FAILED -> EmptyStateData(
            icon = Icons.Default.Error,
            title = "PDF processing failed",
            description = "Unable to extract text from this PDF. Try a different file or convert to a text-based PDF."
        )
        EmptyStateType.NO_SUMMARIES -> EmptyStateData(
            icon = Icons.Default.Article,
            title = "Start summarizing",
            description = "Add text, upload a PDF, or scan an image to create your first intelligent summary."
        )
        EmptyStateType.CAMERA_PERMISSION_DENIED -> EmptyStateData(
            icon = Icons.Default.CameraAlt,
            title = "Camera access needed",
            description = "Grant camera permission to scan text from images and documents."
        )
        EmptyStateType.NO_NETWORK_CONNECTION -> EmptyStateData(
            icon = Icons.Default.CloudOff,
            title = "No internet connection",
            description = "Check your internet connection and try again."
        )
    }
}