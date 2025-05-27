# Camera OCR Screen - Technical Specification

## 🎯 Overview
Complex camera interface for text extraction. OCR on mobile is inherently challenging - expect 30% failure rate and design accordingly.

## 📱 Layout Structure

### Camera Preview - Portrait
```
┌─────────────────────────────┐
│ Status (dark overlay)        │
│ [X]           Flash   Import │
├─────────────────────────────┤
│ ╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱ │
│ ╱┌───────────────────────┐╱ │
│ ╱│                       │╱ │
│ ╱│   Document Area       │╱ │ 70% height
│ ╱│                       │╱ │
│ ╱│  ← Align text here →  │╱ │
│ ╱│                       │╱ │
│ ╱└───────────────────────┘╱ │
│ ╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱ │
│                              │
│ 🔍 Looking for text...       │ OCR State
│                              │
│ [Manual] Auto ○              │ Toggle
│                              │
│        ( Capture )           │ 72dp button
└─────────────────────────────┘
```

## 🛠️ Implementation

### OCR State Management
```kotlin
sealed class OCRState {
    object Searching : OCRState()    // "Looking for text..."
    object Focusing : OCRState()     // "Focusing..."
    object Ready : OCRState()        // "Ready to capture!"
    object Processing : OCRState()   // "Processing..."
}

@Composable
fun CameraOCRScreen() {
    var ocrState by remember { mutableStateOf(OCRState.Searching) }
    var captureMode by remember { mutableStateOf(CaptureMode.Auto) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onTextDetected = { confidence ->
                ocrState = when {
                    confidence > 0.8f -> OCRState.Ready
                    confidence > 0.5f -> OCRState.Focusing
                    else -> OCRState.Searching
                }
            }
        )
        
        // Guide Overlay
        GuideOverlay(
            state = ocrState,
            modifier = Modifier.align(Alignment.Center)
        )
        
        // Bottom Controls
        CameraControls(
            state = ocrState,
            mode = captureMode,
            onModeToggle = { captureMode = it },
            onCapture = { handleCapture() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
```

### Guide Overlay Component
```kotlin
@Composable
fun GuideOverlay(state: OCRState, modifier: Modifier) {
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OCRState.Searching -> Color.Yellow
            OCRState.Focusing -> Color.Orange
            OCRState.Ready -> Color.Green
            OCRState.Processing -> Color.Blue
        },
        animationSpec = tween(300)
    )
    
    val borderStyle = when (state) {
        OCRState.Searching -> BorderStyle.Dashed
        else -> BorderStyle.Solid
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f)
            .border(
                width = 3.dp,
                color = borderColor,
                style = borderStyle
            )
            .padding(16.dp)
    ) {
        // Guide text and animations
        Text(
            text = when (state) {
                OCRState.Searching -> "🔍 Looking for text..."
                OCRState.Focusing -> "📸 Focusing..."
                OCRState.Ready -> "✓ Ready to capture!"
                OCRState.Processing -> "⚙️ Processing..."
            },
            color = borderColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
```

### Post-Capture Review
```kotlin
@Composable
fun OCRReviewScreen(
    detectedText: String,
    confidence: Float,
    onRetake: () -> Unit,
    onEdit: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        TopAppBar(
            title = { Text("Review Scanned Text") },
            navigationIcon = {
                IconButton(onClick = onRetake) {
                    Icon(Icons.Default.Close, "Cancel")
                }
            }
        )
        
        // Detected text preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SelectionContainer {
                Text(
                    text = detectedText,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // Confidence indicator
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Assessment, "Confidence")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Confidence: ${(confidence * 100).toInt()}%")
        }
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = onRetake) {
                Text("Retake")
            }
            OutlinedButton(onClick = onEdit) {
                Text("Edit")
            }
            Button(onClick = onContinue) {
                Text("Continue")
            }
        }
    }
}
```

## 🎮 Interaction Flows

