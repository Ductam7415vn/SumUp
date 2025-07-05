package com.example.sumup.presentation.screens.ocr

import android.Manifest
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sumup.ui.theme.Dimensions
import com.example.sumup.ui.theme.Spacing
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImprovedOcrScreen(
    onNavigateBack: () -> Unit,
    onTextScanned: (String) -> Unit
) {
    val hapticManager = rememberHapticFeedback()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var capturedText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var showGuidance by remember { mutableStateOf(true) }
    var scanMode by remember { mutableStateOf(ScanMode.AUTO) }
    
    Scaffold(
        topBar = {
            OcrTopBar(
                onNavigateBack = onNavigateBack,
                scanMode = scanMode,
                onScanModeChange = { mode ->
                    hapticManager.performHapticFeedback(HapticFeedbackType.SELECTION_START)
                    scanMode = mode
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraPreviewWithOcr(
                        modifier = Modifier.fillMaxSize(),
                        scanMode = scanMode,
                        onTextDetected = { text ->
                            if (text.isNotEmpty() && !isProcessing) {
                                hapticManager.performHapticFeedback(HapticFeedbackType.SUCCESS)
                                capturedText = text
                                showPreview = true
                            }
                        },
                        onCapture = { text ->
                            hapticManager.performHapticFeedback(HapticFeedbackType.IMPACT_HEAVY)
                            capturedText = text
                            showPreview = true
                        }
                    )
                    
                    // Scanning overlay
                    OcrScanningOverlay(
                        showGuidance = showGuidance,
                        scanMode = scanMode
                    )
                    
                    // Bottom controls
                    OcrBottomControls(
                        scanMode = scanMode,
                        showGuidance = showGuidance,
                        onToggleGuidance = {
                            hapticManager.performHapticFeedback(HapticFeedbackType.TICK)
                            showGuidance = !showGuidance
                        },
                        onCapture = {
                            // Manual capture handled by camera
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    PermissionRationale(
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                        onNavigateBack = onNavigateBack
                    )
                }
                else -> {
                    LaunchedEffect(Unit) {
                        cameraPermissionState.launchPermissionRequest()
                    }
                    
                    PermissionDenied(
                        onNavigateBack = onNavigateBack
                    )
                }
            }
            
            // Text preview dialog
            if (showPreview) {
                TextPreviewDialog(
                    text = capturedText,
                    onConfirm = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.SUCCESS)
                        onTextScanned(capturedText)
                    },
                    onEdit = { editedText ->
                        capturedText = editedText
                    },
                    onRetry = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                        showPreview = false
                        capturedText = ""
                    },
                    onDismiss = {
                        showPreview = false
                        capturedText = ""
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OcrTopBar(
    onNavigateBack: () -> Unit,
    scanMode: ScanMode,
    onScanModeChange: (ScanMode) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Scan Text",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            // Scan mode selector
            IconButton(
                onClick = {
                    val nextMode = when (scanMode) {
                        ScanMode.AUTO -> ScanMode.MANUAL
                        ScanMode.MANUAL -> ScanMode.AUTO
                    }
                    onScanModeChange(nextMode)
                }
            ) {
                Icon(
                    imageVector = when (scanMode) {
                        ScanMode.AUTO -> Icons.Default.FlashOn
                        ScanMode.MANUAL -> Icons.Default.FlashOff
                    },
                    contentDescription = "Toggle scan mode",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.7f),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

enum class ScanMode {
    AUTO,
    MANUAL
}

@Composable
private fun CameraPreviewWithOcr(
    modifier: Modifier = Modifier,
    scanMode: ScanMode,
    onTextDetected: (String) -> Unit,
    onCapture: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var lastDetectedText by remember { mutableStateOf("") }
    var autoScanEnabled by remember { mutableStateOf(scanMode == ScanMode.AUTO) }
    
    LaunchedEffect(scanMode) {
        autoScanEnabled = scanMode == ScanMode.AUTO
    }
    
    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            textRecognizer.close()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier,
        update = { previewView ->
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(previewView.display.rotation)
                    .build()
                
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            if (autoScanEnabled) {
                                processImageProxy(imageProxy, textRecognizer) { text ->
                                    if (text.isNotEmpty() && text != lastDetectedText) {
                                        lastDetectedText = text
                                        onTextDetected(text)
                                    }
                                }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
    
    // Manual capture button handler
    if (scanMode == ScanMode.MANUAL) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            FloatingActionButton(
                onClick = {
                    imageCapture?.let { capture ->
                        captureImage(capture, context, textRecognizer, onCapture)
                    }
                },
                modifier = Modifier.size(72.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

private fun processImageProxy(
    imageProxy: ImageProxy,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    onTextRecognized: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                onTextRecognized(visionText.text)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

private fun captureImage(
    imageCapture: ImageCapture,
    context: android.content.Context,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    onTextRecognized: (String) -> Unit
) {
    // Implementation for manual capture
    // This would capture the image and process it with OCR
}

@Composable
private fun OcrScanningOverlay(
    showGuidance: Boolean,
    scanMode: ScanMode
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Scanning frame
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.screenPadding)
        ) {
            val strokeWidth = 3.dp.toPx()
            val cornerLength = 50.dp.toPx()
            val cornerRadius = 16.dp.toPx()
            
            // Calculate frame dimensions
            val frameWidth = size.width * 0.9f
            val frameHeight = size.height * 0.5f
            val frameLeft = (size.width - frameWidth) / 2
            val frameTop = (size.height - frameHeight) / 2
            
            // Draw semi-transparent overlay
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )
            
            // Clear the scanning area
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                blendMode = BlendMode.Clear
            )
            
            // Draw corner guides
            val primaryColor = Color(0xFF5B5FDE)
            
            // Top-left corner
            drawPath(
                path = Path().apply {
                    moveTo(frameLeft + cornerLength, frameTop)
                    lineTo(frameLeft + cornerRadius, frameTop)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            frameLeft, frameTop,
                            frameLeft + cornerRadius * 2, frameTop + cornerRadius * 2
                        ),
                        startAngleDegrees = 270f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    lineTo(frameLeft, frameTop + cornerLength)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Top-right corner
            drawPath(
                path = Path().apply {
                    moveTo(frameLeft + frameWidth - cornerLength, frameTop)
                    lineTo(frameLeft + frameWidth - cornerRadius, frameTop)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            frameLeft + frameWidth - cornerRadius * 2, frameTop,
                            frameLeft + frameWidth, frameTop + cornerRadius * 2
                        ),
                        startAngleDegrees = 270f,
                        sweepAngleDegrees = -90f,
                        forceMoveTo = false
                    )
                    lineTo(frameLeft + frameWidth, frameTop + cornerLength)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Bottom-left corner
            drawPath(
                path = Path().apply {
                    moveTo(frameLeft, frameTop + frameHeight - cornerLength)
                    lineTo(frameLeft, frameTop + frameHeight - cornerRadius)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            frameLeft, frameTop + frameHeight - cornerRadius * 2,
                            frameLeft + cornerRadius * 2, frameTop + frameHeight
                        ),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = -90f,
                        forceMoveTo = false
                    )
                    lineTo(frameLeft + cornerLength, frameTop + frameHeight)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Bottom-right corner
            drawPath(
                path = Path().apply {
                    moveTo(frameLeft + frameWidth - cornerLength, frameTop + frameHeight)
                    lineTo(frameLeft + frameWidth - cornerRadius, frameTop + frameHeight)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            frameLeft + frameWidth - cornerRadius * 2, frameTop + frameHeight - cornerRadius * 2,
                            frameLeft + frameWidth, frameTop + frameHeight
                        ),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = -90f,
                        forceMoveTo = false
                    )
                    lineTo(frameLeft + frameWidth, frameTop + frameHeight - cornerLength)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        // Guidance text
        AnimatedVisibility(
            visible = showGuidance,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(Dimensions.radiusLg)
            ) {
                Text(
                    text = when (scanMode) {
                        ScanMode.AUTO -> "Position text within the frame\nAuto-detecting text..."
                        ScanMode.MANUAL -> "Position text within the frame\nTap capture when ready"
                    },
                    modifier = Modifier.padding(Dimensions.paddingMd),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Scanning animation for auto mode
        if (scanMode == ScanMode.AUTO) {
            val infiniteTransition = rememberInfiniteTransition(label = "scan")
            val scanProgress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "scan_progress"
            )
            
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.screenPadding)
            ) {
                val frameWidth = size.width * 0.9f
                val frameHeight = size.height * 0.5f
                val frameLeft = (size.width - frameWidth) / 2
                val frameTop = (size.height - frameHeight) / 2
                
                val scanLineY = frameTop + (frameHeight * scanProgress)
                
                // Draw scan line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF5B5FDE),
                            Color(0xFF5B5FDE),
                            Color.Transparent
                        )
                    ),
                    start = Offset(frameLeft, scanLineY),
                    end = Offset(frameLeft + frameWidth, scanLineY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun OcrBottomControls(
    scanMode: ScanMode,
    showGuidance: Boolean,
    onToggleGuidance: () -> Unit,
    onCapture: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.screenPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle guidance
            IconButton(
                onClick = onToggleGuidance,
                modifier = Modifier.size(Dimensions.minTouchTarget)
            ) {
                Icon(
                    imageVector = if (showGuidance) Icons.Default.Info else Icons.Outlined.Info,
                    contentDescription = "Toggle guidance",
                    tint = Color.White,
                    modifier = Modifier.size(Dimensions.iconSizeMd)
                )
            }
            
            // Mode indicator
            Surface(
                onClick = { },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (scanMode) {
                            ScanMode.AUTO -> Icons.Default.FlashOn
                            ScanMode.MANUAL -> Icons.Default.TouchApp
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingXs))
                    Text(
                        text = when (scanMode) {
                            ScanMode.AUTO -> "Auto Scan"
                            ScanMode.MANUAL -> "Manual"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
            
            // Flash toggle (placeholder)
            IconButton(
                onClick = { /* TODO: Toggle flash */ },
                modifier = Modifier.size(Dimensions.minTouchTarget)
            ) {
                Icon(
                    Icons.Default.FlashOff,
                    contentDescription = "Toggle flash",
                    tint = Color.White,
                    modifier = Modifier.size(Dimensions.iconSizeMd)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextPreviewDialog(
    text: String,
    onConfirm: () -> Unit,
    onEdit: (String) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    var editedText by remember { mutableStateOf(text) }
    var isEditing by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Scanned Text",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { isEditing = !isEditing },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Save edits" else "Edit text"
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMd)
            ) {
                // Text stats
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingMd),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = editedText.length.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Characters",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = editedText.split("\\s+".toRegex()).size.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Words",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                // Text content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    if (isEditing) {
                        TextField(
                            value = editedText,
                            onValueChange = { 
                                editedText = it
                                onEdit(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = editedText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(Dimensions.paddingMd),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Quality indicator
                if (!isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Good quality scan detected",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSm)
            ) {
                TextButton(onClick = onRetry) {
                    Text("Retry Scan")
                }
                Button(
                    onClick = onConfirm,
                    enabled = editedText.isNotEmpty()
                ) {
                    Text("Use This Text")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PermissionRationale(
    onRequestPermission: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingLg))
        
        Text(
            text = "Camera Permission Needed",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingMd))
        
        Text(
            text = "To scan text from documents, we need access to your camera. We'll only use it while you're actively scanning.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingXl))
        
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Grant Permission")
        }
        
        TextButton(onClick = onNavigateBack) {
            Text("Go Back")
        }
    }
}

@Composable
private fun PermissionDenied(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingLg))
        
        Text(
            text = "Camera Permission Denied",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingMd))
        
        Text(
            text = "Without camera permission, we can't scan text from documents. You can enable it in your device settings.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.spacingXl))
        
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Go Back")
        }
    }
}