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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (String) -> Unit = {},
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
            
            // Content
            if (uiState.isEmpty) {
                EmptyState(
                    searchQuery = searchQuery,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
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
                                        onNavigateToResult(summary.id)
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

@Composable
private fun EmptyState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (searchQuery.isNotEmpty()) "No results found" else "No history yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (searchQuery.isNotEmpty()) 
                    "Try a different search term" 
                else 
                    "Your summaries will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
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