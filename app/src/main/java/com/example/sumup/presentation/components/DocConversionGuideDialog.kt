package com.example.sumup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sumup.ui.theme.Dimensions

@Composable
fun DocConversionGuideDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUseTextInput: () -> Unit
) {
    if (isVisible) {
        val uriHandler = LocalUriHandler.current
        
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp),
                shape = RoundedCornerShape(Dimensions.radiusXl),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationMd)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.spacingLg),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "DOC File Detected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    Divider()
                    
                    // Content
                    Column(
                        modifier = Modifier.padding(Dimensions.spacingLg),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
                    ) {
                        // Info card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimensions.spacingMd),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        "DOC format not supported",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "The older .doc format requires special processing. Please use one of these alternatives:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                        
                        // Options
                        Text(
                            "How to proceed:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // Option 1: Convert in Word
                        OptionCard(
                            number = "1",
                            title = "Convert in Microsoft Word",
                            description = "Open your file in Word → File → Save As → Choose DOCX format",
                            icon = Icons.Default.Computer
                        )
                        
                        // Option 2: Online converter
                        OptionCard(
                            number = "2",
                            title = "Use Online Converter",
                            description = "Free online tools can convert DOC to DOCX instantly",
                            icon = Icons.Default.Language,
                            action = {
                                OutlinedButton(
                                    onClick = { 
                                        uriHandler.openUri("https://cloudconvert.com/doc-to-docx")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Open CloudConvert")
                                }
                            }
                        )
                        
                        // Option 3: Copy paste
                        OptionCard(
                            number = "3",
                            title = "Copy & Paste Text",
                            description = "Open your document and copy the text directly",
                            icon = Icons.Default.ContentCopy,
                            action = {
                                OutlinedButton(
                                    onClick = onUseTextInput,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Use Text Input")
                                }
                            }
                        )
                        
                        // Option 4: Save as PDF
                        OptionCard(
                            number = "4",
                            title = "Save as PDF",
                            description = "Export your document as PDF format",
                            icon = Icons.Default.PictureAsPdf
                        )
                    }
                    
                    // Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.spacingLg),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Got it")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    number: String,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.spacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
                verticalAlignment = Alignment.Top
            ) {
                // Number badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            number,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            action?.invoke()
        }
    }
}