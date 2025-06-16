package com.example.sumup.presentation.screens.ocr.components

import android.graphics.Rect
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun CameraXPreview(
    onTextDetected: (String, Float) -> Unit,
    onCaptureImage: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    
    // Create executor and text recognizer with proper lifecycle management
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable).apply {
                name = "CameraXAnalysis"
                priority = Thread.NORM_PRIORITY
            }
        }
    }
    
    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    
    // Camera provider instance
    val cameraProvider = remember { mutableStateOf<ProcessCameraProvider?>(null) }
    
    // Performance optimization: throttle text detection
    val isProcessing = remember { AtomicBoolean(false) }
    val lastProcessTime = remember { mutableStateOf(0L) }
    val processingThrottleMs = 500L // Process every 500ms max
    
    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProvider.value?.unbindAll()
                cameraExecutor.shutdown()
                if (!cameraExecutor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS)) {
                    cameraExecutor.shutdownNow()
                }
                textRecognizer.close()
            } catch (e: Exception) {
                android.util.Log.e("CameraXPreview", "Error cleaning up resources", e)
            }
        }
    }
    
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                try {
                    // Use runCatching to safely get the provider
                    val provider = runCatching { cameraProviderFuture.get() }.getOrNull()
                    if (provider != null) {
                        cameraProvider.value = provider
                
                // Preview use case
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                
                // Image capture use case
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                
                // Image analysis use case for real-time text detection with optimization
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(android.util.Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setImageQueueDepth(1) // Keep only latest frame
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxyAdvanced(
                                imageProxy,
                                textRecognizer,
                                onTextDetected,
                                isProcessing,
                                lastProcessTime,
                                processingThrottleMs
                            )
                        }
                    }
                
                    try {
                        // Unbind all use cases before rebinding
                        provider.unbindAll()
                        
                        // Bind use cases to camera
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture,
                            imageAnalyzer
                        )
                        
                        // Pass imageCapture to parent
                        imageCapture?.let { onCaptureImage(it) }
                        
                        } catch (e: Exception) {
                            android.util.Log.e("CameraXPreview", "Failed to bind use cases", e)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("CameraXPreview", "Failed to get camera provider", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageProxyAdvanced(
    imageProxy: ImageProxy,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    onTextDetected: (String, Float) -> Unit,
    isProcessing: AtomicBoolean,
    lastProcessTime: MutableState<Long>,
    throttleMs: Long
) {
    val currentTime = System.currentTimeMillis()
    
    // Throttle processing to improve performance
    if (isProcessing.get() || currentTime - lastProcessTime.value < throttleMs) {
        imageProxy.close()
        return
    }
    
    isProcessing.set(true)
    lastProcessTime.value = currentTime
    
    imageProxy.image?.let { mediaImage ->
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                val analysisResult = analyzeTextQuality(visionText)
                
                if (text.isNotEmpty() && analysisResult.confidence > 0.3f) {
                    onTextDetected(text, analysisResult.confidence)
                }
            }
            .addOnFailureListener { e ->
                // Log error but don't interrupt the camera flow
                e.printStackTrace()
            }
            .addOnCompleteListener {
                isProcessing.set(false)
                imageProxy.close()
            }
    } ?: run {
        isProcessing.set(false)
        imageProxy.close()
    }
}

private data class TextAnalysisResult(
    val confidence: Float,
    val wordCount: Int,
    val lineCount: Int,
    val hasStructuredContent: Boolean
)

private fun analyzeTextQuality(visionText: com.google.mlkit.vision.text.Text): TextAnalysisResult {
    val blocks = visionText.textBlocks
    if (blocks.isEmpty()) return TextAnalysisResult(0f, 0, 0, false)
    
    // Advanced confidence calculation using multiple metrics
    val totalText = visionText.text
    val wordCount = totalText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    val lineCount = blocks.flatMap { it.lines }.size
    val blockCount = blocks.size
    
    // Analyze text structure and quality
    val avgWordsPerLine = if (lineCount > 0) wordCount.toFloat() / lineCount else 0f
    val avgCharsPerWord = if (wordCount > 0) totalText.length.toFloat() / wordCount else 0f
    val blockDensity = if (blocks.isNotEmpty()) lineCount.toFloat() / blockCount else 0f
    
    // Check for structured content (tables, lists, etc.)
    val hasNumbers = totalText.contains(Regex("\\d"))
    val hasPunctuation = totalText.contains(Regex("[.,;:!?]"))
    val hasMultipleLines = lineCount > 1
    val hasStructuredContent = hasNumbers && hasPunctuation && hasMultipleLines
    
    // Calculate geometric distribution of text blocks
    val blockPositions = blocks.map { block ->
        val boundingBox = block.boundingBox
        boundingBox?.let { 
            Pair(it.centerX().toFloat(), it.centerY().toFloat()) 
        }
    }.filterNotNull()
    
    val isWellDistributed = if (blockPositions.size > 1) {
        val xVariance = calculateVariance(blockPositions.map { it.first })
        val yVariance = calculateVariance(blockPositions.map { it.second })
        xVariance > 1000 || yVariance > 1000 // Good spatial distribution
    } else false
    
    // Comprehensive confidence score
    var confidence = 0f
    
    // Base confidence from content volume
    confidence += when {
        wordCount > 20 -> 0.4f
        wordCount > 10 -> 0.3f
        wordCount > 5 -> 0.2f
        wordCount > 0 -> 0.1f
        else -> 0f
    }
    
    // Bonus for text quality
    if (avgCharsPerWord in 3f..12f) confidence += 0.2f // Reasonable word length
    if (avgWordsPerLine in 2f..15f) confidence += 0.1f // Good line structure
    if (blockDensity > 1.5f) confidence += 0.1f // Rich content density
    
    // Bonus for structured content
    if (hasStructuredContent) confidence += 0.1f
    if (isWellDistributed) confidence += 0.1f
    
    // Cap confidence at 1.0
    confidence = confidence.coerceAtMost(1.0f)
    
    return TextAnalysisResult(
        confidence = confidence,
        wordCount = wordCount,
        lineCount = lineCount,
        hasStructuredContent = hasStructuredContent
    )
}

private fun calculateVariance(values: List<Float>): Float {
    if (values.isEmpty()) return 0f
    val mean = values.average().toFloat()
    return values.map { (it - mean) * (it - mean) }.average().toFloat()
}