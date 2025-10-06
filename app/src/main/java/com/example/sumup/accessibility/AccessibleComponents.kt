package com.example.sumup.accessibility

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp

/**
 * Accessible Button with proper semantics
 * AC-NFR5.14: All interactive elements have minimum 48dp touch target
 */
@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    icon: ImageVector? = null,
    contentDescription: String? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = WCAGCompliance.MIN_TOUCH_TARGET_SIZE.dp)
            .semantics {
                this.contentDescription = contentDescription ?: label
                this.role = Role.Button
                if (!enabled) {
                    disabled()
                }
            },
        enabled = enabled
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Handled by button's contentDescription
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(label)
    }
}

/**
 * Accessible Icon Button
 */
@Composable
fun AccessibleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(WCAGCompliance.MIN_TOUCH_TARGET_SIZE.dp)
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Button
                if (!enabled) {
                    disabled()
                }
            },
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null // Already set on IconButton
        )
    }
}

/**
 * Accessible Text Field with proper labels and hints
 */
@Composable
fun AccessibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = label
                if (placeholder != null && value.isEmpty()) {
                    stateDescription = placeholder
                }
                if (isError && errorMessage != null) {
                    error(errorMessage)
                }
                if (!enabled) {
                    disabled()
                }
                if (readOnly) {
                    this.contentDescription = "$label, read-only"
                }
            },
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        maxLines = maxLines,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage) }
        } else null
    )
}

/**
 * Accessible Switch with state announcement
 */
@Composable
fun AccessibleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null
) {
    Row(
        modifier = modifier
            .heightIn(min = WCAGCompliance.MIN_TOUCH_TARGET_SIZE.dp)
            .semantics(mergeDescendants = true) {
                this.contentDescription = description ?: "$label, ${if (checked) "on" else "off"}"
                this.role = Role.Switch
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
                if (!enabled) {
                    disabled()
                }
            }
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

/**
 * Accessible Checkbox with proper semantics
 */
@Composable
fun AccessibleCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .heightIn(min = WCAGCompliance.MIN_TOUCH_TARGET_SIZE.dp)
            .semantics(mergeDescendants = true) {
                this.role = Role.Checkbox
                this.contentDescription = "$label, ${if (checked) "checked" else "unchecked"}"
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
                if (!enabled) {
                    disabled()
                }
            }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Accessible Card with proper semantics
 */
@Composable
fun AccessibleCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (contentDescription != null) {
                    Modifier.semantics {
                        this.contentDescription = contentDescription
                        if (onClick != null) {
                            this.role = Role.Button
                        }
                        if (!enabled) {
                            disabled()
                        }
                    }
                } else {
                    Modifier
                }
            ),
        enabled = enabled && onClick != null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Accessible Snackbar with proper duration and action
 */
@Composable
fun AccessibleSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.semantics {
            liveRegion = LiveRegionMode.Polite
            contentDescription = snackbarData.visuals.message
        },
        action = snackbarData.visuals.actionLabel?.let {
            {
                TextButton(
                    onClick = { snackbarData.performAction() },
                    modifier = Modifier.semantics {
                        contentDescription = it
                        role = Role.Button
                    }
                ) {
                    Text(it)
                }
            }
        }
    ) {
        Text(snackbarData.visuals.message)
    }
}

/**
 * Accessible Dialog with proper focus management
 */
@Composable
fun AccessibleAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    titleContentDescription: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier.semantics {
            isTraversalGroup = true
            titleContentDescription?.let {
                contentDescription = it
            }
        },
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text
    )
}

/**
 * Accessible Tab Row with proper semantics
 */
@Composable
fun AccessibleTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.semantics {
            contentDescription = "Tab row with ${selectedTabIndex + 1} tabs"
        },
        tabs = tabs
    )
}

/**
 * Accessible Tab
 */
@Composable
fun AccessibleTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    position: Int,
    totalTabs: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .heightIn(min = WCAGCompliance.MIN_TOUCH_TARGET_SIZE.dp)
            .semantics {
                this.role = Role.Tab
                this.selected = selected
                contentDescription = "$text tab, ${position + 1} of $totalTabs"
                stateDescription = if (selected) "Selected" else "Not selected"
                if (!enabled) {
                    disabled()
                }
            },
        enabled = enabled,
        text = { Text(text) },
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null // Handled by tab's contentDescription
                )
            }
        }
    )
}

/**
 * Accessible Progress Indicator with state announcement
 */
@Composable
fun AccessibleLinearProgressIndicator(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "$label: ${(progress * 100).toInt()}%",
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
                    contentDescription = "$label: ${(progress * 100).toInt()}% complete"
                }
        )
    }
}

/**
 * Accessible Chip with proper semantics
 */
@Composable
fun AccessibleFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier
            .heightIn(min = WCAGCompliance.MIN_TOUCH_TARGET_SIZE.dp)
            .semantics {
                this.role = Role.Checkbox
                toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
                contentDescription = "$label, ${if (selected) "selected" else "not selected"}"
                if (!enabled) {
                    disabled()
                }
            },
        enabled = enabled,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        }
    )
}
