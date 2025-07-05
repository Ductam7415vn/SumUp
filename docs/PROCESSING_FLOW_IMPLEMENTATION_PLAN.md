# Processing Flow Implementation Plan

## Current Situation
- Processing screen exists with nice animations and progress tracking
- Currently only used for PDF processing (`navigateToProcessing = true`)
- Regular text summarization skips processing screen (`navigateToProcessing = false`)
- The screen has simulated progress with fixed delays

## Goal
Enable the processing screen for all API calls (both mock and real) with realistic progress tracking.

## Implementation Plan

### Option 1: Simple Processing Screen (Recommended)
Show processing screen with realistic but estimated progress based on text length.

**Pros:**
- Simple to implement
- Consistent UX for all operations
- Works with both mock and real API

**Cons:**
- Progress is estimated, not real

### Option 2: Real Progress Tracking
Implement actual progress tracking from API.

**Pros:**
- Accurate progress
- Better for very long operations

**Cons:**
- Requires API changes
- More complex implementation

## Recommended Solution (Option 1)

### 1. Update MainViewModel
```kotlin
private fun processSummarization(text: String) {
    summarizationJob = viewModelScope.launch {
        try {
            // Enable processing screen
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    navigateToProcessing = true, // CHANGED: Enable processing screen
                    error = null,
                    processingMessage = "Preparing your summary..."
                )
            }
            
            // Start progress simulation in parallel
            val progressJob = launch {
                simulateRealisticProgress(text.length)
            }
            
            // Make API call
            val result = summarizeTextUseCase(
                text = text,
                lengthMultiplier = lengthMultiplier
            )
            
            // Cancel progress simulation when done
            progressJob.cancel()
            
            result.onSuccess { summary ->
                // Update progress to 100%
                _uiState.update { 
                    it.copy(
                        processingProgress = 1f,
                        processingMessage = "Done!"
                    )
                }
                
                // Small delay for user to see completion
                delay(300)
                
                // Navigate to result
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        summary = summary,
                        summaryId = summary.id,
                        navigateToResult = true,
                        navigateToProcessing = false
                    )
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

### 2. Create Realistic Progress Simulation
```kotlin
private suspend fun simulateRealisticProgress(textLength: Int) {
    // Calculate estimated time based on text length
    val estimatedTime = when {
        textLength < 500 -> 3000L    // 3 seconds
        textLength < 2000 -> 5000L   // 5 seconds
        textLength < 5000 -> 8000L   // 8 seconds
        else -> 12000L               // 12 seconds
    }
    
    // Phase 1: Reading (0-25%)
    updateProgress(0.1f, "Reading your text...")
    delay(estimatedTime * 0.15)
    
    updateProgress(0.25f, "Analyzing content...")
    delay(estimatedTime * 0.20)
    
    // Phase 2: Understanding (25-60%)
    updateProgress(0.40f, "Understanding context...")
    delay(estimatedTime * 0.25)
    
    updateProgress(0.60f, "Identifying key points...")
    delay(estimatedTime * 0.20)
    
    // Phase 3: Creating (60-90%)
    updateProgress(0.75f, "Creating summary...")
    delay(estimatedTime * 0.15)
    
    updateProgress(0.90f, "Finalizing...")
    delay(estimatedTime * 0.05)
}
```

### 3. Update Processing Screen Navigation
```kotlin
// In MainScreen.kt
LaunchedEffect(uiState.navigateToProcessing) {
    if (uiState.navigateToProcessing) {
        onNavigateToProcessing()
        // Don't reset navigation flag here - let ProcessingScreen handle completion
    }
}
```

### 4. Update ProcessingScreen
```kotlin
@Composable
fun ProcessingScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Use actual progress from ViewModel
    val progress = uiState.processingProgress
    val message = uiState.processingMessage
    
    // Handle completion
    LaunchedEffect(uiState.navigateToResult) {
        if (uiState.navigateToResult && uiState.summaryId != null) {
            onComplete()
        }
    }
    
    // Rest of the UI...
}
```

## Benefits
1. **Consistent UX**: All summarizations show processing screen
2. **Better Feedback**: Users see progress for longer operations
3. **Professional Feel**: Smooth transitions and progress tracking
4. **Cancelable**: Users can cancel long operations
5. **Works Everywhere**: Same experience for mock and real API

## Alternative: Quick Toggle
Add a setting to enable/disable processing screen:
```kotlin
// In settings
var showProcessingScreen by remember { mutableStateOf(true) }

// In MainViewModel
if (settingsRepository.showProcessingScreen) {
    _uiState.update { it.copy(navigateToProcessing = true) }
} else {
    // Direct navigation to result
}
```

## Testing Plan
1. Test with short text (< 500 chars)
2. Test with medium text (500-2000 chars)
3. Test with long text (> 5000 chars)
4. Test cancellation at different progress points
5. Test error handling during processing
6. Test with both mock and real API