package com.example.sumup.presentation.screens.main.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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

    Text(
        text = "$current/$max characters",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier
    )
}