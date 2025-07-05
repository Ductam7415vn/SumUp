package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sumup.domain.model.ApiKeyInfo
import com.example.sumup.domain.model.KeyRotationReminder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ApiKeyManagementCard(
    apiKeys: List<ApiKeyInfo>,
    activeKeyId: String?,
    rotationReminders: List<KeyRotationReminder>,
    onAddKey: (name: String, key: String) -> Unit,
    onDeleteKey: (String) -> Unit,
    onSetActiveKey: (String) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showRotationDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "API Keys",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    // Key count badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = apiKeys.size.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onExport) {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = "Export",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onImport) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Import",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Key",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Rotation warning
            val overdueKeys = rotationReminders.filter { it.isOverdue }
            if (overdueKeys.isNotEmpty()) {
                RotationWarningCard(
                    overdueCount = overdueKeys.size,
                    onClick = { showRotationDialog = true }
                )
            }
            
            // API Keys List
            if (apiKeys.isEmpty()) {
                EmptyKeysPlaceholder(
                    onAddKey = { showAddDialog = true }
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    apiKeys.forEach { keyInfo ->
                        ApiKeyItem(
                            keyInfo = keyInfo,
                            isActive = keyInfo.id == activeKeyId,
                            onSetActive = { onSetActiveKey(keyInfo.id) },
                            onDelete = { onDeleteKey(keyInfo.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Add Key Dialog
    if (showAddDialog) {
        AddApiKeyDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, key ->
                onAddKey(name, key)
                showAddDialog = false
            }
        )
    }
    
    // Rotation Reminders Dialog
    if (showRotationDialog) {
        KeyRotationDialog(
            reminders = rotationReminders,
            onDismiss = { showRotationDialog = false }
        )
    }
}

@Composable
private fun RotationWarningCard(
    overdueCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.RotateRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$overdueCount key${if (overdueCount > 1) "s" else ""} need rotation",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    text = "Tap to view details",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ApiKeyItem(
    keyInfo: ApiKeyInfo,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isActive) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Active indicator
            RadioButton(
                selected = isActive,
                onClick = onSetActive,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Key info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = keyInfo.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = maskApiKey(keyInfo.key),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Created: ${formatDate(keyInfo.createdAt)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    
                    if (keyInfo.lastUsedAt != null) {
                        Text(
                            text = "Last used: ${formatRelativeTime(keyInfo.lastUsedAt)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Usage stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${keyInfo.usageCount} requests",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Delete button
            IconButton(
                onClick = { showDeleteConfirm = true },
                enabled = !isActive // Can't delete active key
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = if (isActive) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete API Key?") },
            text = { 
                Text("Are you sure you want to delete '${keyInfo.name}'? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyKeysPlaceholder(
    onAddKey: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.VpnKey,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Text(
            text = "No API keys added",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Add your Gemini API key to start",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        
        Button(
            onClick = onAddKey,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add API Key")
        }
    }
}

@Composable
private fun AddApiKeyDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, key: String) -> Unit
) {
    var keyName by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add API Key",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = keyName,
                    onValueChange = { keyName = it },
                    label = { Text("Key Name") },
                    placeholder = { Text("e.g., Personal Key, Work Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    placeholder = { Text("Enter your Gemini API key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showKey) 
                        androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showKey) "Hide" else "Show"
                            )
                        }
                    }
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            if (keyName.isNotBlank() && apiKey.isNotBlank()) {
                                onConfirm(keyName, apiKey)
                            }
                        },
                        enabled = keyName.isNotBlank() && apiKey.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyRotationDialog(
    reminders: List<KeyRotationReminder>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Key Rotation Reminders",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(reminders) { reminder ->
                        RotationReminderItem(reminder = reminder)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "It's recommended to rotate API keys every 90 days for security.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RotationReminderItem(
    reminder: KeyRotationReminder
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isOverdue)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (reminder.isOverdue) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = if (reminder.isOverdue)
                    MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.keyName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    text = if (reminder.isOverdue) {
                        "${reminder.daysOverdue} days overdue"
                    } else {
                        "${reminder.daysUntilRotation} days until rotation"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (reminder.isOverdue) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = "OVERDUE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun maskApiKey(key: String): String {
    return if (key.length > 8) {
        "${key.take(4)}${"•".repeat(key.length - 8)}${key.takeLast(4)}"
    } else {
        "•".repeat(key.length)
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatRelativeTime(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time
    
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> formatDate(date)
    }
}