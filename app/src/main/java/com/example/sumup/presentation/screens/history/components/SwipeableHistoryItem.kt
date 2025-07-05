package com.example.sumup.presentation.screens.history.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.sumup.presentation.components.rememberSwipeHapticFeedback
import com.example.sumup.presentation.components.animations.AnimatedFavoriteIcon
import com.example.sumup.utils.haptic.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.Summary
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SwipeableHistoryItem(
    summary: Summary,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticManager = rememberHapticFeedback()
    val swipeHaptic = rememberSwipeHapticFeedback()
    
    val deleteAction = SwipeAction(
        onSwipe = {
            swipeHaptic()
            onDelete()
        },
        icon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        },
        background = Color.Red
    )
    
    val shareAction = SwipeAction(
        onSwipe = {
            swipeHaptic()
            onShare()
        },
        icon = {
            Icon(
                Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White
            )
        },
        background = Color(0xFF4CAF50)
    )
    
    SwipeableActionsBox(
        startActions = listOf(shareAction),
        endActions = listOf(deleteAction),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        HistoryItemCard(
            summary = summary,
            isSelected = isSelected,
            isSelectionMode = isSelectionMode,
            onClick = onClick,
            onLongClick = {
                hapticManager.performHapticFeedback(com.example.sumup.utils.haptic.HapticFeedbackType.LONG_PRESS)
                onLongClick()
            },
            onToggleFavorite = onToggleFavorite
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryItemCard(
    summary: Summary,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val hapticManager = rememberHapticFeedback()
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "selection_color"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox in selection mode
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // First bullet point as title
                Text(
                    text = summary.bulletPoints.firstOrNull() ?: "Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Preview of other bullet points
                if (summary.bulletPoints.size > 1) {
                    Text(
                        text = summary.bulletPoints.drop(1).take(2).joinToString(" • "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTimestamp(summary.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${summary.metrics.originalWordCount}→${summary.metrics.summaryWordCount} words",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        if (!isSelectionMode) {
                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier.size(28.dp)
                            ) {
                                AnimatedFavoriteIcon(
                                    isFavorite = summary.isFavorite,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 172_800_000 -> "Yesterday"
        else -> {
            val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}