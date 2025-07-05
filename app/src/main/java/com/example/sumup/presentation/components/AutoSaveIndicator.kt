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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.sumup.ui.theme.extendedColorScheme
import kotlinx.coroutines.delay

enum class SaveState {
    IDLE, SAVING, SAVED, ERROR
}

/**
 * Beautiful auto-save indicator that shows save status
 */
@Composable
fun AutoSaveIndicator(
    saveState: SaveState,
    modifier: Modifier = Modifier,
    onErrorClick: (() -> Unit)? = null
) {
    AnimatedContent(
        targetState = saveState,
        transitionSpec = {
            when (targetState) {
                SaveState.SAVING -> fadeIn() + scaleIn() togetherWith fadeOut()
                SaveState.SAVED -> fadeIn() + slideInVertically { -it } togetherWith fadeOut()
                else -> fadeIn() togetherWith fadeOut()
            }
        },
        modifier = modifier
    ) { state ->
        when (state) {
            SaveState.IDLE -> {
                // Show nothing when idle
                Box(modifier = Modifier.size(1.dp))
            }
            
            SaveState.SAVING -> {
                SaveStatusChip(
                    icon = {
                        val infiniteTransition = rememberInfiniteTransition(label = "saving")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing)
                            ),
                            label = "rotation"
                        )
                        
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Saving",
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer { rotationZ = rotation },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = "Saving...",
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            SaveState.SAVED -> {
                var visible by remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    delay(2000)
                    visible = false
                }
                
                AnimatedVisibility(
                    visible = visible,
                    exit = fadeOut() + scaleOut()
                ) {
                    SaveStatusChip(
                        icon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Saved",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = "Saved",
                        containerColor = Color(0xFFE8F5E9).copy(alpha = 0.8f),
                        contentColor = Color(0xFF1B5E20)
                    )
                }
            }
            
            SaveState.ERROR -> {
                SaveStatusChip(
                    icon = {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = "Save error",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    text = "Not saved",
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = onErrorClick
                )
            }
        }
    }
}

@Composable
private fun SaveStatusChip(
    icon: @Composable () -> Unit,
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: (() -> Unit)? = null
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = Modifier
            .alpha(animatedAlpha)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(
                text = text,
                fontSize = 12.sp,
                color = contentColor
            )
        }
    }
}

/**
 * Inline save indicator for forms
 */
@Composable
fun InlineSaveIndicator(
    isSaving: Boolean,
    lastSaved: Long?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else if (lastSaved != null) {
            Icon(
                Icons.Default.CloudDone,
                contentDescription = "Saved",
                modifier = Modifier.size(12.dp),
                tint = Color(0xFF4CAF50)
            )
        }
        
        Text(
            text = when {
                isSaving -> "Saving..."
                lastSaved != null -> "All changes saved"
                else -> ""
            },
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}