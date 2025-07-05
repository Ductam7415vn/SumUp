package com.example.sumup.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sumup.ui.theme.Dimensions
import com.example.sumup.ui.theme.Spacing
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import kotlinx.coroutines.delay

data class PdfPageInfo(
    val pageNumber: Int,
    val thumbnailUrl: String? = null,
    val isSelected: Boolean = false
)

enum class PdfProcessOption {
    ALL_PAGES,
    FIRST_10,
    CUSTOM_PAGES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedPdfPreviewDialog(
    pdfUri: String,
    pdfName: String,
    pageCount: Int,
    estimatedProcessingTime: Int = 30, // seconds
    onConfirm: (selectedPages: List<Int>?) -> Unit,
    onDismiss: () -> Unit
) {
    val hapticManager = rememberHapticFeedback()
    var selectedOption by remember { mutableStateOf(PdfProcessOption.ALL_PAGES) }
    var selectedPages by remember { mutableStateOf(setOf<Int>()) }
    var showPageSelection by remember { mutableStateOf(false) }
    
    // Generate page info
    val pages = remember {
        (1..pageCount).map { pageNumber ->
            PdfPageInfo(
                pageNumber = pageNumber,
                thumbnailUrl = null, // In real app, generate thumbnails
                isSelected = false
            )
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(Dimensions.radiusXxl),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.elevationLg
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                PdfPreviewHeader(
                    pdfName = pdfName,
                    pageCount = pageCount,
                    onClose = onDismiss
                )
                
                Divider()
                
                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
                ) {
                    // PDF Info Card
                    PdfInfoCard(
                        pageCount = pageCount,
                        estimatedTime = estimatedProcessingTime,
                        fileSize = "2.5 MB" // TODO: Get actual file size
                    )
                    
                    // Processing Options
                    Text(
                        text = "Processing Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    PdfProcessingOptions(
                        selectedOption = selectedOption,
                        onOptionSelected = { option ->
                            hapticManager.performHapticFeedback(HapticFeedbackType.SELECTION_START)
                            selectedOption = option
                            showPageSelection = option == PdfProcessOption.CUSTOM_PAGES
                        },
                        pageCount = pageCount
                    )
                    
                    // Page Selection Grid
                    AnimatedVisibility(
                        visible = showPageSelection,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Pages (${selectedPages.size} selected)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXs)
                                ) {
                                    // Quick selection buttons
                                    if (pageCount > 10) {
                                        TextButton(
                                            onClick = {
                                                selectedPages = (1..10).toSet()
                                                hapticManager.performHapticFeedback(HapticFeedbackType.SELECTION_START)
                                            }
                                        ) {
                                            Text("First 10", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                    
                                    TextButton(
                                        onClick = {
                                            selectedPages = if (selectedPages.size == pageCount) {
                                                emptySet()
                                            } else {
                                                (1..pageCount).toSet()
                                            }
                                            hapticManager.performHapticFeedback(HapticFeedbackType.SELECTION_START)
                                        }
                                    ) {
                                        Text(
                                            if (selectedPages.size == pageCount) "Deselect All" else "Select All",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                            
                            PageSelectionGrid(
                                pages = pages,
                                selectedPages = selectedPages,
                                onPageToggle = { pageNumber ->
                                    hapticManager.performHapticFeedback(HapticFeedbackType.TICK)
                                    selectedPages = if (pageNumber in selectedPages) {
                                        selectedPages - pageNumber
                                    } else {
                                        selectedPages + pageNumber
                                    }
                                }
                            )
                        }
                    }
                    
                    // Tips - Only show when not selecting pages to save space
                    if (!showPageSelection) {
                        InfoCard(
                            icon = Icons.Default.Lightbulb,
                            title = "Pro Tip",
                            message = "For best results, select pages with primarily text content. Images and charts may affect summary quality."
                        )
                    }
                }
                
                Divider()
                
                // Footer Actions
                PdfPreviewFooter(
                    selectedOption = selectedOption,
                    selectedPageCount = when (selectedOption) {
                        PdfProcessOption.ALL_PAGES -> pageCount
                        PdfProcessOption.FIRST_10 -> minOf(10, pageCount)
                        PdfProcessOption.CUSTOM_PAGES -> selectedPages.size
                    },
                    canProceed = when (selectedOption) {
                        PdfProcessOption.CUSTOM_PAGES -> selectedPages.isNotEmpty()
                        else -> true
                    },
                    onCancel = onDismiss,
                    onConfirm = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.SUCCESS)
                        when (selectedOption) {
                            PdfProcessOption.ALL_PAGES -> onConfirm(null)
                            PdfProcessOption.FIRST_10 -> onConfirm((1..minOf(10, pageCount)).toList())
                            PdfProcessOption.CUSTOM_PAGES -> onConfirm(selectedPages.sorted().toList())
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PdfPreviewHeader(
    pdfName: String,
    pageCount: Int,
    onClose: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.screenPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PDF Preview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pdfName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close preview",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PdfInfoCard(
    pageCount: Int,
    estimatedTime: Int,
    fileSize: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMd),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(
                icon = Icons.Default.Description,
                label = "Pages",
                value = pageCount.toString()
            )
            
            InfoItem(
                icon = Icons.Default.Timer,
                label = "Est. Time",
                value = "${estimatedTime}s"
            )
            
            InfoItem(
                icon = Icons.Default.Storage,
                label = "Size",
                value = fileSize
            )
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXs)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimensions.iconSizeMd)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PdfProcessingOptions(
    selectedOption: PdfProcessOption,
    onOptionSelected: (PdfProcessOption) -> Unit,
    pageCount: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
    ) {
        ProcessOptionCard(
            option = PdfProcessOption.ALL_PAGES,
            title = "Process All Pages",
            description = "Summarize the entire document ($pageCount pages)",
            icon = Icons.Default.SelectAll,
            isSelected = selectedOption == PdfProcessOption.ALL_PAGES,
            onClick = { onOptionSelected(PdfProcessOption.ALL_PAGES) }
        )
        
        if (pageCount > 10) {
            ProcessOptionCard(
                option = PdfProcessOption.FIRST_10,
                title = "First 10 Pages",
                description = "Quick summary of the beginning",
                icon = Icons.Default.FirstPage,
                isSelected = selectedOption == PdfProcessOption.FIRST_10,
                onClick = { onOptionSelected(PdfProcessOption.FIRST_10) }
            )
        }
        
        ProcessOptionCard(
            option = PdfProcessOption.CUSTOM_PAGES,
            title = "Select Pages",
            description = "Choose specific pages to summarize",
            icon = Icons.Default.FilterAlt,
            isSelected = selectedOption == PdfProcessOption.CUSTOM_PAGES,
            onClick = { onOptionSelected(PdfProcessOption.CUSTOM_PAGES) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProcessOptionCard(
    option: PdfProcessOption,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "option_scale"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(Dimensions.radiusMd),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(Dimensions.iconSizeMd)
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimensions.iconSizeMd)
                )
            }
        }
    }
}

@Composable
private fun PageSelectionGrid(
    pages: List<PdfPageInfo>,
    selectedPages: Set<Int>,
    onPageToggle: (Int) -> Unit
) {
    // Use LazyVerticalGrid for better performance and proper scrolling
    val gridColumns = 4
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp), // Maximum height with scrolling
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
        contentPadding = PaddingValues(vertical = Dimensions.paddingXs)
    ) {
        items(
            count = pages.size,
            key = { index -> pages[index].pageNumber }
        ) { index ->
            val page = pages[index]
            PageThumbnail(
                page = page,
                isSelected = page.pageNumber in selectedPages,
                onClick = { onPageToggle(page.pageNumber) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageThumbnail(
    page: PdfPageInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.9f else 1f,
        label = "thumbnail_scale"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(0.7f) // Original aspect ratio
            .scale(scale),
        shape = RoundedCornerShape(Dimensions.radiusSm),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for PDF thumbnail
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(Dimensions.iconSizeMd)
                )
                Text(
                    text = page.pageNumber.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
            
            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimensions.paddingXs)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimensions.iconSizeSm)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(Dimensions.iconSizeMd)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PdfPreviewFooter(
    selectedOption: PdfProcessOption,
    selectedPageCount: Int,
    canProceed: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Dimensions.elevationMd,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.screenPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ready to process",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "$selectedPageCount pages selected",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = onConfirm,
                    enabled = canProceed
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSizeSm)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingXs))
                    Text("Start Processing")
                }
            }
        }
    }
}