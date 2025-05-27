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
import kotlinx.coroutines.delay

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
        // Clear button with animation
        TextButton(
            onClick = onClear,
            enabled = hasText,
            modifier = Modifier
                .alpha(if (hasText) 1f else 0f)
                .testTag("clear_button")
        ) {
            Text("Clear All")
        }
        // Summarize button with animation
        SummarizeButton(
            enabled = canSummarize,
            loading = isLoading,
            onClick = onSummarize,
            modifier = Modifier.testTag("summarize_button")
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
    var isPressed by remember { mutableStateOf(false) }
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        enabled = enabled && !loading,
        modifier = modifier
            .widthIn(min = 140.dp)
            .scale(buttonScale)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Summarize")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}