package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun BlurDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit
) {
    if (visible) {
        var showContent by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            showContent = true
        }
        
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Blurred background
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(radius = 20.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onDismiss() }
                    )
                }
                
                // Dialog content
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(300)) + 
                            scaleIn(initialScale = 0.8f, animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )),
                    exit = fadeOut(animationSpec = tween(150)) + 
                           scaleOut(targetScale = 0.8f)
                ) {
                    DialogContent(
                        title = title,
                        onDismiss = onDismiss,
                        content = content
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogContent(
    title: String?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dialog_glow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight()
            .graphicsLayer {
                shadowElevation = 16.dp.toPx()
            },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Animated divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
                        )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Content
            content()
        }
    }
}

@Composable
fun AnimatedSelectionDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    options: List<Pair<String, String>>, // value to display name
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    BlurDialog(
        visible = visible,
        onDismiss = onDismiss,
        title = title
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, (value, displayName) ->
                AnimatedSelectionItem(
                    text = displayName,
                    isSelected = value == selectedOption,
                    onClick = {
                        onOptionSelected(value)
                        onDismiss()
                    },
                    animationDelay = index * 50
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Close button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun AnimatedSelectionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    animationDelay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -50 })
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            },
            animationSpec = tween(200),
            label = "item_background"
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() },
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio button
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}