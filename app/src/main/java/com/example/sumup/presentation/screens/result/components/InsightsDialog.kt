package com.example.sumup.presentation.screens.result.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sumup.domain.model.Summary
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InsightsDialog(
    summary: Summary?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Detailed Insights",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Deep dive into your summary analytics",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                
                if (summary != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Performance Metrics
                        item {
                            InsightSection(
                                icon = Icons.Outlined.Speed,
                                title = "Performance Metrics",
                                content = {
                                    MetricRow("Compression Rate", "${summary.metrics.reductionPercentage}%")
                                    MetricRow("Words Reduced", "${summary.metrics.originalWordCount - summary.metrics.summaryWordCount}")
                                    MetricRow("Reading Time Saved", "${summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime} min")
                                    MetricRow("Efficiency Score", calculateEfficiencyScore(summary))
                                }
                            )
                        }
                        
                        // Content Analysis
                        item {
                            InsightSection(
                                icon = Icons.Outlined.Analytics,
                                title = "Content Analysis",
                                content = {
                                    val aiMetrics = summary.aiQualityMetrics
                                    MetricRow("Key Points", "${summary.bulletPoints.size}")
                                    MetricRow("Average Point Length", "${summary.bulletPoints.map { it.length }.average().toInt()} chars")
                                    MetricRow("Readability Level", 
                                        aiMetrics?.readabilityLevel?.toDisplayString() ?: getReadabilityLevel(summary)
                                    )
                                    MetricRow("Information Density", 
                                        aiMetrics?.informationDensity?.toDisplayString() ?: getInformationDensity(summary)
                                    )
                                    if (aiMetrics != null) {
                                        MetricRow("Clarity Score", "${aiMetrics.clarityScore}%")
                                        MetricRow("Focus Score", "${aiMetrics.focusScore}%")
                                        MetricRow("Topic Coverage", "${aiMetrics.topicCoverage}%")
                                    }
                                }
                            )
                        }
                        
                        // Keywords Analysis
                        summary.keywords?.let { keywords ->
                            item {
                                InsightSection(
                                    icon = Icons.Outlined.Label,
                                    title = "Keyword Analysis",
                                    content = {
                                        Text(
                                            text = "Top Keywords Extracted",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            keywords.take(8).forEach { keyword ->
                                                KeywordChip(keyword)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        
                        // AI Quality Metrics
                        item {
                            InsightSection(
                                icon = Icons.Outlined.AutoAwesome,
                                title = "AI Quality Metrics",
                                content = {
                                    val aiMetrics = summary.aiQualityMetrics
                                    if (aiMetrics != null) {
                                        MetricRow("Coherence Score", "${aiMetrics.coherenceScore}%")
                                        MetricRow("Context Preservation", "${aiMetrics.contextPreservation}%")
                                        MetricRow("Information Retention", "${aiMetrics.informationRetention}%")
                                        MetricRow("Accuracy Score", "${aiMetrics.accuracyScore}%")
                                        MetricRow("Summary Type", summary.persona.displayName)
                                        MetricRow("Confidence", "${(aiMetrics.overallConfidence * 100).toInt()}%")
                                    } else {
                                        // Fallback for old summaries without AI metrics
                                        MetricRow("Coherence Score", "${calculateFallbackCoherence(summary)}%")
                                        MetricRow("Context Preservation", "High")
                                        MetricRow("Information Retention", "${calculateFallbackRetention(summary)}%")
                                        MetricRow("Summary Type", summary.persona.displayName)
                                    }
                                }
                            )
                        }
                        
                        // Advanced Analysis (if AI metrics available)
                        summary.aiQualityMetrics?.let { aiMetrics ->
                            item {
                                InsightSection(
                                    icon = Icons.Outlined.Psychology,
                                    title = "Advanced Analysis",
                                    content = {
                                        MetricRow("Structure Quality", "${aiMetrics.structureQuality}%")
                                        MetricRow("Transition Quality", "${aiMetrics.transitionQuality}%")
                                        MetricRow("Hierarchy Score", "${aiMetrics.hierarchyScore}%")
                                        MetricRow("Redundancy", "${100 - aiMetrics.redundancyScore}% unique")
                                        MetricRow("Processing Difficulty", aiMetrics.processingDifficulty.toDisplayString())
                                        MetricRow("Content Suitability", aiMetrics.contentSuitability.toDisplayString())
                                    }
                                )
                            }
                        }
                        
                        // Recommendations
                        item {
                            InsightSection(
                                icon = Icons.Outlined.Lightbulb,
                                title = "Recommendations",
                                content = {
                                    val aiMetrics = summary.aiQualityMetrics
                                    
                                    RecommendationItem("Try the ${getRecommendedPersona(summary)} persona for different insights")
                                    
                                    if (aiMetrics != null) {
                                        if (aiMetrics.readabilityLevel in listOf(
                                            com.example.sumup.domain.model.ReadabilityLevel.GRADUATE,
                                            com.example.sumup.domain.model.ReadabilityLevel.PROFESSIONAL
                                        )) {
                                            RecommendationItem("Consider using Simple persona for easier understanding")
                                        }
                                        
                                        if (aiMetrics.redundancyScore > 30) {
                                            RecommendationItem("High redundancy detected - try a shorter summary length")
                                        }
                                        
                                        if (aiMetrics.clarityScore < 70) {
                                            RecommendationItem("Clarity could be improved - try regenerating")
                                        }
                                    }
                                    
                                    RecommendationItem("Export to PDF for professional documentation")
                                    
                                    if (summary.bulletPoints.size > 10) {
                                        RecommendationItem("Consider using the brief overview for quick reference")
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No insights available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightSection(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun KeywordChip(keyword: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        modifier = Modifier.animateContentSize()
    ) {
        Text(
            text = keyword,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun RecommendationItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.TipsAndUpdates,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper functions
private fun calculateEfficiencyScore(summary: Summary): String {
    val score = (summary.metrics.reductionPercentage * 0.7f + 
                (summary.metrics.originalReadingTime - summary.metrics.summaryReadingTime) * 3f).toInt()
    return when {
        score >= 90 -> "Excellent"
        score >= 70 -> "Very Good"
        score >= 50 -> "Good"
        else -> "Fair"
    }
}

private fun getReadabilityLevel(summary: Summary): String {
    val avgWordLength = summary.summary.split(" ").map { it.length }.average()
    return when {
        avgWordLength < 4.5 -> "Easy"
        avgWordLength < 5.5 -> "Medium"
        else -> "Advanced"
    }
}

private fun getInformationDensity(summary: Summary): String {
    val density = summary.bulletPoints.size.toFloat() / summary.summary.split(" ").size * 100
    return when {
        density > 15 -> "High"
        density > 8 -> "Medium"
        else -> "Low"
    }
}

private fun getRecommendedPersona(summary: Summary): String {
    return when (summary.persona) {
        com.example.sumup.domain.model.SummaryPersona.GENERAL -> "Study"
        com.example.sumup.domain.model.SummaryPersona.STUDY -> "Professional"
        com.example.sumup.domain.model.SummaryPersona.PROFESSIONAL -> "Academic"
        com.example.sumup.domain.model.SummaryPersona.ACADEMIC -> "Simple"
        com.example.sumup.domain.model.SummaryPersona.SIMPLE -> "General"
    }
}

// Extension functions for enums
private fun com.example.sumup.domain.model.ReadabilityLevel.toDisplayString(): String = when (this) {
    com.example.sumup.domain.model.ReadabilityLevel.ELEMENTARY -> "Elementary"
    com.example.sumup.domain.model.ReadabilityLevel.MIDDLE_SCHOOL -> "Middle School"
    com.example.sumup.domain.model.ReadabilityLevel.HIGH_SCHOOL -> "High School"
    com.example.sumup.domain.model.ReadabilityLevel.COLLEGE -> "College"
    com.example.sumup.domain.model.ReadabilityLevel.GRADUATE -> "Graduate"
    com.example.sumup.domain.model.ReadabilityLevel.PROFESSIONAL -> "Professional"
}

private fun com.example.sumup.domain.model.DensityLevel.toDisplayString(): String = when (this) {
    com.example.sumup.domain.model.DensityLevel.SPARSE -> "Sparse"
    com.example.sumup.domain.model.DensityLevel.LIGHT -> "Light"
    com.example.sumup.domain.model.DensityLevel.MODERATE -> "Moderate"
    com.example.sumup.domain.model.DensityLevel.DENSE -> "Dense"
    com.example.sumup.domain.model.DensityLevel.VERY_DENSE -> "Very Dense"
}

private fun com.example.sumup.domain.model.DifficultyLevel.toDisplayString(): String = when (this) {
    com.example.sumup.domain.model.DifficultyLevel.TRIVIAL -> "Trivial"
    com.example.sumup.domain.model.DifficultyLevel.EASY -> "Easy"
    com.example.sumup.domain.model.DifficultyLevel.MODERATE -> "Moderate"
    com.example.sumup.domain.model.DifficultyLevel.CHALLENGING -> "Challenging"
    com.example.sumup.domain.model.DifficultyLevel.EXPERT -> "Expert"
}

private fun com.example.sumup.domain.model.SuitabilityLevel.toDisplayString(): String = when (this) {
    com.example.sumup.domain.model.SuitabilityLevel.EXCELLENT -> "Excellent"
    com.example.sumup.domain.model.SuitabilityLevel.GOOD -> "Good"
    com.example.sumup.domain.model.SuitabilityLevel.FAIR -> "Fair"
    com.example.sumup.domain.model.SuitabilityLevel.POOR -> "Poor"
}

// Fallback functions for old summaries
private fun calculateFallbackCoherence(summary: Summary): Int {
    return 70 + min(summary.bulletPoints.size * 3, 25)
}

private fun calculateFallbackRetention(summary: Summary): Int {
    val compressionRatio = summary.metrics.reductionPercentage
    return when {
        compressionRatio < 50 -> 95
        compressionRatio < 70 -> 85
        compressionRatio < 85 -> 75
        else -> 65
    }
}