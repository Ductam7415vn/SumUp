# Processing Screen - Technical Specification

## 🎯 Overview
Critical for perceived performance. Users hate waiting without knowing what's happening. Design for psychology, not just functionality.

## 📱 Layout Structure

### Portrait Layout
```
┌─────────────────────────────┐
│ StatusBar (translucent)      │
├─────────────────────────────┤
│                             │ 24dp top
│                             │
│     ╭───────────────╮       │
│     │               │       │ 120dp
│     │   Gemini      │       │ Animated
│     │   Logo        │       │
│     │               │       │
│     ╰───────────────╯       │
│                             │
│   "Understanding context..." │ 24sp
│                             │ 16dp gap
│   ████████████░░░░░ 70%    │ 8dp height
│                             │
│                             │
│   ⏱ Taking longer than usual│ 14sp
│                             │ (after 5s)
│                             │
│                             │
│                             │
│        [ Cancel ]           │ TextButton
│                             │ 48dp bottom
└─────────────────────────────┘
```

## 🛠️ Implementation

### Progress Algorithm
```kotlin
class ProcessingProgressManager {
    private var startTime = 0L
    private var apiCallStarted = false
    
    fun getProgress(realApiProgress: Float? = null): ProcessingState {
        val elapsed = System.currentTimeMillis() - startTime
        
        return when {
            elapsed < 1000 -> ProcessingState(
                progress = lerp(0f, 0.3f, elapsed / 1000f),
                message = "Reading your text..."
            )
            elapsed < 3000 && realApiProgress == null -> ProcessingState(
                progress = lerp(0.3f, 0.5f, (elapsed - 1000) / 2000f),
                message = "Understanding context..."
            )
            elapsed < 5000 -> ProcessingState(
                progress = realApiProgress?.let { lerp(0.5f, 0.7f, it) } ?: 0.65f,
                message = "Creating summary..."
            )
            elapsed < 7000 -> ProcessingState(
                progress = 0.85f,
                message = "Almost ready..."
            )
            else -> ProcessingState(
                progress = 0.95f,
                message = "Finalizing...",
                showTimeout = true
            )
        }
    }
}

data class ProcessingState(
    val progress: Float,
    val message: String,
    val showTimeout: Boolean = false
)
```

### Animated Logo Component
```kotlin
@Composable
fun AnimatedGeminiLogo() {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Rotation animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )
    
    // Pulse animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.gemini_logo),
            contentDescription = "Processing",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
```

### Progress Bar with Animation
```kotlin
@Composable
fun SmartProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )
    
    // Shimmer effect
    val shimmerTransition = rememberInfiniteTransition()
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .drawWithContent {
                drawContent()
                // Add shimmer effect on active portion
                if (animatedProgress > 0f) {
                    drawShimmer(shimmerOffset, animatedProgress)
                }
            },
        color = getProgressColor(animatedProgress),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

fun getProgressColor(progress: Float): Color {
    return when {
        progress < 0.3f -> Color(0xFF4CAF50)  // Green
        progress < 0.7f -> Color(0xFF2196F3)  // Blue  
        else -> Color(0xFF9C27B0)             // Purple
    }
}
```

### Timeout Management
```kotlin
@Composable
fun ProcessingScreen(
    onCancel: () -> Unit,
    onComplete: (String) -> Unit,
    onError: (Throwable) -> Unit
) {
    var timeoutLevel by remember { mutableStateOf(0) }
    var showTimeoutMessage by remember { mutableStateOf(false) }
    
    // Timeout messages
    LaunchedEffect(Unit) {
        delay(5000)
        timeoutLevel = 1
        showTimeoutMessage = true
        
        delay(5000) // 10s total
        timeoutLevel = 2
        
        delay(5000) // 15s total
        timeoutLevel = 3
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedGeminiLogo()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = progressState.message,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SmartProgressBar(progressState.progress)
        
        // Timeout messages
        AnimatedVisibility(
            visible = showTimeoutMessage,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = when (timeoutLevel) {
                        1 -> "This is taking longer than usual..."
                        2 -> "Almost there... Large texts take more time"
                        3 -> "Still processing. You can try with shorter text"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                if (timeoutLevel >= 3) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            onCancel()
                            // Navigate to input with suggestion to shorten text
                        }
                    ) {
                        Text("Try Shorter Text")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        TextButton(onClick = onCancel) {
            Text("Cancel")
        }
    }
}
```

## 🎭 Error Handling

