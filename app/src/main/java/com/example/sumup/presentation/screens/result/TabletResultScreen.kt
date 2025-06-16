package com.example.sumup.presentation.screens.result

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.presentation.components.PredictiveBackGestureHandler
import com.example.sumup.presentation.screens.result.components.PersonaSelector
import com.example.sumup.presentation.screens.result.components.SummaryActionBar
import com.example.sumup.presentation.screens.result.components.SummaryKPICard
import com.example.sumup.presentation.utils.AdaptiveLayoutInfo
import com.example.sumup.presentation.utils.DeviceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    adaptiveInfo: AdaptiveLayoutInfo,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    PredictiveBackGestureHandler(
        enabled = true,
        onBackPressed = onNavigateBack
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = { 
                        Text(
                            text = "Summary Result",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(Icons.Default.History, contentDescription = "History")
                        }
                        IconButton(
                            onClick = { viewModel.toggleFavorite() },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (uiState.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left panel - Summary content
                Card(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Persona selector
                        if (uiState.availablePersonas.isNotEmpty()) {
                            PersonaSelector(
                                currentPersona = uiState.currentPersona,
                                onPersonaChange = viewModel::changePersona
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        
                        // Summary title
                        Text(
                            text = uiState.summaryTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Summary content with animations
                        AnimatedContent(
                            targetState = uiState.summaryText,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(300)) + 
                                slideInVertically(animationSpec = tween(300))) togetherWith
                                (fadeOut(animationSpec = tween(200)) + 
                                slideOutVertically(animationSpec = tween(200)))
                            },
                            label = "summaryContent"
                        ) { summaryText ->
                            Column {
                                summaryText.split("\n").forEach { paragraph ->
                                    if (paragraph.isNotBlank()) {
                                        Text(
                                            text = paragraph,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Right panel - Metrics and actions
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // KPI Cards
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Summary Metrics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            SummaryKPICard(
                                originalWords = uiState.originalWordCount,
                                summaryWords = uiState.summaryWordCount,
                                originalReadTime = (uiState.originalWordCount / 200), // Assume 200 words per minute
                                summaryReadTime = (uiState.summaryWordCount / 200),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Action buttons
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Copy button
                            OutlinedButton(
                                onClick = { viewModel.copySummary(context) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isRegenerating
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy Summary")
                            }
                            
                            // Share button
                            OutlinedButton(
                                onClick = { viewModel.shareSummary() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isRegenerating
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share")
                            }
                            
                            // Export button
                            OutlinedButton(
                                onClick = { viewModel.exportSummary() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isRegenerating
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export PDF")
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Regenerate button
                            Button(
                                onClick = { viewModel.regenerateSummary() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isRegenerating
                            ) {
                                if (uiState.isRegenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (uiState.isRegenerating) "Regenerating..." else "Regenerate")
                            }
                        }
                    }
                    
                    // Summary info
                    if (uiState.generatedAt > 0) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Generated",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(uiState.generatedAt)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                if (uiState.summaryLanguage.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Language",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = uiState.summaryLanguage,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Success snackbar
        if (uiState.showCopySuccess) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                viewModel.dismissCopySuccess()
            }
        }
    }
}