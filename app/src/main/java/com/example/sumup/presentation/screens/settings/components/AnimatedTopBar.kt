package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.presentation.components.HapticIconButton
import com.example.sumup.utils.haptic.HapticFeedbackType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    scrollOffset: Float = 0f,
    modifier: Modifier = Modifier
) {
    val isScrolled = scrollOffset > 100f
    
    val elevation by animateDpAsState(
        targetValue = if (isScrolled) 4.dp else 0.dp,
        animationSpec = tween(200),
        label = "topbar_elevation"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isScrolled) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(200),
        label = "topbar_background"
    )
    
    TopAppBar(
        modifier = modifier.graphicsLayer {
            shadowElevation = elevation.toPx()
        },
        title = {
            AnimatedTitle(
                text = title,
                isScrolled = isScrolled
            )
        },
        navigationIcon = {
            HapticIconButton(
                onClick = onNavigateBack,
                hapticType = HapticFeedbackType.NAVIGATION
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isScrolled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            navigationIconContentColor = if (isScrolled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    )
}

@Composable
private fun AnimatedTitle(
    text: String,
    isScrolled: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isScrolled) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "title_scale"
    )
    
    val titleColor by animateColorAsState(
        targetValue = if (isScrolled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onPrimary
        },
        animationSpec = tween(200),
        label = "title_color"
    )
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
        
        // Animated indicator
        if (!isScrolled) {
            AnimatedGlowIndicator()
        }
    }
}

@Composable
private fun AnimatedGlowIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = alpha),
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0f)
                    )
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            )
    )
}