### Network Error State
```kotlin
@Composable
fun NetworkErrorDialog(onRetry: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        icon = {
            Icon(
                Icons.Default.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Connection Lost")
        },
        text = {
            Text("Check your internet and try again. Summary feature needs internet.")
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
```

### API Error Recovery
```kotlin
sealed class ProcessingError {
    object NetworkError : ProcessingError()
    object ServerError : ProcessingError()
    object RateLimitError : ProcessingError()
    object TimeoutError : ProcessingError()
    data class UnknownError(val throwable: Throwable) : ProcessingError()
}

@Composable
fun ProcessingErrorHandler(
    error: ProcessingError,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    val (title, message, icon) = when (error) {
        ProcessingError.NetworkError -> Triple(
            "No Internet Connection",
            "Check your connection and try again",
            Icons.Default.WifiOff
        )
        ProcessingError.ServerError -> Triple(
            "Service Unavailable",
            "Our servers are having issues. Please try again",
            Icons.Default.Error
        )
        ProcessingError.RateLimitError -> Triple(
            "Daily Limit Reached",
            "You've used all your summaries today",
            Icons.Default.HourglassEmpty
        )
        ProcessingError.TimeoutError -> Triple(
            "Request Timed Out",
            "The server took too long to respond",
            Icons.Default.Timer
        )
        is ProcessingError.UnknownError -> Triple(
            "Something Went Wrong",
            "An unexpected error occurred",
            Icons.Default.ErrorOutline
        )
    }
    
    ErrorDialog(
        title = title,
        message = message,
        icon = icon,
        primaryAction = "Try Again" to onRetry,
        secondaryAction = "Cancel" to onCancel
    )
}
```

## 📊 Analytics & Monitoring

### Performance Metrics
```kotlin
class ProcessingAnalytics {
    fun trackProcessingStart(textLength: Int) {
        Analytics.track("processing_started", mapOf(
            "text_length" to textLength,
            "timestamp" to System.currentTimeMillis()
        ))
    }
    
    fun trackProcessingComplete(
        duration: Long,
        textLength: Int,
        success: Boolean
    ) {
        Analytics.track("processing_completed", mapOf(
            "duration_ms" to duration,
            "text_length" to textLength,
            "success" to success,
            "timeout_shown" to timeoutLevel > 0
        ))
    }
    
    fun trackUserCancellation(
        duration: Long,
        timeoutLevel: Int
    ) {
        Analytics.track("processing_cancelled", mapOf(
            "duration_ms" to duration,
            "timeout_level" to timeoutLevel,
            "user_patience" to when {
                duration < 3000 -> "impatient"
                duration < 10000 -> "normal"
                else -> "patient"
            }
        ))
    }
}
```

## ♿ Accessibility

### Screen Reader Support
```kotlin
@Composable
fun AccessibleProcessingScreen() {
    val a11yManager = LocalAccessibilityManager.current
    
    LaunchedEffect(progressState) {
        // Announce progress milestones
        when (progressState.progress.toInt()) {
            25, 50, 75 -> {
                a11yManager?.announce(
                    "Processing ${progressState.progress.toInt()} percent complete"
                )
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = "${progressState.message}. ${progressState.progress.toInt()} percent complete"
            }
    ) {
        // Processing UI
    }
}
```

## 🎯 Performance Targets

### Time Budgets
- **Screen appears**: <200ms after navigation
- **First progress movement**: <100ms
- **Animation frame rate**: 60 FPS
- **Progress updates**: Every 100ms max
- **Cancel response**: <50ms

### Memory Usage
- **Peak memory**: <50MB additional
- **Animation overhead**: <5MB
- **Cleanup on exit**: Complete within 1s

## ⚠️ Critical Reality Checks

### What Users Actually Care About
1. **Movement** - Something is happening
2. **Time estimate** - How long will this take?
3. **Cancel works** - Can I escape?
4. **Doesn't crash** - Completes reliably

### What Doesn't Matter
1. Perfect progress accuracy
2. Fancy animations (unless free)
3. Technical processing details
4. Brand messaging during wait

### Common Failures
- API timeout with no feedback
- Progress stuck at 99%
- Cancel doesn't actually cancel
- Animation drops to 15 FPS on budget phones

---

**Brutal Truth**: Users tolerate 3-5 seconds max before getting antsy. After 10 seconds, you've lost them. Design for 95th percentile latency, not the average. A smooth lie is better than a jerky truth.
