package com.example.sumup.presentation.screens.main.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.sumup.presentation.components.SharedElementKeys
import com.example.sumup.presentation.components.animations.BouncyButton

@Composable
fun BottomActionBar(
    canSummarize: Boolean,
    onClear: () -> Unit,
    onSummarize: () -> Unit,
    isLoading: Boolean = false,
    hasText: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
            // Clear button with animation
        // Clear button with bouncy animation
        AnimatedVisibility(
            visible = hasText,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            BouncyButton(
                onClick = onClear,
                modifier = Modifier.testTag("clear_button")
            ) {
                Text("Clear All", color = MaterialTheme.colorScheme.error)
            }
        }
        // Summarize button with haptic feedback and shared element key
        SummarizeButton(
            enabled = canSummarize,
            loading = isLoading,
            onClick = onSummarize,
            modifier = Modifier
                .testTag("summarize_button")
                .testTag(SharedElementKeys.MAIN_SUMMARY_BUTTON)
        )
    }
}

@Composable
private fun SummarizeButton(
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonScale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        animationSpec = tween(200),
        label = "content_alpha"
    )
    
    BouncyButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .widthIn(min = 140.dp)
            .scale(buttonScale)
    ) {
        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                if (targetState) {
                    (scaleIn(initialScale = 0.8f) + fadeIn()).togetherWith(
                        scaleOut(targetScale = 1.2f) + fadeOut()
                    )
                } else {
                    (scaleIn(initialScale = 1.2f) + fadeIn()).togetherWith(
                        scaleOut(targetScale = 0.8f) + fadeOut()
                    )
                }
            },
            label = "loading_content"
        ) { isLoading ->
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Processing...",
                        modifier = Modifier.alpha(contentAlpha)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Summarize",
                        modifier = Modifier.alpha(contentAlpha)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .alpha(contentAlpha)
                    )
                }
            }
        }
    }
}