package com.example.sumup.presentation.screens.main.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode

@Composable
fun CharacterCounter(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val ratio = current.toFloat() / max
    val color = when {
        ratio > 0.95 -> MaterialTheme.colorScheme.error
        ratio > 0.9 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val description = when {
        ratio > 0.95 -> "Character limit warning: $current of $max characters used. ${max - current} characters remaining."
        ratio > 0.9 -> "Approaching character limit: $current of $max characters used."
        else -> "$current of $max characters used."
    }
    
    Text(
        text = "$current/$max characters",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier.semantics {
            contentDescription = description
            // Announce changes when approaching limit
            if (ratio > 0.9) {
                liveRegion = LiveRegionMode.Polite
            }
        }
    )
}