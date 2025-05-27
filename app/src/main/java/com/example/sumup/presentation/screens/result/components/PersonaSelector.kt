package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.SummaryPersona

@Composable
fun PersonaSelector(
    currentPersona: SummaryPersona,
    onPersonaChange: (SummaryPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SummaryPersona.values()) { persona ->
            PersonaChip(
                persona = persona,
                isSelected = persona == currentPersona,
                onClick = { onPersonaChange(persona) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaChip(
    persona: SummaryPersona,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chip_scale"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "chip_color"
    )
    
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { 
            Text(
                text = getPersonaLabel(persona),
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

private fun getPersonaLabel(persona: SummaryPersona): String {
    return when (persona) {
        SummaryPersona.GENERAL -> "General"
        SummaryPersona.STUDY -> "Study"
        SummaryPersona.BUSINESS -> "Business"
        SummaryPersona.LEGAL -> "Legal"
        SummaryPersona.TECHNICAL -> "Technical"
        SummaryPersona.QUICK -> "Quick"
    }
}