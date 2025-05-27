package com.example.sumup.presentation.screens.ocr.components

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
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            textRecognizer.close()
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
                val cameraProvider = cameraProviderFuture.get()
                
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
                
                // Image analysis use case for real-time text detection
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(android.util.Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(
                                imageProxy,
                                textRecognizer,
                                onTextDetected
                            )
                        }
                    }
                
                try {
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()
                    
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                        imageAnalyzer
                    )
                    
                    // Pass imageCapture to parent
                    imageCapture?.let { onCaptureImage(it) }
                    
                } catch (e: Exception) {
                    // Handle errors
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    onTextDetected: (String, Float) -> Unit
) {
    imageProxy.image?.let { mediaImage ->
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                val confidence = calculateConfidence(visionText)
                
                if (text.isNotEmpty()) {
                    onTextDetected(text, confidence)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } ?: imageProxy.close()
}

private fun calculateConfidence(visionText: com.google.mlkit.vision.text.Text): Float {
    val blocks = visionText.textBlocks
    if (blocks.isEmpty()) return 0f
    
    // Simple confidence calculation based on text length and block count
    val avgBlockLength = blocks.map { it.text.length }.average()
    val blockCount = blocks.size
    
    return when {
        blockCount > 5 && avgBlockLength > 20 -> 0.9f
        blockCount > 3 && avgBlockLength > 10 -> 0.7f
        blockCount > 1 && avgBlockLength > 5 -> 0.5f
        else -> 0.3f
    }
}