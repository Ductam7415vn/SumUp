# Summary Length Fix Guide

## Current Behavior vs Expected Behavior

### Current (Problematic) Behavior:
1. User selects summary length (Brief/Standard/Detailed) on MainScreen
2. API is called with `maxLength` parameter based on selection
3. API returns ALL THREE levels in one response:
   - `briefOverview`
   - `summary` (standard)
   - `detailedSummary`
4. ResultScreen shows cached data, switching instantly between levels

### Expected Behavior:
1. User selects summary length on MainScreen
2. API is called to generate ONLY that specific length
3. ResultScreen shows the generated summary
4. If user wants a different length, they need to regenerate

## Why This Happens

The API (GeminiApiService) is designed to return multiple summary tiers in one call for efficiency, but this creates confusion because:
- The length selector on MainScreen seems meaningless
- Users expect different API calls for different lengths
- The instant switching makes it seem like no processing is happening

## Solutions

### Option 1: Keep Multi-Tier Response (Recommended)
- Remove the summary length selector from MainScreen
- Keep the view mode switcher in ResultScreen
- Make it clear this is just switching views, not regenerating

### Option 2: Single-Tier API Calls
- Modify the API to return only the requested length
- Implement "Regenerate with different length" in ResultScreen
- This requires new API calls for each length change

### Option 3: Hybrid Approach
- Keep multi-tier for initial generation
- Add "Regenerate" button for truly different summaries
- Show processing time when actually calling API

## Implementation Steps for Option 2 (True Length-Based Generation)

1. **Modify GeminiPromptBuilder** to generate only the requested length:
```kotlin
fun buildPrompt(text: String, style: String, maxLength: Int): String {
    return when {
        maxLength <= 300 -> generateBriefPrompt(text, style)
        maxLength <= 800 -> generateStandardPrompt(text, style)
        else -> generateDetailedPrompt(text, style)
    }
}
```

2. **Update API Response** to return single summary:
```kotlin
data class SummarizeResponse(
    val summary: String,
    val bullets: List<String>,
    val summaryType: SummaryLength, // BRIEF, STANDARD, or DETAILED
    // Remove: briefOverview, detailedSummary
)
```

3. **Add Regenerate Function** in ResultViewModel:
```kotlin
fun regenerateWithLength(length: SummaryLength) {
    // Navigate back to processing with new length parameter
    // This ensures progress bar shows and new API call is made
}
```

4. **Update ResultScreen** to show single summary with regenerate option:
```kotlin
// Instead of view mode switcher, show current length and regenerate button
Row {
    Text("Current: ${summary.summaryType}")
    TextButton(onClick = { showLengthDialog = true }) {
        Text("Change Length")
    }
}
```

## Quick Fix for Progress Bar

The progress bar issue is separate. To verify it's working:

1. Add debug logging in ProcessingScreen:
```kotlin
LaunchedEffect(progress) {
    Log.d("ProcessingScreen", "Progress: $progress, Message: $message")
}
```

2. Check MainViewModel is actually updating progress:
```kotlin
private fun updateProgress(progress: Float, message: String) {
    Log.d("MainViewModel", "Updating progress to: $progress")
    _uiState.update { 
        it.copy(
            processingProgress = progress,
            processingMessage = message
        )
    }
}
```

3. Ensure ProcessingScreen receives the shared ViewModel (already fixed in navigation)

## Testing

1. Set mock mode to test without API calls:
   - In TransitionNavigation.kt, set `isMockMode = true`
   - This will show smooth progress animation

2. For real API calls:
   - Add breakpoints in `simulateRealisticProgress`
   - Check Logcat for "MainViewModel" and "ProcessingScreen" tags

## Conclusion

The current implementation is actually quite sophisticated - it generates multiple summary levels in one API call for efficiency. The confusion comes from the UI making it seem like these are separate operations. Either embrace the multi-tier approach fully or implement true regeneration for different lengths.