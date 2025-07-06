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
import com.example.sumup.domain.model.Document
import com.example.sumup.domain.model.DocumentType
import com.example.sumup.ui.theme.Dimensions
import com.example.sumup.ui.theme.Spacing
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import kotlinx.coroutines.delay
import android.content.Context
import android.provider.OpenableColumns
import com.example.sumup.presentation.screens.main.components.getSupportedMimeTypes
// Feature status components commented out until they're implemented
// import com.example.sumup.presentation.components.FeatureStatusBadge
// import com.example.sumup.presentation.components.FeatureStatus
// import com.example.sumup.presentation.components.FeatureStatusInfo

@Composable
fun ImprovedDocumentUploadSection(
    selectedDocumentUri: String?,
    selectedDocumentName: String?,
    selectedDocument: Document? = null,
    serviceInfo: com.example.sumup.domain.model.ServiceInfo? = null,
    onDocumentSelected: (uri: String) -> Unit,
    onClear: () -> Unit,
    onShowPreview: () -> Unit,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    
    // Animation states
    var isDragOver by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isDragOver) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )
    
    // Document launcher with multiple MIME types
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            haptic.performHapticFeedback(HapticFeedbackType.SUCCESS)
            onDocumentSelected(it.toString())
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (selectedDocumentUri != null) 160.dp else 280.dp),
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
            defaultElevation = if (selectedDocumentUri != null) Dimensions.elevationXs else Dimensions.elevationSm
        )
    ) {
        AnimatedContent(
            targetState = selectedDocumentUri != null,
            transitionSpec = {
                if (targetState) {
                    slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
                } else {
                    slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
                }
            }
        ) { hasDocument ->
            if (hasDocument) {
                SelectedDocumentContent(
                    documentName = selectedDocumentName ?: "document",
                    document = selectedDocument,
                    onReplace = { 
                        launcher.launch(getSupportedMimeTypes()) 
                    },
                    onShowPreview = onShowPreview,
                    onClear = onClear
                )
            } else {
                DocumentPickerContent(
                    serviceInfo = serviceInfo,
                    onPick = { 
                        launcher.launch(getSupportedMimeTypes())
                    },
                    onNavigateToSettings = onNavigateToSettings,
                    modifier = Modifier.scale(scale)
                )
            }
        }
    }
}

@Composable
private fun DocumentPickerContent(
    serviceInfo: com.example.sumup.domain.model.ServiceInfo?,
    onPick: () -> Unit,
    onNavigateToSettings: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
    ) {
        // Animated Icon
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = "Upload Document",
                modifier = Modifier
                    .size(40.dp)
                    .rotate(rotation * 0.1f)
                    .graphicsLayer {
                        shadowElevation = 8.dp.toPx()
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = "Upload Document",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Support for PDF, TXT and DOCX files",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "DOC format requires conversion to DOCX",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        // Feature status badge commented out until implemented
        // if (serviceInfo?.serviceType == com.example.sumup.domain.model.ServiceType.MOCK) {
        //     val statusInfo = FeatureStatusInfo(
        //         status = FeatureStatus.MOCK,
        //         message = "Configure API key to process real documents",
        //         actionLabel = "Settings",
        //         onAction = onNavigateToSettings
        //     )
        //     
        //     Spacer(modifier = Modifier.height(Dimensions.spacingSm))
        //     
        //     FeatureStatusBadge(
        //         statusInfo = statusInfo,
        //         modifier = Modifier.fillMaxWidth()
        //     )
        // }
        
        Spacer(modifier = Modifier.height(Dimensions.spacingMd))
        
        Button(
            onClick = onPick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.radiusMd)
        ) {
            Icon(Icons.Default.UploadFile, contentDescription = null)
            Spacer(modifier = Modifier.width(Dimensions.spacingSm))
            Text("Select Document")
        }
        
        Text(
            text = "Max 10MB",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SelectedDocumentContent(
    documentName: String,
    document: Document?,
    onReplace: () -> Unit,
    onShowPreview: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (document?.type) {
                        DocumentType.PDF -> Icons.Default.PictureAsPdf
                        DocumentType.DOC, DocumentType.DOCX -> Icons.Default.Description
                        DocumentType.TXT -> Icons.Default.TextFields
                        DocumentType.RTF -> Icons.Default.Article
                        else -> Icons.Default.InsertDriveFile
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = documentName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    val info = buildString {
                        append(document?.type?.name ?: "Document")
                        document?.pageCount?.let { append(" • $it pages") }
                        document?.sizeBytes?.let { 
                            val sizeMB = it / (1024.0 * 1024.0)
                            append(" • %.1f MB".format(sizeMB))
                        }
                    }
                    
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            DocumentActions(
                documentType = document?.type,
                onShowPreview = onShowPreview,
                onReplace = onReplace,
                onClear = onClear
            )
        }
    }
}

@Composable
private fun DocumentActions(
    documentType: DocumentType?,
    onShowPreview: () -> Unit,
    onReplace: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
    ) {
        // Show preview button for PDF and DOCX
        if (documentType == DocumentType.PDF || documentType == DocumentType.DOCX) {
            IconButton(
                onClick = onShowPreview,
                modifier = Modifier.size(Dimensions.minTouchTarget)
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = when (documentType) {
                        DocumentType.PDF -> "Preview PDF"
                        DocumentType.DOCX -> "Preview DOCX"
                        else -> "Preview Document"
                    },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        IconButton(
            onClick = onReplace,
            modifier = Modifier.size(Dimensions.minTouchTarget)
        ) {
            Icon(
                Icons.Default.SwapHoriz,
                contentDescription = "Replace Document",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        IconButton(
            onClick = onClear,
            modifier = Modifier.size(Dimensions.minTouchTarget)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove Document",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}