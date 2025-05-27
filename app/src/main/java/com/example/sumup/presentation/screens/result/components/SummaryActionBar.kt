package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SummaryActionBar(
    summaryText: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var copyButtonState by remember { mutableStateOf<CopyButtonState>(CopyButtonState.Default) }
    val scope = rememberCoroutineScope()
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Copy Button with state feedback
            AnimatedCopyButton(
                state = copyButtonState,
                onClick = {
                    if (copyButtonState == CopyButtonState.Default) {
                        copyButtonState = CopyButtonState.Copying
                        onCopy()
                        scope.launch {
                            delay(300)
                            copyButtonState = CopyButtonState.Copied
                            delay(2000)
                            copyButtonState = CopyButtonState.Default
                        }
                    }
                }
            )
            
            // Share Button
            ActionButton(
                icon = Icons.Outlined.Share,
                text = "Share",
                onClick = onShare
            )
            
            // Save Button
            ActionButton(
                icon = Icons.Outlined.BookmarkBorder,
                text = "Save",
                onClick = onSave
            )
            
            // Regenerate Button
            ActionButton(
                icon = Icons.Outlined.Refresh,
                text = "Retry",
                onClick = onRegenerate
            )
        }
    }
}

sealed class CopyButtonState {
    object Default : CopyButtonState()
    object Copying : CopyButtonState()
    object Copied : CopyButtonState()
}

@Composable
fun AnimatedCopyButton(
    state: CopyButtonState,
    onClick: () -> Unit
) {
    val buttonColor by animateColorAsState(
        targetValue = when (state) {
            CopyButtonState.Default -> MaterialTheme.colorScheme.surfaceVariant
            CopyButtonState.Copying -> MaterialTheme.colorScheme.secondaryContainer
            CopyButtonState.Copied -> MaterialTheme.colorScheme.tertiaryContainer
        },
        label = "copy_button_color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = when (state) {
            CopyButtonState.Default -> MaterialTheme.colorScheme.onSurfaceVariant
            CopyButtonState.Copying -> MaterialTheme.colorScheme.onSecondaryContainer
            CopyButtonState.Copied -> MaterialTheme.colorScheme.onTertiaryContainer
        },
        label = "copy_content_color"
    )
    
    val scale by animateFloatAsState(
        targetValue = when (state) {
            CopyButtonState.Copying -> 0.95f
            CopyButtonState.Copied -> 1.1f
            else -> 1f
        },
        animationSpec = spring(),
        label = "copy_button_scale"
    )
    
    ActionButton(
        icon = when (state) {
            CopyButtonState.Default -> Icons.Outlined.ContentCopy
            CopyButtonState.Copying -> Icons.Outlined.ContentCopy
            CopyButtonState.Copied -> Icons.Filled.Check
        },
        text = when (state) {
            CopyButtonState.Default -> "Copy"
            CopyButtonState.Copying -> "..."
            CopyButtonState.Copied -> "Copied!"
        },
        onClick = onClick,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        containerColor = buttonColor,
        contentColor = contentColor
    )
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}