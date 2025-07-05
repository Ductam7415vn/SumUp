package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.domain.model.ServiceInfo
import com.example.sumup.domain.model.ServiceType

@Composable
fun ApiStatusBanner(
    serviceInfo: ServiceInfo?,
    onConfigureApiKey: () -> Unit,
    modifier: Modifier = Modifier
) {
    serviceInfo?.let { info ->
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = modifier
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (info.type) {
                        ServiceType.MOCK_API -> MaterialTheme.colorScheme.errorContainer
                        ServiceType.REAL_API -> MaterialTheme.colorScheme.primaryContainer
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
                        imageVector = when (info.type) {
                            ServiceType.MOCK_API -> Icons.Default.Warning
                            ServiceType.REAL_API -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = when (info.type) {
                            ServiceType.MOCK_API -> MaterialTheme.colorScheme.error
                            ServiceType.REAL_API -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (info.type) {
                                ServiceType.MOCK_API -> "Demo Mode Active"
                                ServiceType.REAL_API -> "Connected to AI"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = when (info.type) {
                                ServiceType.MOCK_API -> MaterialTheme.colorScheme.onErrorContainer
                                ServiceType.REAL_API -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                        Text(
                            text = info.message,
                            fontSize = 12.sp,
                            color = when (info.type) {
                                ServiceType.MOCK_API -> MaterialTheme.colorScheme.onErrorContainer
                                ServiceType.REAL_API -> MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    
                    if (info.type == ServiceType.MOCK_API) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = onConfigureApiKey,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "Add Key",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}