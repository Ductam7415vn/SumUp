package com.example.sumup.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

/**
 * Beautiful skeleton screen for main input area
 */
@Composable
fun MainScreenSkeleton(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmer()
                        .background(brush = Brush.linearGradient(shimmerColors))
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                }
            }
        }

        // Input type selector skeleton
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer()
                    .background(brush = Brush.linearGradient(shimmerColors))
            )
        }

        // Stats cards skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                }
            }
        }

        // Text input area skeleton
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Content area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (index == 4) 0.6f else 1f)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmer()
                                    .background(brush = Brush.linearGradient(shimmerColors))
                            )
                        }
                    }
                }
                // Footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shimmer()
                        .background(
                            brush = Brush.linearGradient(
                                shimmerColors,
                                start = Offset(0f, 0f),
                                end = Offset(1000f, 0f)
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action button skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmer()
                .background(brush = Brush.linearGradient(shimmerColors))
        )
    }
}

/**
 * Skeleton for result screen while loading
 */
@Composable
fun ResultScreenSkeleton(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // KPI cards skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                }
            }
        }

        // Persona selector skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmer()
                .background(brush = Brush.linearGradient(shimmerColors))
        )

        // Summary content skeleton
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (index == 5) 0.7f else 1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                }
            }
        }

        // Bullet points skeleton
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .shimmer()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer()
                            .background(brush = Brush.linearGradient(shimmerColors))
                    )
                }
            }
        }
    }
}

/**
 * Inline skeleton for loading states within content
 */
@Composable
fun InlineLoadingSkeleton(
    modifier: Modifier = Modifier,
    text: String = "Loading"
) {
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Animated dots
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { index ->
                val alpha = infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(index * 100)
                    ),
                    label = "dot_alpha"
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = alpha.value)
                        )
                )
            }
        }
    }
}