package com.example.sumup.presentation.screens.ocr.components

import android.content.Context
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.sumup.presentation.screens.ocr.OcrUiState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(
    uiState: OcrUiState,
    onTextDetected: (String, Float) -> Unit,
    onCaptureClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onTextScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var showCameraPlaceholder by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        if (showCameraPlaceholder) {
            CameraPlaceholder()
        }
        
        CameraXPreview(
            onTextDetected = onTextDetected,
            onCaptureImage = { capture ->
                imageCapture = capture
                showCameraPlaceholder = false
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar
        TopAppBar(
            title = { Text("Scan Text", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // OCR overlay
        OcrOverlay(
            uiState = uiState,
            onCaptureClick = {
                imageCapture?.let { capture ->
                    captureImage(
                        context = context,
                        imageCapture = capture,
                        onTextScanned = onTextScanned,
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                } ?: run {
                    onCaptureClick()
                }
            },
            onTextScanned = onTextScanned,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    onTextScanned: (String) -> Unit,
    onError: (String) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )
    
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                onError("Photo capture failed: ${exc.message}")
            }
            
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Process the captured image with ML Kit
                val image = InputImage.fromFilePath(context, android.net.Uri.fromFile(photoFile))
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (visionText.text.isNotEmpty()) {
                            onTextScanned(visionText.text)
                        } else {
                            onError("No text detected. Please try again.")
                        }
                    }
                    .addOnFailureListener { e ->
                        onError("Text recognition failed: ${e.message}")
                    }
                    .addOnCompleteListener {
                        // Clean up
                        photoFile.delete()
                        recognizer.close()
                    }
            }
        }
    )
}