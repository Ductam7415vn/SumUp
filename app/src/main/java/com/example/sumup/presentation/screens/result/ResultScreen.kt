package com.example.sumup.presentation.screens.result

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.presentation.screens.result.components.*
import com.example.sumup.presentation.components.*
import com.example.sumup.presentation.components.animations.ConfettiCelebration
import com.example.sumup.utils.clipboard.ClipboardManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCelebration by remember { mutableStateOf(false) }
    
    // Show celebration when summary is first loaded
    LaunchedEffect(uiState.summary) {
        if (uiState.summary != null && !showCelebration) {
            showCelebration = true
            delay(3000) // Hide after 3 seconds
            showCelebration = false
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Summary Result") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Show more options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                }
            )
        }
    ) { paddingValues ->
        EnhancedLoadingState(
            isLoading = uiState.isLoading,
            hasData = uiState.summary != null,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            shimmerContent = {
                // Show shimmer while loading summary
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // KPI cards shimmer
                    item {
                        ShimmerKPIRow()
                    }
                    
                    // Persona selector shimmer
                    item {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )
                    }
                    
                    // Summary content shimmer
                    item {
                        ShimmerResultCard()
                    }
                }
            },
            actualContent = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // KPI Card
                    item {
                        SummaryKPICard(
                            originalWords = uiState.summary?.metrics?.originalWordCount ?: 0,
                            summaryWords = uiState.summary?.metrics?.summaryWordCount ?: 0,
                            originalReadTime = uiState.summary?.metrics?.originalReadingTime ?: 0,
                            summaryReadTime = uiState.summary?.metrics?.summaryReadingTime ?: 0
                        )
                    }
                    
                    // Persona Selector
                    item {
                        Column {
                            Text(
                                text = "Optimize for:",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            PersonaSelector(
                                currentPersona = uiState.selectedPersona,
                                onPersonaChange = viewModel::selectPersona
                            )
                        }
                    }
                    
                    // Divider
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    
                    // Summary paragraph
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Summary paragraph with animation
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically()
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Text(
                                        text = uiState.summary?.summary ?: "",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(16.dp),
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Key Points",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    // Bullet points
                    itemsIndexed(
                        items = if (uiState.showAllBullets) {
                            uiState.summary?.bulletPoints ?: emptyList()
                        } else {
                            uiState.summary?.bulletPoints?.take(3) ?: emptyList()
                        }
                    ) { index, bullet ->
                        BulletPoint(
                            text = bullet,
                            index = index,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    
                    // Show more button
                    if ((uiState.summary?.bulletPoints?.size ?: 0) > 3 && !uiState.showAllBullets) {
                        item {
                            TextButton(
                                onClick = { viewModel.toggleShowAllBullets() },
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text("Show more...")
                            }
                        }
                    }
                    
                    // Bottom spacing for action bar
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            },
            loadingContent = {
                // Show loading overlay when regenerating
                LoadingOverlay(
                    isVisible = uiState.isLoading && uiState.summary != null,
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
        
        // Fixed bottom action bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            SummaryActionBar(
                summaryText = buildSummaryText(uiState.summary),
                onCopy = {
                    viewModel.copySummary(context)
                },
                onShare = {
                    shareSummary(context, uiState.summary)
                },
                onSave = {
                    viewModel.saveSummary()
                },
                onRegenerate = {
                    viewModel.regenerateSummary()
                }
            )
        }
        
        // Show success snackbar
        if (uiState.showCopySuccess) {
            LaunchedEffect(Unit) {
                delay(2000)
                viewModel.dismissCopySuccess()
            }
        }
    }
        
        // Confetti celebration overlay
        ConfettiCelebration(
            isActive = showCelebration,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BulletPoint(
    text: String,
    index: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(index * 100L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Row(
            modifier = modifier.padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun buildSummaryText(summary: com.example.sumup.domain.model.Summary?): String {
    if (summary == null) return ""
    
    return buildString {
        // Add summary paragraph first
        appendLine("=== SUMMARY ===")
        appendLine(summary.summary)
        appendLine()
        
        // Then add bullet points
        appendLine("=== KEY POINTS ===")
        summary.bulletPoints.forEach { bullet ->
            appendLine("• $bullet")
        }
    }
}

private fun shareSummary(
    context: android.content.Context,
    summary: com.example.sumup.domain.model.Summary?
) {
    summary?.let { sum ->
        val shareText = buildString {
            appendLine("Summary created with SumUp:")
            appendLine()
            
            // Include summary paragraph
            appendLine("=== SUMMARY ===")
            appendLine(sum.summary)
            appendLine()
            
            // Include key points
            appendLine("=== KEY POINTS ===")
            sum.bulletPoints.forEach { bullet ->
                appendLine("• $bullet")
            }
            appendLine()
            appendLine("---")
            appendLine("Original: ${sum.metrics.originalWordCount} words")
            appendLine("Summary: ${sum.metrics.summaryWordCount} words (${sum.metrics.reductionPercentage}% reduction)")
            appendLine("Time saved: ~${sum.metrics.originalReadingTime - sum.metrics.summaryReadingTime} minutes")
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Summary from SumUp")
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share summary via...")
        context.startActivity(chooser)
    }
}