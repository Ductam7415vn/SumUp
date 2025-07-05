package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.sumup.utils.haptic.*

/**
 * Enhanced Button with haptic feedback and animations
 */
@Composable
fun HapticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticType: HapticFeedbackType = HapticFeedbackType.CLICK,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: androidx.compose.foundation.BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val hapticManager = rememberHapticFeedback()
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )
    
    Button(
        onClick = {
            if (enabled) {
                hapticManager.performHapticFeedback(hapticType)
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { 
                isPressed = !isPressed 
            },
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Enhanced IconButton with haptic feedback
 */
@Composable
fun HapticIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticType: HapticFeedbackType = HapticFeedbackType.CLICK,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val hapticManager = rememberHapticFeedback()
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_button_scale"
    )
    
    IconButton(
        onClick = {
            if (enabled) {
                hapticManager.performHapticFeedback(hapticType)
                onClick()
            }
        },
        modifier = modifier.scale(scale),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )
    
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                    isPressed = true
                }
                is androidx.compose.foundation.interaction.PressInteraction.Release -> {
                    isPressed = false
                }
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isPressed = false
                }
            }
        }
    }
}

/**
 * Enhanced FloatingActionButton with haptic feedback
 */
@Composable
fun HapticFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    hapticType: HapticFeedbackType = HapticFeedbackType.CLICK,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val hapticManager = rememberHapticFeedback()
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_scale"
    )
    
    FloatingActionButton(
        onClick = {
            hapticManager.performHapticFeedback(hapticType)
            onClick()
        },
        modifier = modifier.scale(scale),
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        interactionSource = interactionSource,
        content = content
    )
    
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                    isPressed = true
                }
                is androidx.compose.foundation.interaction.PressInteraction.Release -> {
                    isPressed = false
                }
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isPressed = false
                }
            }
        }
    }
}

/**
 * Enhanced Switch with haptic feedback
 */
@Composable
fun HapticSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val hapticManager = rememberHapticFeedback()
    
    Switch(
        checked = checked,
        onCheckedChange = { newValue ->
            if (enabled && onCheckedChange != null) {
                hapticManager.performHapticFeedback(
                    if (newValue) HapticFeedbackType.SUCCESS else HapticFeedbackType.TICK
                )
                onCheckedChange(newValue)
            }
        },
        modifier = modifier,
        thumbContent = thumbContent,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

/**
 * Enhanced Checkbox with haptic feedback
 */
@Composable
fun HapticCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val hapticManager = rememberHapticFeedback()
    
    Checkbox(
        checked = checked,
        onCheckedChange = { newValue ->
            if (enabled && onCheckedChange != null) {
                hapticManager.performHapticFeedback(
                    if (newValue) HapticFeedbackType.SUCCESS else HapticFeedbackType.TICK
                )
                onCheckedChange(newValue)
            }
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

/**
 * Enhanced Slider with haptic feedback
 */
@Composable
fun HapticSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val hapticManager = rememberHapticFeedback()
    var lastValue by remember { mutableFloatStateOf(value) }
    
    Slider(
        value = value,
        onValueChange = { newValue ->
            // Provide haptic feedback for significant changes
            if (kotlin.math.abs(newValue - lastValue) > 0.1f) {
                hapticManager.performHapticFeedback(HapticFeedbackType.TICK)
                lastValue = newValue
            }
            onValueChange(newValue)
        },
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = {
            hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
            onValueChangeFinished?.invoke()
        },
        colors = colors,
        interactionSource = interactionSource
    )
}

/**
 * Enhanced Card with haptic feedback for click interactions
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HapticCard(
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: androidx.compose.foundation.BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val hapticManager = rememberHapticFeedback()
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .then(
                if (onClick != null || onLongClick != null) {
                    Modifier.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClickLabel = null,
                        role = Role.Button,
                        onLongClickLabel = null,
                        onLongClick = {
                            if (enabled && onLongClick != null) {
                                hapticManager.performHapticFeedback(HapticFeedbackType.LONG_PRESS)
                                onLongClick()
                            }
                        },
                        onClick = {
                            if (enabled && onClick != null) {
                                hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                                onClick()
                            }
                        }
                    )
                } else Modifier
            ),
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
    
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                    isPressed = true
                }
                is androidx.compose.foundation.interaction.PressInteraction.Release -> {
                    isPressed = false
                }
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isPressed = false
                }
            }
        }
    }
}

/**
 * Enhanced Chip with haptic feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HapticFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val hapticManager = rememberHapticFeedback()
    
    FilterChip(
        selected = selected,
        onClick = {
            if (enabled) {
                hapticManager.performHapticFeedback(
                    if (!selected) HapticFeedbackType.SUCCESS else HapticFeedbackType.TICK
                )
                onClick()
            }
        },
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        interactionSource = interactionSource
    )
}

/**
 * Convenience modifier for adding haptic feedback to any clickable component
 */
@Composable
fun Modifier.hapticClickable(
    hapticType: HapticFeedbackType = HapticFeedbackType.CLICK,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    val hapticManager = rememberHapticFeedback()
    return this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role
    ) {
        if (enabled) {
            hapticManager.performHapticFeedback(hapticType)
        }
        onClick()
    }
}

/**
 * Haptic feedback for swipe gestures
 */
@Composable
fun rememberSwipeHapticFeedback(): () -> Unit {
    val hapticManager = rememberHapticFeedback()
    return remember {
        {
            hapticManager.performHapticFeedback(HapticFeedbackType.SWIPE_DELETE)
        }
    }
}