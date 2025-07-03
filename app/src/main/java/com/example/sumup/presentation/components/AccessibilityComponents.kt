package com.example.sumup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

/**
 * Accessibility utilities for the app
 */
object AccessibilityUtils {
    fun isScreenReaderEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled
    }
    
    fun announceForAccessibility(context: Context, message: String) {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            event.text.add(message)
            accessibilityManager.sendAccessibilityEvent(event)
        }
    }
}

/**
 * Accessible button with proper semantics
 */
@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    text: String,
    contentDescription: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val isScreenReaderOn = remember { AccessibilityUtils.isScreenReaderEnabled(context) }
    
    Button(
        onClick = {
            if (!isLoading) {
                onClick()
                if (isScreenReaderOn) {
                    AccessibilityUtils.announceForAccessibility(context, "Action performed: $text")
                }
            }
        },
        enabled = enabled && !isLoading,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription ?: text
            if (isLoading) {
                this.stateDescription = "Loading"
            }
            this.role = Role.Button
        }
    ) {
        if (isLoading && isScreenReaderOn) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text)
    }
}

/**
 * Accessible card with proper semantics
 */
@Composable
fun AccessibleCard(
    onClick: (() -> Unit)? = null,
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val semanticsModifier = if (onClick != null) {
        Modifier.semantics {
            contentDescription = buildString {
                append(title)
                description?.let {
                    append(". ")
                    append(it)
                }
            }
            role = Role.Button
        }
    } else {
        Modifier.semantics {
            heading()
        }
    }
    
    Card(
        onClick = onClick ?: {},
        modifier = modifier.then(semanticsModifier),
        enabled = onClick != null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Accessible radio button group
 */
@Composable
fun AccessibleRadioGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.semantics {
            contentDescription = "$label. Current selection: $selectedOption"
            selectableGroup()
        }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.semantics { heading() }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp)
                    .semantics {
                        contentDescription = "$option. ${if (option == selectedOption) "Selected" else "Not selected"}"
                    }
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = null // onClick is handled by selectable modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option)
            }
        }
    }
}

/**
 * Accessible text field with proper labels
 */
@Composable
fun AccessibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    maxLength: Int? = null
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val isScreenReaderOn = remember { AccessibilityUtils.isScreenReaderEnabled(context) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            isError = errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .semantics {
                    contentDescription = buildString {
                        append(label)
                        if (value.isEmpty() && placeholder != null) {
                            append(". ")
                            append(placeholder)
                        }
                        if (value.isNotEmpty()) {
                            append(". Current text: ")
                            append(value)
                        }
                        maxLength?.let {
                            append(". Character limit: ")
                            append("${value.length} of $it")
                        }
                        errorMessage?.let {
                            append(". Error: ")
                            append(it)
                        }
                    }
                    if (errorMessage != null) {
                        error(errorMessage)
                    }
                },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    maxLength?.let {
                        Text(
                            text = "${value.length}/$it",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (value.length > it * 0.9) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        )
        
        // Announce character count changes for screen readers
        if (isScreenReaderOn && maxLength != null) {
            LaunchedEffect(value.length) {
                when {
                    value.length == maxLength -> {
                        AccessibilityUtils.announceForAccessibility(
                            context,
                            "Character limit reached"
                        )
                    }
                    value.length > maxLength * 0.9 -> {
                        AccessibilityUtils.announceForAccessibility(
                            context,
                            "${maxLength - value.length} characters remaining"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Skip to content button for keyboard navigation
 */
@Composable
fun SkipToContentButton(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    
    TextButton(
        onClick = onSkip,
        modifier = modifier
            .focusRequester(focusRequester)
            .semantics {
                contentDescription = "Skip to main content"
            }
    ) {
        Text("Skip to content")
    }
    
    // Request focus on first composition for keyboard users
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

/**
 * Loading indicator with proper announcements
 */
@Composable
fun AccessibleLoadingIndicator(
    message: String = "Loading",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier.semantics {
            contentDescription = "$message. Please wait."
            liveRegion = LiveRegionMode.Polite
        }
    ) {
        CircularProgressIndicator()
    }
    
    // Announce loading state
    LaunchedEffect(Unit) {
        AccessibilityUtils.announceForAccessibility(context, message)
    }
}

/**
 * Accessible icon button with tooltip
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibleIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    contentDescription: String,
    tooltip: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (tooltip != null) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(tooltip)
                }
            },
            state = rememberTooltipState()
        ) {
            IconButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier.semantics {
                    this.contentDescription = contentDescription
                    this.role = Role.Button
                }
            ) {
                icon()
            }
        }
    } else {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.semantics {
                this.contentDescription = contentDescription
                this.role = Role.Button
            }
        ) {
            icon()
        }
    }
}