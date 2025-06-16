package com.example.sumup.presentation.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class PermissionType {
    CAMERA,
    STORAGE,
    NOTIFICATION,
    LOCATION
}

@Composable
fun PermissionDeniedDialog(
    permissionType: PermissionType,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val permissionInfo = getPermissionInfo(permissionType)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = permissionInfo.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = permissionInfo.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = permissionInfo.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "How to enable:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        permissionInfo.steps.forEach { step ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onOpenSettings()
                    // Open app settings
                    context.startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        }
    )
}

@Composable
fun NetworkErrorBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon with background
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Text(
                    text = "Check your WiFi or mobile data connection and try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                // Network troubleshooting tips
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NetworkTip(
                            icon = Icons.Default.Wifi,
                            text = "Check if WiFi is turned on"
                        )
                        NetworkTip(
                            icon = Icons.Default.SignalCellularAlt,
                            text = "Verify mobile data is enabled"
                        )
                        NetworkTip(
                            icon = Icons.Default.AirplanemodeActive,
                            text = "Ensure airplane mode is off"
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkTip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class PermissionInfo(
    val title: String,
    val message: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val steps: List<String>
)

private fun getPermissionInfo(permissionType: PermissionType): PermissionInfo {
    return when (permissionType) {
        PermissionType.CAMERA -> PermissionInfo(
            title = "Camera Permission Needed",
            message = "To scan text from documents, SumUp needs access to your camera.",
            icon = Icons.Default.CameraAlt,
            steps = listOf(
                "Open Settings below",
                "Tap on 'Permissions'",
                "Find 'Camera' and turn it on",
                "Return to SumUp"
            )
        )
        PermissionType.STORAGE -> PermissionInfo(
            title = "Storage Permission Needed",
            message = "To save and access your PDF files, SumUp needs storage permission.",
            icon = Icons.Default.Folder,
            steps = listOf(
                "Open Settings below",
                "Tap on 'Permissions'",
                "Find 'Storage' and turn it on",
                "Return to SumUp"
            )
        )
        PermissionType.NOTIFICATION -> PermissionInfo(
            title = "Enable Notifications",
            message = "Get notified when your summaries are ready and about important updates.",
            icon = Icons.Default.Notifications,
            steps = listOf(
                "Open Settings below",
                "Tap on 'Notifications'",
                "Turn on 'Allow Notifications'",
                "Customize your preferences"
            )
        )
        PermissionType.LOCATION -> PermissionInfo(
            title = "Location Permission",
            message = "We use location to provide region-specific AI models for better results.",
            icon = Icons.Default.LocationOn,
            steps = listOf(
                "Open Settings below",
                "Tap on 'Permissions'",
                "Find 'Location' and select 'While using app'",
                "Return to SumUp"
            )
        )
    }
}