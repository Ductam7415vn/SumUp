# 📸 KẾ HOẠCH TRIỂN KHAI OCR CHO SUMUP

## 🎯 MỤC TIÊU
Chuyển đổi tính năng OCR từ mock implementation sang tích hợp thực sự với ML Kit Text Recognition và CameraX.

## 📊 HIỆN TRẠNG

### ✅ Đã có sẵn:
1. **CameraXPreview.kt** - Implementation đầy đủ với:
   - CameraX setup
   - ML Kit Text Recognition
   - Real-time text detection
   - Advanced confidence calculation
   - Performance optimization (throttling)

2. **UI Components**:
   - OcrScreen với animations
   - CameraPreview UI
   - OcrOverlay
   - Review dialog
   - Permission handling

### ❌ Vấn đề:
1. **OcrViewModel** đang dùng mock data
2. **CameraPreview.kt** gọi CameraPlaceholder thay vì CameraXPreview
3. Chưa kết nối real OCR với navigation flow

## 📋 KẾ HOẠCH CHI TIẾT

### Phase 1: Kết nối CameraX với UI (2-3 giờ)

#### 1.1 Update CameraPreview Component
```kotlin
// app/src/main/java/com/example/sumup/presentation/screens/ocr/components/CameraPreview.kt

@Composable
fun CameraPreview(
    uiState: OcrUiState,
    onTextDetected: (String, Float) -> Unit,
    onCaptureClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onTextScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Thay thế CameraPlaceholder bằng CameraXPreview
        CameraXPreview(
            onTextDetected = { text, confidence ->
                onTextDetected(text, confidence)
                // Auto-trigger review khi confidence cao
                if (confidence > 0.7f && text.length > 50) {
                    onTextScanned(text)
                }
            },
            onCaptureImage = { capture ->
                imageCapture = capture
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Giữ nguyên Overlay và Controls
        OcrOverlay(
            detectedText = uiState.detectedText,
            confidence = uiState.confidence,
            ocrState = uiState.ocrState
        )
        
        CameraControls(
            isProcessing = uiState.isProcessing,
            onCaptureClick = {
                // Manual capture khi user click
                imageCapture?.let { capture ->
                    captureAndProcess(capture, onTextDetected)
                }
            },
            onNavigateBack = onNavigateBack,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
```

#### 1.2 Implement Manual Capture
```kotlin
private fun captureAndProcess(
    imageCapture: ImageCapture,
    onTextDetected: (String, Float) -> Unit
) {
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
        File.createTempFile("ocr_", ".jpg")
    ).build()
    
    imageCapture.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Process saved image với ML Kit
                processImageFile(output.savedUri, onTextDetected)
            }
            override fun onError(exception: ImageCaptureException) {
                // Handle error
            }
        }
    )
}
```

### Phase 2: Update OcrViewModel (1-2 giờ)

#### 2.1 Remove Mock Logic
```kotlin
// app/src/main/java/com/example/sumup/presentation/screens/ocr/OcrViewModel.kt

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    fun onTextDetected(text: String, confidence: Float) {
        _uiState.update { 
            it.copy(
                detectedText = text,
                confidence = confidence,
                ocrState = when {
                    text.isEmpty() -> OcrState.Searching
                    confidence < 0.5f -> OcrState.Processing
                    else -> OcrState.Ready
                }
            )
        }
    }
    
    fun processDetectedText() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            
            try {
                // Validate text
                val text = _uiState.value.detectedText
                if (text.length < 20) {
                    throw Exception("Text too short. Please scan more content.")
                }
                
                // Clean and prepare text
                val cleanedText = cleanExtractedText(text)
                
                // Navigate to main screen with text
                savedStateHandle["scanned_text"] = cleanedText
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        navigateToMain = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        error = AppError.OCRFailedError(e.message)
                    )
                }
            }
        }
    }
    
    private fun cleanExtractedText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(5000) // Respect character limit
    }
}
```

### Phase 3: Integration & Navigation (1 giờ)

