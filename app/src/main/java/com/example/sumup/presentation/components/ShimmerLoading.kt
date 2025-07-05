package com.example.sumup.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

/**
 * Shimmer loading components for various UI elements
 */

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
) {
    Box(
        modifier = modifier
            .shimmer()
            .background(
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun ShimmerCircle(
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .shimmer()
            .background(
                color = color,
                shape = CircleShape
            )
    )
}

@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    height: Int = 16,
    color: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
) {
    ShimmerBox(
        modifier = modifier.height(height.dp),
        color = color
    )
}

// Specific shimmer components for different screens

@Composable
fun ShimmerHistoryItem(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon shimmer
            ShimmerCircle(size = 40)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title shimmer
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    height = 18
                )
                
                // Subtitle shimmer
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    height = 14
                )
                
                // Date shimmer
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.3f),
                    height = 12
                )
            }
            
            // Action button shimmer
            ShimmerCircle(size = 24)
        }
    }
}

@Composable
fun ShimmerHistoryList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) {
            ShimmerHistoryItem()
        }
    }
}

@Composable
fun ShimmerResultCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    height = 20
                )
                ShimmerCircle(size = 32)
            }
            
            // Content lines
            repeat(4) { index ->
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(
                        when (index) {
                            0 -> 1f
                            1 -> 0.9f
                            2 -> 0.95f
                            else -> 0.7f
                        }
                    ),
                    height = 16
                )
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
            }
        }
    }
}

@Composable
fun ShimmerKPICard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerCircle(size = 24)
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    height = 16
                )
            }
            
            // Value
            ShimmerText(
                modifier = Modifier.fillMaxWidth(0.4f),
                height = 32
            )
            
            // Description
            ShimmerText(
                modifier = Modifier.fillMaxWidth(0.8f),
                height = 12
            )
        }
    }
}

@Composable
fun ShimmerKPIRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerKPICard(modifier = Modifier.weight(1f))
        ShimmerKPICard(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ShimmerProcessingCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Processing icon
            ShimmerCircle(size = 80)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            ShimmerText(
                modifier = Modifier.fillMaxWidth(0.6f),
                height = 24
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            ShimmerText(
                modifier = Modifier.fillMaxWidth(0.8f),
                height = 16
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ShimmerText(
                modifier = Modifier.fillMaxWidth(0.7f),
                height = 16
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress bar
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cancel button
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(44.dp)
            )
        }
    }
}

@Composable
fun ShimmerSettingsItem(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            ShimmerCircle(size = 24)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Title
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    height = 16
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Subtitle
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    height = 14
                )
            }
            
            // Arrow or toggle
            ShimmerCircle(size = 20)
        }
    }
}

@Composable
fun ShimmerSettingsSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section title
            ShimmerText(
                modifier = Modifier.fillMaxWidth(0.4f),
                height = 18
            )
            
            // Section items
            repeat(itemCount) {
                ShimmerSettingsItem()
            }
        }
    }
}