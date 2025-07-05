package com.example.sumup.presentation.components.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import kotlinx.coroutines.delay

@Composable
fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val hapticManager = rememberHapticFeedback()
    val interactionSource = remember { MutableInteractionSource() }
    
    val animatedOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "switch_offset"
    )
    
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(200),
        label = "switch_background"
    )
    
    val thumbScale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "thumb_scale"
    )
    
    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(animatedBackgroundColor)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                hapticManager.performHapticFeedback(HapticFeedbackType.TICK)
                onCheckedChange(!checked)
            }
    ) {
        // Animated thumb
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .padding(2.dp)
                .size(24.dp)
                .scale(thumbScale)
                .clip(CircleShape)
                .background(Color.White)
        )
        
        // Pulse effect when toggled
        if (checked) {
            var showPulse by remember { mutableStateOf(true) }
            
            LaunchedEffect(checked) {
                showPulse = true
                delay(600)
                showPulse = false
            }
            
            if (showPulse) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(300),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_scale"
                )
                
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(2.dp)
                        .size(24.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
fun AnimatedSettingToggle(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            androidx.compose.material3.Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                androidx.compose.material3.Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        AnimatedSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}