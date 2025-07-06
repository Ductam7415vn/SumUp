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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sumup.ui.theme.Dimensions

@Composable
fun SaveAsPdfGuideDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Microsoft Word", "Google Docs", "LibreOffice", "Mobile Apps")
        
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp),
                shape = RoundedCornerShape(Dimensions.radiusXl),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationMd)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "How to Save as PDF",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    // Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 0.dp
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                    
                    Divider()
                    
                    // Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(Dimensions.spacingLg),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
                    ) {
                        when (selectedTab) {
                            0 -> MicrosoftWordGuide()
                            1 -> GoogleDocsGuide()
                            2 -> LibreOfficeGuide()
                            3 -> MobileAppsGuide()
                        }
                    }
                    
                    // Footer
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.spacingMd),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MicrosoftWordGuide() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)) {
        InfoCard(
            icon = Icons.Default.Computer,
            title = "Microsoft Word (Windows/Mac)",
            color = Color(0xFF2B579A) // Microsoft blue
        )
        
        StepCard("1", "Open your DOC file in Microsoft Word")
        StepCard("2", "Click File → Save As")
        StepCard("3", "Choose location to save")
        StepCard("4", "In 'Save as type' dropdown, select 'PDF'")
        StepCard("5", "Click Save")
        
        TipCard(
            "You can also use File → Export → Create PDF/XPS for more options"
        )
    }
}

@Composable
private fun GoogleDocsGuide() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)) {
        InfoCard(
            icon = Icons.Default.Language,
            title = "Google Docs (Online)",
            color = Color(0xFF4285F4) // Google blue
        )
        
        StepCard("1", "Upload your DOC file to Google Drive")
        StepCard("2", "Open with Google Docs")
        StepCard("3", "Click File → Download")
        StepCard("4", "Select 'PDF Document (.pdf)'")
        
        TipCard(
            "Google Docs is free and works on any device with internet"
        )
    }
}

@Composable
private fun LibreOfficeGuide() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)) {
        InfoCard(
            icon = Icons.Default.Description,
            title = "LibreOffice (Free Alternative)",
            color = Color(0xFF00A500) // LibreOffice green
        )
        
        StepCard("1", "Open your DOC file in LibreOffice Writer")
        StepCard("2", "Click File → Export as PDF")
        StepCard("3", "Adjust settings if needed")
        StepCard("4", "Click Export")
        
        TipCard(
            "LibreOffice is completely free and open-source"
        )
    }
}

@Composable
private fun MobileAppsGuide() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)) {
        InfoCard(
            icon = Icons.Default.PhoneAndroid,
            title = "Mobile Apps",
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            "Android Apps:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        MobileAppCard(
            name = "Microsoft Word",
            steps = listOf(
                "Open DOC file",
                "Tap ⋮ (menu)",
                "Save As → PDF"
            )
        )
        
        MobileAppCard(
            name = "Google Docs",
            steps = listOf(
                "Open DOC file",
                "Tap ⋮ (menu)",
                "Share & export → Save as → PDF"
            )
        )
        
        MobileAppCard(
            name = "WPS Office",
            steps = listOf(
                "Open DOC file",
                "Tools → Export to PDF",
                "Save"
            )
        )
        
        TipCard(
            "Most office apps on Android can export to PDF for free"
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun StepCard(
    number: String,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
        verticalAlignment = Alignment.Top
    ) {
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
        
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MobileAppCard(
    name: String,
    steps: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.spacingMd),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXs)
        ) {
            Text(
                name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            steps.forEach { step ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXs)
                ) {
                    Text("•", color = MaterialTheme.colorScheme.primary)
                    Text(step, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun TipCard(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}