#### 3.1 Update Navigation Flow
```kotlin
// In OcrScreen.kt
LaunchedEffect(uiState.navigateToMain) {
    if (uiState.navigateToMain) {
        val scannedText = viewModel.getScannedText()
        onTextScanned(scannedText)
    }
}

// In MainViewModel.kt
fun setScannedText(text: String) {
    _uiState.update { 
        it.copy(
            inputText = text,
            inputType = InputType.OCR
        )
    }
}
```

#### 3.2 Update MainScreen to Handle OCR Input
```kotlin
// In SumUpNavigation.kt
composable(Screen.Ocr.route) {
    OcrScreen(
        onNavigateBack = { navController.popBackStack() },
        onTextScanned = { scannedText ->
            // Navigate back with result
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("scanned_text", scannedText)
            navController.popBackStack()
        }
    )
}

// In MainScreen
LaunchedEffect(Unit) {
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("scanned_text")
        ?.observe(lifecycleOwner) { text ->
            viewModel.setScannedText(text)
        }
}
```

### Phase 4: Optimization & Polish (2 giờ)

#### 4.1 Add Processing Indicators
```kotlin
@Composable
fun OcrProcessingIndicator(
    isProcessing: Boolean,
    confidence: Float,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isProcessing || confidence > 0,
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scanning text...")
                } else {
                    val confidencePercent = (confidence * 100).toInt()
                    LinearProgressIndicator(
                        progress = confidence,
                        modifier = Modifier.width(100.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confidence: $confidencePercent%")
                }
            }
        }
    }
}
```

#### 4.2 Add Guidance Overlay
```kotlin
@Composable
fun OcrGuidanceOverlay(
    ocrState: OcrState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Center reticle
        Canvas(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
        ) {
            drawRoundRect(
                color = when (ocrState) {
                    OcrState.Ready -> Color.Green
                    OcrState.Processing -> Color.Yellow
                    else -> Color.White
                },
                style = Stroke(width = 3.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx())
            )
        }
        
        // Guidance text
        Text(
            text = when (ocrState) {
                OcrState.Searching -> "Position text within frame"
                OcrState.Processing -> "Hold steady..."
                OcrState.Ready -> "Text detected! Tap to capture"
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
```

### Phase 5: Testing & Edge Cases (1-2 giờ)

#### 5.1 Test Cases
1. **Low light conditions** - Add flash toggle
2. **Blurry text** - Show retry prompt
3. **Multiple languages** - Test với tiếng Việt
4. **Long documents** - Handle text truncation
5. **No text detected** - Clear error message

#### 5.2 Error Handling
```kotlin
sealed class OcrError : AppError() {
    object NoTextDetected : OcrError() {
        override val message = "No text found. Try adjusting lighting or angle."
    }
    
    data class LowConfidence(val confidence: Float) : OcrError() {
        override val message = "Text unclear (${(confidence * 100).toInt()}%). Try holding steady."
    }
    
    object TextTooShort : OcrError() {
        override val message = "Please scan more text (minimum 20 characters)."
    }
    
    object CameraError : OcrError() {
        override val message = "Camera error. Please try again."
    }
}
```

## 📱 TESTING CHECKLIST

- [ ] Camera permission request works
- [ ] Real-time text detection functions
- [ ] Manual capture button works
- [ ] Text review dialog shows correct text
- [ ] Navigation to main screen with text
- [ ] Error handling for edge cases
- [ ] Performance on low-end devices
- [ ] Memory usage optimization
- [ ] Different lighting conditions
- [ ] Various text types (printed, handwritten)

## 🚀 DEPLOYMENT

### Dependencies Check
```gradle
// Already in build.gradle.kts:
implementation("androidx.camera:camera-camera2:1.4.0")
implementation("androidx.camera:camera-lifecycle:1.4.0")
implementation("androidx.camera:camera-view:1.4.0")
implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
```

### Permissions
```xml
<!-- Already in AndroidManifest.xml -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
```

## ⏱️ TIMELINE
- **Total estimate**: 7-10 giờ
- **Priority**: HIGH
- **Complexity**: MEDIUM

## 📈 SUCCESS METRICS
1. OCR accuracy > 85% in good lighting
2. Processing time < 2 seconds
3. User satisfaction with scan quality
4. Smooth navigation flow
5. Clear error messages

---

Sau khi hoàn thành kế hoạch này, tính năng OCR sẽ chuyển từ 60% mock lên 100% functional!