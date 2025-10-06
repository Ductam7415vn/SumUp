package com.example.sumup.presentation.screens.history

import android.content.Intent
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.domain.model.Summary
import com.example.sumup.presentation.screens.history.components.*
import com.example.sumup.presentation.components.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onSummaryClick: (String) -> Unit = {},
    adaptiveInfo: com.example.sumup.presentation.utils.AdaptiveLayoutInfo? = null,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showClearAllDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState.isSelectionMode) {
                            "${uiState.selectedItems.size} selected"
                        } else {
                            "History"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.isSelectionMode) {
                                viewModel.exitSelectionMode()
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            if (uiState.isSelectionMode) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = if (uiState.isSelectionMode) "Exit selection" else "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.isSelectionMode) {
                        IconButton(onClick = viewModel::selectAll) {
                            Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                        }
                        IconButton(
                            onClick = { 
                                viewModel.deleteMultipleSummaries(uiState.selectedItems)
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected")
                        }
                    } else {
                        if (uiState.totalCount > 0) {
                            IconButton(onClick = { showClearAllDialog = true }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Enhanced Search Bar with Filter (UC-007)
            if (!uiState.isSelectionMode && uiState.totalCount > 0) {
                HistorySearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onClearSearch = viewModel::clearSearch,
                    onFilterClick = viewModel::showFilterBottomSheet,
                    activeFilterCount = filters.activeFilterCount,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Active filter chips (UC-007 AC-007.4)
            if (filters.hasActiveFilters && !uiState.isSelectionMode) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date filter chip
                    if (filters.dateFilter != DateFilter.ALL) {
                        item {
                            ActiveFilterChip(
                                label = filters.dateFilter.displayName,
                                onRemove = { viewModel.updateDateFilter(DateFilter.ALL) }
                            )
                        }
                    }

                    // Persona filter chips
                    items(filters.selectedPersonas.toList()) { persona ->
                        ActiveFilterChip(
                            label = persona.displayName,
                            onRemove = { viewModel.togglePersonaFilter(persona) }
                        )
                    }

                    // Input type filter chips
                    items(filters.selectedInputTypes.toList()) { type ->
                        ActiveFilterChip(
                            label = type.name,
                            onRemove = { viewModel.toggleInputTypeFilter(type) }
                        )
                    }

                    // Favorites chip
                    if (filters.favoritesOnly) {
                        item {
                            ActiveFilterChip(
                                label = "Favorites",
                                onRemove = viewModel::toggleFavoritesFilter
                            )
                        }
                    }

                    // Clear all button
                    if (filters.hasActiveFilters) {
                        item {
                            TextButton(onClick = viewModel::clearAllFilters) {
                                Text("Clear All")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Result count message (UC-007 AC-007.4)
            if (uiState.totalCount > 0 && !uiState.isSelectionMode) {
                Text(
                    text = uiState.resultCountMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Content with enhanced loading state
            EnhancedLoadingState(
                isLoading = uiState.isLoading,
                hasData = !uiState.isEmpty,
                modifier = Modifier.weight(1f),
                shimmerContent = {
                    // Show shimmer when loading for first time
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(6) {
                            ShimmerHistoryItem()
                        }
                    }
                },
                actualContent = {
                    if (uiState.isEmpty) {
                        if (uiState.hasActiveSearchOrFilter) {
                            // No results from search/filter
                            EmptyStateComponent(
                                type = EmptyStateType.SEARCH_NO_RESULTS,
                                modifier = Modifier.fillMaxSize(),
                                actionText = "Clear Filters",
                                onActionClick = {
                                    viewModel.clearSearch()
                                    viewModel.clearAllFilters()
                                }
                            )
                        } else {
                            // No summaries at all
                            EmptyStateComponent(
                                type = EmptyStateType.HISTORY_EMPTY,
                                modifier = Modifier.fillMaxSize(),
                                actionText = "Start Summarizing",
                                onActionClick = onNavigateBack
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            uiState.groupedSummaries.forEach { (timeframe, summaries) ->
                                stickyHeader {
                                    SectionHeader(
                                        title = timeframe,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.background)
                                    )
                                }
                                
                                items(
                                    items = summaries,
                                    key = { it.id }
                                ) { summary ->
                                    SwipeableHistoryItem(
                                        summary = summary,
                                        isSelected = summary.id in uiState.selectedItems,
                                        isSelectionMode = uiState.isSelectionMode,
                                        onClick = {
                                            if (uiState.isSelectionMode) {
                                                viewModel.toggleItemSelection(summary.id)
                                            } else {
                                                onSummaryClick(summary.id)
                                            }
                                        },
                                        onLongClick = {
                                            if (!uiState.isSelectionMode) {
                                                viewModel.enterSelectionMode(summary.id)
                                            }
                                        },
                                        onShare = { shareSummary(context, summary) },
                                        onDelete = { viewModel.deleteSummary(summary.id) },
                                        onToggleFavorite = { viewModel.toggleFavorite(summary.id) }
                                    )
                                }
                            }
                        }
                    }
                },
                loadingContent = {
                    // Show subtle loading overlay when refreshing data
                    LoadingOverlay(
                        isVisible = uiState.isLoading,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
        
        // Clear all dialog
        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                title = { Text("Clear All History?") },
                text = { 
                    Text("This will permanently delete all ${uiState.totalCount} summaries. This action cannot be undone.") 
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllHistory()
                            showClearAllDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Filter Bottom Sheet (UC-007)
        FilterBottomSheet(
            isVisible = uiState.showFilterBottomSheet,
            currentFilters = filters,
            onDismiss = viewModel::hideFilterBottomSheet,
            onDateFilterChange = viewModel::updateDateFilter,
            onPersonaToggle = viewModel::togglePersonaFilter,
            onInputTypeToggle = viewModel::toggleInputTypeFilter,
            onFavoritesToggle = viewModel::toggleFavoritesFilter,
            onClearAll = viewModel::clearAllFilters
        )
    }
}


/**
 * Active filter chip component (UC-007 AC-007.4)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveFilterChip(
    label: String,
    onRemove: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = onRemove,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove filter",
                modifier = Modifier.size(18.dp)
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

private fun shareSummary(context: android.content.Context, summary: Summary) {
    val shareText = buildString {
        appendLine("Summary created with SumUp:")
        appendLine()
        summary.bulletPoints.forEach { bullet ->
            appendLine("• $bullet")
        }
        appendLine()
        appendLine("---")
        appendLine("Original: ${summary.metrics.originalWordCount} words")
        appendLine("Summary: ${summary.metrics.summaryWordCount} words")
        appendLine("${summary.metrics.reductionPercentage}% reduction")
    }
    
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "Summary from SumUp")
    }
    
    context.startActivity(Intent.createChooser(shareIntent, "Share summary"))
}

// Preview Composables
@ThemePreview
@Composable
fun HistoryScreenPreview() {
    PreviewWrapper {
        HistoryScreen(
            onNavigateBack = {},
            onSummaryClick = {}
        )
    }
}

@Preview(name = "History Screen - Empty", showBackground = true)
@Composable
fun HistoryScreenEmptyPreview() {
    PreviewWrapper {
        HistoryScreen(
            onNavigateBack = {},
            onSummaryClick = {}
        )
    }
}

@Preview(name = "History Screen - With Items", showBackground = true)
@Composable
fun HistoryScreenWithItemsPreview() {
    PreviewWrapper {
        HistoryScreen(
            onNavigateBack = {},
            onSummaryClick = {}
        )
    }
}

@AllDevicePreview
@Composable
fun HistoryScreenDevicePreview() {
    PreviewWrapper {
        HistoryScreen(
            onNavigateBack = {},
            onSummaryClick = {}
        )
    }
}