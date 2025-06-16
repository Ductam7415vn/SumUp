package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.utils.drafts.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay

/**
 * Enhanced TextField with auto-save functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoSaveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    // Auto-save specific parameters
    autoSaveEnabled: Boolean = true,
    draftInputType: DraftInputType = DraftInputType.TEXT,
    showAutoSaveStatus: Boolean = true,
    onDraftRestored: ((String) -> Unit)? = null,
    draftManager: DraftManager? = null
) {
    val context = LocalContext.current
    val actualDraftManager = draftManager ?: remember { DraftManager(context) }
    
    // Auto-save state
    var autoSaveStatus by remember { mutableStateOf(AutoSaveStatus.IDLE) }
    var lastSaveTime by remember { mutableStateOf(0L) }
    
    // Focus state for better auto-save timing
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    
    // Debounced auto-save effect
    LaunchedEffect(value, autoSaveEnabled) {
        if (autoSaveEnabled && value.isNotBlank() && isFocused) {
            autoSaveStatus = AutoSaveStatus.PENDING
            delay(2000) // 2 second debounce
            
            try {
                actualDraftManager.saveDraft(value, draftInputType)
                autoSaveStatus = AutoSaveStatus.SAVED
                lastSaveTime = System.currentTimeMillis()
                
                // Reset to idle after showing saved status
                delay(2000)
                autoSaveStatus = AutoSaveStatus.IDLE
            } catch (e: Exception) {
                autoSaveStatus = AutoSaveStatus.ERROR
                delay(3000)
                autoSaveStatus = AutoSaveStatus.IDLE
            }
        }
    }
    
    // Draft restoration on first composition
    LaunchedEffect(Unit) {
        if (autoSaveEnabled && value.isBlank()) {
            try {
                val draft = actualDraftManager.getCurrentDraft().first()
                if (draft.content.isNotBlank()) {
                    onDraftRestored?.invoke(draft.content) ?: onValueChange(draft.content)
                }
            } catch (e: Exception) {
                // Silently handle draft restoration errors
            }
        }
    }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                if (autoSaveEnabled && newValue.isNotBlank()) {
                    autoSaveStatus = AutoSaveStatus.TYPING
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            enabled = enabled,
            readOnly = readOnly,
            placeholder = placeholder,
            label = label,
            leadingIcon = leadingIcon,
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto-save status indicator
                    if (showAutoSaveStatus && autoSaveEnabled) {
                        AutoSaveStatusIndicator(
                            status = autoSaveStatus,
                            lastSaveTime = lastSaveTime
                        )
                    }
                    
                    // Original trailing icon
                    trailingIcon?.invoke()
                }
            },
            supportingText = {
                Column {
                    supportingText?.invoke()
                    
                    // Auto-save supporting text
                    if (showAutoSaveStatus && autoSaveEnabled) {
                        AutoSaveSupportingText(
                            status = autoSaveStatus,
                            characterCount = value.length,
                            wordCount = if (value.isBlank()) 0 else value.trim().split("\\s+".toRegex()).size
                        )
                    }
                }
            },
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines
        )
    }
}

/**
 * Auto-save status enumeration
 */
enum class AutoSaveStatus {
    IDLE,       // No auto-save activity
    TYPING,     // User is actively typing
    PENDING,    // Auto-save scheduled
    SAVING,     // Currently saving
    SAVED,      // Successfully saved
    ERROR       // Save failed
}

/**
 * Auto-save status indicator icon
 */
@Composable
private fun AutoSaveStatusIndicator(
    status: AutoSaveStatus,
    lastSaveTime: Long,
    modifier: Modifier = Modifier
) {
    val (icon, color, rotation) = when (status) {
        AutoSaveStatus.IDLE -> Triple(
            Icons.Default.CloudDone,
            MaterialTheme.colorScheme.onSurfaceVariant,
            0f
        )
        AutoSaveStatus.TYPING -> Triple(
            Icons.Default.Edit,
            MaterialTheme.colorScheme.primary,
            0f
        )
        AutoSaveStatus.PENDING -> Triple(
            Icons.Default.Schedule,
            MaterialTheme.colorScheme.tertiary,
            0f
        )
        AutoSaveStatus.SAVING -> Triple(
            Icons.Default.CloudSync,
            MaterialTheme.colorScheme.primary,
            360f
        )
        AutoSaveStatus.SAVED -> Triple(
            Icons.Default.CloudDone,
            MaterialTheme.colorScheme.primary,
            0f
        )
        AutoSaveStatus.ERROR -> Triple(
            Icons.Default.CloudOff,
            MaterialTheme.colorScheme.error,
            0f
        )
    }
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = if (status == AutoSaveStatus.SAVING) {
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            spring()
        },
        label = "auto_save_rotation"
    )
    
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "auto_save_color"
    )
    
    Icon(
        imageVector = icon,
        contentDescription = when (status) {
            AutoSaveStatus.IDLE -> "Auto-save ready"
            AutoSaveStatus.TYPING -> "Typing in progress"
            AutoSaveStatus.PENDING -> "Auto-save pending"
            AutoSaveStatus.SAVING -> "Saving draft"
            AutoSaveStatus.SAVED -> "Draft saved"
            AutoSaveStatus.ERROR -> "Save failed"
        },
        tint = animatedColor,
        modifier = modifier
            .size(16.dp)
            .graphicsLayer { rotationZ = animatedRotation }
    )
}

/**
 * Supporting text for auto-save status
 */
@Composable
private fun AutoSaveSupportingText(
    status: AutoSaveStatus,
    characterCount: Int,
    wordCount: Int,
    modifier: Modifier = Modifier
) {
    val statusText = when (status) {
        AutoSaveStatus.IDLE -> if (characterCount > 0) "Draft ready" else ""
        AutoSaveStatus.TYPING -> "Auto-save in 2s..."
        AutoSaveStatus.PENDING -> "Auto-saving..."
        AutoSaveStatus.SAVING -> "Saving draft..."
        AutoSaveStatus.SAVED -> "Draft saved"
        AutoSaveStatus.ERROR -> "Save failed - will retry"
    }
    
    val statsText = if (characterCount > 0) {
        "$characterCount characters, $wordCount words"
    } else ""
    
    val displayText = when {
        statusText.isNotBlank() && statsText.isNotBlank() -> "$statusText • $statsText"
        statusText.isNotBlank() -> statusText
        statsText.isNotBlank() -> statsText
        else -> ""
    }
    
    if (displayText.isNotBlank()) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
            color = when (status) {
                AutoSaveStatus.ERROR -> MaterialTheme.colorScheme.error
                AutoSaveStatus.SAVED -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = modifier,
            textAlign = TextAlign.Start
        )
    }
}

/**
 * Draft recovery dialog
 */
@Composable
fun DraftRecoveryDialog(
    isVisible: Boolean,
    draftContent: String,
    draftStats: DraftStats,
    onRestore: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Restore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Restore Draft?")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "We found an auto-saved draft from ${draftStats.lastSavedText.lowercase()}.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = draftContent.take(150) + if (draftContent.length > 150) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "${draftStats.characterCount} characters • ${draftStats.wordCount} words",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                HapticButton(
                    onClick = onRestore,
                    hapticType = com.example.sumup.utils.haptic.HapticFeedbackType.SUCCESS
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Keep Both")
                    }
                    TextButton(
                        onClick = onDiscard,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Discard")
                    }
                }
            },
            modifier = modifier
        )
    }
}