package com.example.sumup.presentation.screens.main.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sumup.ui.theme.Dimensions
import com.example.sumup.ui.theme.Spacing
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import kotlinx.coroutines.delay
import android.content.Context
import android.provider.OpenableColumns
import com.example.sumup.presentation.components.FeatureStatusBadge
import com.example.sumup.presentation.components.FeatureStatus
import com.example.sumup.presentation.components.FeatureStatusInfo

@Composable
fun ImprovedPdfUploadSection(
    selectedPdfUri: String?,
    selectedPdfName: String?,
    serviceInfo: com.example.sumup.domain.model.ServiceInfo? = null,
    onPdfSelected: (uri: String, name: String, pageCount: Int) -> Unit,
    onClear: () -> Unit,
    onShowPreview: () -> Unit,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val hapticManager = rememberHapticFeedback()
    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(false) }
    var isDragOver by remember { mutableStateOf(false) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isProcessing = true
            val fileInfo = getPdfFileInfo(context, it)
            hapticManager.performHapticFeedback(HapticFeedbackType.SUCCESS)
            onPdfSelected(it.toString(), fileInfo.name, fileInfo.pageCount)
            isProcessing = false
        }
    }
    
    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (selectedPdfUri != null) 160.dp else 280.dp),
        shape = RoundedCornerShape(Dimensions.radiusXl),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragOver) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isDragOver) 3.dp else 2.dp,
            color = if (isDragOver) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selectedPdfUri != null) Dimensions.elevationXs else Dimensions.elevationSm
        )
    ) {
        AnimatedContent(
            targetState = selectedPdfUri != null,
            transitionSpec = {
                if (targetState) {
                    slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
                } else {
                    slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
                }
            },
            label = "pdf_content"
        ) { hasPdf ->
            if (hasPdf) {
                SelectedPdfContent(
                    pdfName = selectedPdfName ?: "Document.pdf",
                    onShowPreview = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                        onShowPreview()
                    },
                    onClear = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                        onClear()
                    },
                    onReplace = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                        launcher.launch("application/pdf")
                    }
                )
            } else {
                UploadPromptContent(
                    isProcessing = isProcessing,
                    showDemoBadge = serviceInfo?.type == com.example.sumup.domain.model.ServiceType.MOCK_API,
                    onChooseFile = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                        launcher.launch("application/pdf")
                    }
                )
            }
        }
    }
        
//        // Demo mode info - only show if using mock API
//        if (serviceInfo?.type == com.example.sumup.domain.model.ServiceType.MOCK_API) {
//            FeatureStatusInfo(
//                status = FeatureStatus.DEMO,
//                message = "PDF processing uses demo data. Add API key for real processing.",
//                onAction = onNavigateToSettings,
//                actionText = if (onNavigateToSettings != null) "Configure" else null
//            )
//        }
    }
}

@Composable
private fun SelectedPdfContent(
    pdfName: String,
    onShowPreview: () -> Unit,
    onClear: () -> Unit,
    onReplace: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // PDF Icon with animation
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pdf_pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pdf_scale"
            )
            
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimensions.iconSizeMd)
                    )
                }
            }
        }
        
        // PDF Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingXs)
        ) {
            Text(
                text = pdfName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Ready to process",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
        ) {
            IconButton(
                onClick = onShowPreview,
                modifier = Modifier.size(Dimensions.minTouchTarget)
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "Preview PDF",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(
                onClick = onReplace,
                modifier = Modifier.size(Dimensions.minTouchTarget)
            ) {
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = "Replace PDF",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(Dimensions.minTouchTarget)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove PDF",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun UploadPromptContent(
    isProcessing: Boolean,
    showDemoBadge: Boolean = true,
    onChooseFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = !isProcessing) { onChooseFile() }
            .padding(Dimensions.spacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated upload icon
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                val infiniteTransition = rememberInfiniteTransition(label = "upload")
                val translateY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "upload_translate"
                )
                
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer { translationY = translateY },
                    shape = RoundedCornerShape(Dimensions.radiusLg),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.spacingLg))
        
        Text(
            text = if (isProcessing) "Processing..." else "Drop your PDF here",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingSm))
        
        // Demo mode badge - only show if using mock API
        if (showDemoBadge) {
            FeatureStatusBadge(
                status = FeatureStatus.DEMO,
                showIcon = true
            )
            
            Spacer(modifier = Modifier.height(Dimensions.spacingSm))
        }
        
        Text(
            text = "or click to browse",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingXs))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoChip(
                icon = Icons.Default.Description,
                text = "Max 10MB"
            )
            InfoChip(
                icon = Icons.Default.Security,
                text = "Secure"
            )
        }
        
        Spacer(modifier = Modifier.height(Dimensions.spacingLg))
        
        Button(
            onClick = onChooseFile,
            enabled = !isProcessing,
            modifier = Modifier.height(Dimensions.buttonHeightMd)
        ) {
            Icon(
                Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeSm)
            )
            Spacer(modifier = Modifier.width(Dimensions.spacingSm))
            Text("Choose PDF File")
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(Dimensions.radiusFull),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimensions.paddingMd,
                vertical = Dimensions.paddingXs
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingXs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class PdfFileInfo(
    val name: String,
    val sizeBytes: Long,
    val pageCount: Int
)

private fun getPdfFileInfo(context: Context, uri: Uri): PdfFileInfo {
    var fileName = "Document.pdf"
    var fileSize = 0L
    
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
        fileSize = cursor.getLong(sizeIndex)
    }
    
    // TODO: Get actual page count using PDFBox
    val estimatedPageCount = (fileSize / 50000).toInt().coerceAtLeast(1)
    
    return PdfFileInfo(
        name = fileName,
        sizeBytes = fileSize,
        pageCount = estimatedPageCount
    )
}