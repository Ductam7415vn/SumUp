package com.example.sumup.presentation.screens.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.presentation.screens.history.DateFilter
import com.example.sumup.presentation.screens.history.SummaryFilters
import com.example.sumup.presentation.screens.main.MainUiState.InputType

/**
 * Filter bottom sheet for History screen (UC-007 AC-007.2)
 *
 * Filter options:
 * - Date range (Today, Yesterday, Last 7/30 days, Custom)
 * - Persona (All 6 personas)
 * - Input type (TEXT, PDF, DOCX, OCR)
 * - Favorites only toggle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    isVisible: Boolean,
    currentFilters: SummaryFilters,
    onDismiss: () -> Unit,
    onDateFilterChange: (DateFilter) -> Unit,
    onPersonaToggle: (SummaryPersona) -> Unit,
    onInputTypeToggle: (InputType) -> Unit,
    onFavoritesToggle: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Summaries",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = onClearAll) {
                        Text("Clear All")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Date Filter Section
                    item {
                        FilterSection(title = "Date Range") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DateFilter.values().filter { it != DateFilter.CUSTOM }.forEach { filter ->
                                    FilterChipItem(
                                        label = filter.displayName,
                                        isSelected = currentFilters.dateFilter == filter,
                                        onClick = { onDateFilterChange(filter) }
                                    )
                                }
                            }
                        }
                    }

                    // Persona Filter Section
                    item {
                        FilterSection(title = "AI Persona") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SummaryPersona.values().forEach { persona ->
                                    FilterChipItem(
                                        label = persona.displayName,
                                        isSelected = persona in currentFilters.selectedPersonas,
                                        onClick = { onPersonaToggle(persona) },
                                        icon = getPersonaIcon(persona)
                                    )
                                }
                            }
                        }
                    }

                    // Input Type Filter Section
                    item {
                        FilterSection(title = "Input Type") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                InputType.values().forEach { type ->
                                    FilterChipItem(
                                        label = type.name,
                                        isSelected = type in currentFilters.selectedInputTypes,
                                        onClick = { onInputTypeToggle(type) },
                                        icon = getInputTypeIcon(type)
                                    )
                                }
                            }
                        }
                    }

                    // Favorites Toggle
                    item {
                        FilterSection(title = "Other") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Favorites Only",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Switch(
                                    checked = currentFilters.favoritesOnly,
                                    onCheckedChange = { onFavoritesToggle() }
                                )
                            }
                        }
                    }

                    // Apply button
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(label)
            }
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    )
}

private fun getPersonaIcon(persona: SummaryPersona): androidx.compose.ui.graphics.vector.ImageVector {
    return when (persona) {
        SummaryPersona.GENERAL -> Icons.Default.Person
        SummaryPersona.STUDY -> Icons.Default.School
        SummaryPersona.PROFESSIONAL -> Icons.Default.BusinessCenter
        SummaryPersona.ACADEMIC -> Icons.Default.MenuBook
        SummaryPersona.SIMPLE -> Icons.Default.TextFields
    }
}

private fun getInputTypeIcon(type: InputType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        InputType.TEXT -> Icons.Default.TextFields
        InputType.DOCUMENT -> Icons.Default.Description
        InputType.OCR -> Icons.Default.CameraAlt
    }
}
