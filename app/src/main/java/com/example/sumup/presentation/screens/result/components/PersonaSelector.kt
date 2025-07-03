package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.SummaryPersona
import kotlinx.coroutines.delay

@Composable
fun PersonaSelector(
    currentPersona: SummaryPersona,
    onPersonaChange: (SummaryPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(SummaryPersona.values().toList()) { index, persona ->
            var isVisible by remember { mutableStateOf(false) }
            
            LaunchedEffect(Unit) {
                delay(index * 50L)
                isVisible = true
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + scaleIn()
            ) {
                PersonaChip(
                    persona = persona,
                    isSelected = persona == currentPersona,
                    onClick = { onPersonaChange(persona) }
                )
            }
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

// Preview Composables
@ThemePreview
@Composable
fun PersonaSelectorPreview() {
    PreviewWrapper {
        PersonaSelector(
            currentPersona = SummaryPersona.GENERAL,
            onPersonaChange = {}
        )
    }
}

@Preview(name = "Persona Selector - Student Selected", showBackground = true)
@Composable
fun PersonaSelectorStudentPreview() {
    PreviewWrapper {
        PersonaSelector(
            currentPersona = SummaryPersona.STUDY,
            onPersonaChange = {}
        )
    }
}

@Preview(name = "Persona Selector - Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PersonaSelectorDarkPreview() {
    PreviewWrapper(darkTheme = true) {
        PersonaSelector(
            currentPersona = SummaryPersona.TECHNICAL,
            onPersonaChange = {}
        )
    }
}