### Auto-Capture Logic
```kotlin
class AutoCaptureManager {
    private var textStableStartTime = 0L
    private val stabilityThresholdMs = 2000L
    
    fun onTextDetected(text: String, confidence: Float) {
        if (confidence > 0.8f && text.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            
            if (textStableStartTime == 0L) {
                textStableStartTime = currentTime
            } else if (currentTime - textStableStartTime > stabilityThresholdMs) {
                triggerAutoCapture()
            }
        } else {
            textStableStartTime = 0L
        }
    }
    
    private fun triggerAutoCapture() {
        hapticFeedback.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        captureImage()
    }
}
```

### Error Recovery Strategies
```kotlin
sealed class OCRError {
    object NoTextDetected : OCRError()
    object TooBlurry : OCRError()
    object LowLight : OCRError()
    object GlareDetected : OCRError()
    object TextTooSmall : OCRError()
    object WrongAngle : OCRError()
    object CameraUnavailable : OCRError()
}

@Composable
fun OCRErrorHandler(error: OCRError, onRetry: () -> Unit, onTypeInstead: () -> Unit) {
    val (title, message, icon) = when (error) {
        OCRError.NoTextDetected -> Triple(
            "No Text Found",
            "Try moving closer or improving lighting",
            "🔍"
        )
        OCRError.TooBlurry -> Triple(
            "Text is Blurry",
            "Hold device steady and tap to focus",
            "📷"
        )
        OCRError.LowLight -> Triple(
            "Too Dark",
            "Try turning on flash or find better lighting",
            "💡"
        )
        // ... other error cases
    }
    
    ErrorDialog(
        title = title,
        message = message,
        icon = icon,
        primaryAction = "Try Again" to onRetry,
        secondaryAction = "Type Instead" to onTypeInstead
    )
}
```

## 📊 Performance Optimizations

### Camera Initialization
```kotlin
class CameraManager {
    suspend fun initializeCamera(): Result<Camera> {
        return try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()
            
            // Configure camera with optimal settings
            val preview = Preview.Builder()
                .setTargetResolution(Size(1080, 1920))
                .build()
                
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
                
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))  // Lower res for analysis
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
                imageAnalysis
            )
            
            Result.success(camera)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Text Recognition
```kotlin
class TextRecognizer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    suspend fun recognizeText(image: InputImage): Result<RecognizedText> {
        return suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    continuation.resume(Result.success(result))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }
    }
    
    fun calculateConfidence(result: Text): Float {
        val blocks = result.textBlocks
        if (blocks.isEmpty()) return 0f
        
        return blocks.map { it.confidence ?: 0f }.average().toFloat()
    }
}
```

## ⚠️ Edge Cases & Testing

### Common Failure Scenarios
| **Error** | **Detection** | **Recovery** |
|-----------|---------------|--------------|
| **No permission** | Check on entry | Settings guide |
| **Camera busy** | Init failure | Retry mechanism |
| **Poor lighting** | Confidence <0.3 | Flash suggestion |
| **Motion blur** | Accelerometer | Stability tips |
| **Multiple languages** | ML Kit detection | Language picker |
| **Handwriting** | Text patterns | Lower expectations |

### Testing Matrix
```kotlin
@Test fun `camera initializes within 500ms`()
@Test fun `handles permission denial gracefully`()
@Test fun `detects text in good conditions`()
@Test fun `fails gracefully in poor lighting`()
@Test fun `auto-capture works with stable text`()
@Test fun `manual capture always works`()
@Test fun `review screen shows detected text`()
@Test fun `confidence calculation accurate`()
@Test fun `handles device rotation`()
@Test fun `memory usage stays reasonable`()
```

## 🚨 Critical Reality Checks

### What Actually Kills OCR UX
1. **Slow camera startup** (500ms+ on budget phones)
2. **Auto-focus hunting** (never locks in low light)
3. **User expectations** (thinks it's magic)
4. **Permission anxiety** (camera = privacy concern)

### What Actually Works
1. **Guide actively** - Show users how to position
2. **Fail fast** - Don't make them wait for nothing
3. **Always provide escape** - Manual typing option
4. **Set realistic expectations** - "Works best with printed text"

### MVP Constraints
- Skip handwriting recognition (20% success rate)
- English/Latin scripts only initially
- No multi-column layouts (newspapers)
- Basic confidence scoring only

---

**Brutal Truth**: 30% of OCR attempts fail on first try. Design for the retry flow, not the happy path. A user who types manually is better than one who gives up frustrated.
