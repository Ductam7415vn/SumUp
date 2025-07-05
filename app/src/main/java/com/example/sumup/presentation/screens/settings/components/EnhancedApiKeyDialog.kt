package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedApiKeyDialog(
    visible: Boolean,
    currentKey: String,
    isValidating: Boolean,
    errorMessage: String?,
    hasExistingKey: Boolean,
    onKeyChange: (String) -> Unit,
    onValidate: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    
    var showKey by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Animation states
    val dialogScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f)
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300)
    )
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isValidating,
            dismissOnClickOutside = !isValidating
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = dialogScale
                    scaleY = dialogScale
                    alpha = contentAlpha
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header with status indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.VpnKey,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = "Gemini API Key",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Text(
                            text = "Configure your AI-powered summarization",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Status badge
                    StatusBadge(
                        hasKey = hasExistingKey,
                        isValidating = isValidating,
                        hasError = errorMessage != null
                    )
                }
                
                // Main input section
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // API Key Input with enhanced features
                    OutlinedTextField(
                        value = currentKey,
                        onValueChange = onKeyChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API Key") },
                        placeholder = { Text("sk-...") },
                        singleLine = true,
                        visualTransformation = if (showKey) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Copy button
                                IconButton(
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        clipboardManager.setText(AnnotatedString(currentKey))
                                        scope.launch {
                                            showSuccessAnimation = true
                                            delay(2000)
                                            showSuccessAnimation = false
                                        }
                                    },
                                    enabled = currentKey.isNotBlank()
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = if (currentKey.isNotBlank()) 
                                            MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                // Visibility toggle
                                IconButton(onClick = { showKey = !showKey }) {
                                    Icon(
                                        imageVector = if (showKey) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = if (showKey) "Hide key" else "Show key",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { 
                                if (currentKey.isNotBlank()) onValidate() 
                            }
                        ),
                        isError = errorMessage != null,
                        supportingText = {
                            AnimatedContent(
                                targetState = when {
                                    errorMessage != null -> errorMessage
                                    showSuccessAnimation -> "Copied to clipboard!"
                                    else -> "Your key is encrypted and stored locally"
                                },
                                transitionSpec = {
                                    fadeIn() with fadeOut()
                                }
                            ) { text ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (showSuccessAnimation) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = text,
                                        color = when {
                                            errorMessage != null -> MaterialTheme.colorScheme.error
                                            showSuccessAnimation -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        },
                        enabled = !isValidating,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            errorBorderColor = MaterialTheme.colorScheme.error
                        )
                    )
                    
                    // Quick actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Paste from clipboard
                        OutlinedButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    clipboardManager.getText()?.let { clipText ->
                                        onKeyChange(clipText.text)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste")
                        }
                        
                        // Get API key
                        Button(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://makersuite.google.com/app/apikey"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(
                                Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Get Key")
                        }
                    }
                }
                
                // Info card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Free tier includes:",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "• 60 requests per minute\n• 1M tokens per month\n• Gemini 1.5 Flash model",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clear button (if has existing key)
                    if (hasExistingKey) {
                        TextButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onClear()
                            },
                            enabled = !isValidating,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear")
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Cancel button
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isValidating
                    ) {
                        Text("Cancel")
                    }
                    
                    // Validate button
                    Button(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onValidate()
                        },
                        enabled = currentKey.isNotBlank() && !isValidating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isValidating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isValidating) "Validating..." else "Validate")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    hasKey: Boolean,
    isValidating: Boolean,
    hasError: Boolean
) {
    val color = when {
        isValidating -> MaterialTheme.colorScheme.primary
        hasError -> MaterialTheme.colorScheme.error
        hasKey -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.outline
    }
    
    val text = when {
        isValidating -> "Validating"
        hasError -> "Invalid"
        hasKey -> "Active"
        else -> "Not Set"
    }
    
    val icon = when {
        isValidating -> null
        hasError -> Icons.Default.Error
        hasKey -> Icons.Default.CheckCircle
        else -> Icons.Default.Cancel
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isValidating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp,
                    color = color
                )
            } else if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}