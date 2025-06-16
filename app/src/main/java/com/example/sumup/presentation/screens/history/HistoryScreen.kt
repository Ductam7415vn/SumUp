package com.example.sumup.presentation.screens.history

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val context = LocalContext.current
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    
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
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
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
            // Search bar
            AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onClose = { 
                        showSearchBar = false
                        viewModel.updateSearchQuery("")
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Summary count
            if (uiState.totalCount > 0 && !uiState.isSelectionMode && !showSearchBar) {
                Text(
                    text = "${uiState.totalCount} summaries",
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
                        if (searchQuery.isNotEmpty()) {
                            EmptyStateComponent(
                                type = EmptyStateType.SEARCH_NO_RESULTS,
                                modifier = Modifier.fillMaxSize(),
                                actionText = "Clear Search",
                                onActionClick = { viewModel.updateSearchQuery("") }
                            )
                        } else {
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
    }
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