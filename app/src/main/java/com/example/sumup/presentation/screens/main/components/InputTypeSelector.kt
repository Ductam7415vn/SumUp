package com.example.sumup.presentation.screens.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sumup.presentation.screens.main.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTypeSelector(
    selectedType: MainUiState.InputType,
    onTypeSelected: (MainUiState.InputType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InputTypeChip(
            text = "Text",
            icon = Icons.Default.TextFields,
            isSelected = selectedType == MainUiState.InputType.TEXT,
            onClick = { onTypeSelected(MainUiState.InputType.TEXT) },
            modifier = Modifier.weight(1f)
        )
        
        InputTypeChip(
            text = "PDF",
            icon = Icons.Default.PictureAsPdf,
            isSelected = selectedType == MainUiState.InputType.PDF,
            onClick = { onTypeSelected(MainUiState.InputType.PDF) },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputTypeChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = { 
            Icon(
                imageVector = icon, 
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            ) 
        },
        modifier = modifier
    )
}