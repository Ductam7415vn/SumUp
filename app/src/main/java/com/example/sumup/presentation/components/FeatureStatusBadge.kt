package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class FeatureStatus {
    AVAILABLE,
    DEMO,
    COMING_SOON,
    BETA
}

@Composable
fun FeatureStatusBadge(
    status: FeatureStatus,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val (backgroundColor, contentColor, text) = when (status) {
        FeatureStatus.AVAILABLE -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Available"
        )
        FeatureStatus.DEMO -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Demo Mode"
        )
        FeatureStatus.COMING_SOON -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Coming Soon"
        )
        FeatureStatus.BETA -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Beta"
        )
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showIcon && status != FeatureStatus.AVAILABLE) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = contentColor
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = contentColor
            )
        }
    }
}

@Composable
fun FeatureStatusInfo(
    status: FeatureStatus,
    message: String,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
    actionText: String? = null
) {
    AnimatedVisibility(
        visible = status != FeatureStatus.AVAILABLE,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (status) {
                    FeatureStatus.DEMO -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                    FeatureStatus.COMING_SOON -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    FeatureStatus.BETA -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = when (status) {
                        FeatureStatus.DEMO -> MaterialTheme.colorScheme.error
                        FeatureStatus.COMING_SOON -> MaterialTheme.colorScheme.secondary
                        FeatureStatus.BETA -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (onAction != null && actionText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onAction) {
                        Text(
                            text = actionText,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}