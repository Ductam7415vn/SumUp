package com.example.sumup.presentation.screens.result.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryKPICard(
    originalWords: Int,
    summaryWords: Int,
    originalReadTime: Int,
    summaryReadTime: Int,
    modifier: Modifier = Modifier
) {
    val percentReduction = if (originalWords > 0) {
        ((originalWords - summaryWords) / originalWords.toFloat() * 100).toInt()
    } else 0
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Success header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ Summary Complete",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Primary metric - most prominent
            MetricItem(
                icon = "📊",
                value = "$percentReduction% shorter",
                label = "Size reduction",
                prominent = true,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Secondary metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    icon = "⏱️",
                    value = "${originalReadTime}m → ${summaryReadTime}m",
                    label = "Reading time"
                )
                
                MetricItem(
                    icon = "📝",
                    value = "$originalWords → $summaryWords",
                    label = "Word count"
                )
            }
        }
    }
}

@Composable
fun MetricItem(
    icon: String,
    value: String,
    label: String,
    prominent: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = icon,
            fontSize = if (prominent) 32.sp else 20.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = if (prominent) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}