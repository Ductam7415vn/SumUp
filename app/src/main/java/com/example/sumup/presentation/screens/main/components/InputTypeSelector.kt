package com.example.sumup.presentation.screens.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sumup.presentation.screens.main.MainUiState

@Composable
fun InputTypeSelectorAnimated(
    selectedType: MainUiState.InputType,
    onTypeSelected: (MainUiState.InputType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Button
            TextPdfButton(
                text = "Text",
                icon = Icons.Default.TextFields,
                isSelected = selectedType == MainUiState.InputType.TEXT,
                onClick = { onTypeSelected(MainUiState.InputType.TEXT) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            
            // Document Button
            TextPdfButton(
                text = "Document",
                icon = Icons.Default.Description,
                isSelected = selectedType == MainUiState.InputType.DOCUMENT,
                onClick = { onTypeSelected(MainUiState.InputType.DOCUMENT) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun TextPdfButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(300),
        label = "backgroundColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "contentColor"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTypeSelector(
    selectedType: MainUiState.InputType,
    onTypeSelected: (MainUiState.InputType) -> Unit,
    modifier: Modifier = Modifier
) {
    // Delegate to the custom implementation to avoid SegmentedButton visual bugs
    InputTypeSelectorAnimated(
        selectedType = selectedType,
        onTypeSelected = onTypeSelected,
        modifier = modifier
    )
}


