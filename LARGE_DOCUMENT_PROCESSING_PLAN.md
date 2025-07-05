# Large Document Processing Implementation Plan

## Overview
Upgrade SumUp to handle 30-100 page documents efficiently with parallel processing, background execution, and resumable operations.

## Current Limitations
- Sequential processing only
- 30-second timeout kills large documents
- No background processing
- Cannot pause/resume
- UI freezes during long operations

## Implementation Phases

### Phase 1: Parallel Chunk Processing (2-3 days)

#### 1.1 Update SmartSectioningUseCase
```kotlin
// Before: Sequential processing
val sectionSummaries = sections.mapIndexed { index, section ->
    // Process one by one
}

// After: Parallel processing with coroutines
val sectionSummaries = coroutineScope {
    sections.mapIndexed { index, section ->
        async {
            // Process in parallel
            processSectionWithProgress(section, index)
        }
    }.awaitAll()
}
```

#### 1.2 Create ParallelSectionProcessor
```kotlin
class ParallelSectionProcessor @Inject constructor(
    private val summarizeTextUseCase: SummarizeTextUseCase
) {
    suspend fun processInBatches(
        sections: List<DocumentSection>,
        batchSize: Int = 3, // Process 3 chunks simultaneously
        onProgress: (Int, Int) -> Unit
    ): List<DocumentSection> = coroutineScope {
        sections.chunked(batchSize).flatMap { batch ->
            batch.map { section ->
                async {
                    val result = summarizeTextUseCase(section.content)
                    onProgress(sections.indexOf(section), sections.size)
                    section.copy(summary = result.getOrNull())
                }
            }.awaitAll()
        }
    }
}
```

#### 1.3 Update timeout strategy
```kotlin
// NetworkModule.kt - Increase timeouts for large docs
.connectTimeout(60, TimeUnit.SECONDS)
.readTimeout(60, TimeUnit.SECONDS)

// EnhancedGeminiApiService.kt - Per-chunk timeout
private const val TIMEOUT_PER_CHUNK = 45000L // 45s per chunk
private const val MAX_TOTAL_TIMEOUT = 600000L // 10 minutes total
```

### Phase 2: WorkManager Background Processing (3-4 days)

#### 2.1 Add WorkManager dependency
```kotlin
// app/build.gradle.kts
dependencies {
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
}
```

#### 2.2 Create SummarizationWorker
```kotlin
@HiltWorker
class SummarizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository,
    private val sectionProcessor: ParallelSectionProcessor
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val documentId = inputData.getString("document_id") ?: return Result.failure()
        val text = inputData.getString("text") ?: return Result.failure()
        
        // Update progress
        setProgress(workDataOf("progress" to 0))
        
        return try {
            // Process document
            val sections = createSmartSections(text)
            val results = sectionProcessor.processInBatches(
                sections = sections,
                onProgress = { current, total ->
                    val progress = (current * 100) / total
                    setProgress(workDataOf(
                        "progress" to progress,
                        "current_section" to current,
                        "total_sections" to total
                    ))
                }
            )
            
            // Save results
            summaryRepository.saveSectionedSummary(documentId, results)
            
            Result.success(workDataOf("summary_id" to documentId))
        } catch (e: Exception) {
            Result.retry() // Retry on failure
        }
    }
}
```

#### 2.3 Create WorkManagerHelper
```kotlin
@Singleton
class WorkManagerHelper @Inject constructor(
    private val workManager: WorkManager
) {
    fun enqueueSummarization(
        documentId: String,
        text: String,
        constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    ): LiveData<WorkInfo> {
        val data = workDataOf(
            "document_id" to documentId,
            "text" to text
        )
        
        val request = OneTimeWorkRequestBuilder<SummarizationWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("summarization_$documentId")
            .build()
        
        workManager.enqueueUniqueWork(
            "summarization_$documentId",
            ExistingWorkPolicy.KEEP,
            request
        )
        
        return workManager.getWorkInfoByIdLiveData(request.id)
    }
    
    fun cancelSummarization(documentId: String) {
        workManager.cancelAllWorkByTag("summarization_$documentId")
    }
}
```

### Phase 3: Streaming Results (2-3 days)

#### 3.1 Update Summary model for partial results
```kotlin
// domain/model/Summary.kt
data class Summary(
    // existing fields...
    val isPartial: Boolean = false,
    val processedSections: Int = 0,
    val totalSections: Int = 0,
    val sections: List<SectionSummary> = emptyList()
)

data class SectionSummary(
    val id: String,
    val title: String,
    val content: String,
    val summary: String,
    val status: ProcessingStatus
)

enum class ProcessingStatus {
    PENDING, PROCESSING, COMPLETED, FAILED
}
```

#### 3.2 Create StreamingSummaryRepository
```kotlin
interface StreamingSummaryRepository {
    suspend fun saveSectionResult(
        summaryId: String,
        sectionIndex: Int,
        sectionSummary: SectionSummary
    )
    
    fun observeSummaryProgress(summaryId: String): Flow<Summary>
    
    suspend fun markSummaryComplete(summaryId: String)
}
```

#### 3.3 Update ResultScreen to show partial results
```kotlin
@Composable
fun ResultScreen(summaryId: String) {
    val summary by viewModel.getSummary(summaryId).collectAsState()
    
    when {
        summary.isPartial -> PartialResultDisplay(
            summary = summary,
            onViewCompleted = { /* Navigate to completed sections */ }
        )
        else -> CompleteResultDisplay(summary)
    }
}

@Composable
fun PartialResultDisplay(summary: Summary) {
    Column {
        // Progress indicator
        LinearProgressIndicator(
            progress = summary.processedSections.toFloat() / summary.totalSections,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text("Processing: ${summary.processedSections}/${summary.totalSections} sections")
        
        // Show completed sections
        LazyColumn {
            items(summary.sections.filter { it.status == ProcessingStatus.COMPLETED }) { section ->
                SectionCard(section)
            }
        }
    }
}
```

### Phase 4: Pause/Resume Functionality (2 days)

#### 4.1 Add pause/resume to Worker
```kotlin
class ResumableSummarizationWorker : CoroutineWorker() {
    companion object {
        private val pausedJobs = mutableMapOf<String, Boolean>()
    }
    
    override suspend fun doWork(): Result {
        val documentId = inputData.getString("document_id") ?: return Result.failure()
        val resumeFromSection = inputData.getInt("resume_from", 0)
        
        // Check if paused
        while (pausedJobs[documentId] == true) {
            delay(1000) // Check every second
        }
        
        // Continue processing from saved position
        // ...
    }
    
    fun pauseJob(documentId: String) {
        pausedJobs[documentId] = true
    }
    
    fun resumeJob(documentId: String) {
        pausedJobs[documentId] = false
    }
}
```

#### 4.2 Update ProcessingScreen UI
```kotlin
@Composable
fun ProcessingScreen(
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    
    Column {
        // Progress display...
        
        Row {
            if (!isPaused) {
                OutlinedButton(onClick = {
                    onPause()
                    isPaused = true
                }) {
                    Icon(Icons.Default.Pause, null)
                    Text("Pause")
                }
            } else {
                Button(onClick = {
                    onResume()
                    isPaused = false
                }) {
                    Icon(Icons.Default.PlayArrow, null)
                    Text("Resume")
                }
            }
            
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}
```

### Phase 5: Optimized Chunking (1-2 days)

#### 5.1 Update SmartSectioningUseCase constants
```kotlin
companion object {
    const val SECTION_THRESHOLD = 10_000 // Keep same
    const val MAX_SECTION_SIZE = 8_000 // Increase from 5,000
    const val MIN_SECTION_SIZE = 2_000 // Increase from 1,000
    const val OVERLAP_SIZE = 300 // Increase from 200
    
    // New: Dynamic sizing based on document length
    fun getOptimalSectionSize(totalLength: Int): Int {
        return when {
            totalLength < 50_000 -> 5_000 // ~10 pages
            totalLength < 150_000 -> 8_000 // ~30 pages
            totalLength < 300_000 -> 10_000 // ~60 pages
            else -> 12_000 // 60+ pages
        }
    }
}
```

#### 5.2 Implement intelligent chunking
```kotlin
private fun createAdaptiveChunks(text: String): List<DocumentSection> {
    val optimalSize = getOptimalSectionSize(text.length)
    val chunks = mutableListOf<DocumentSection>()
    
    // Try to split at natural boundaries
    val naturalBreaks = findNaturalBreaks(text) // paragraphs, sections, chapters
    
    var currentChunk = StringBuilder()
    var currentStart = 0
    
    for (breakPoint in naturalBreaks) {
        val segment = text.substring(currentStart, breakPoint)
        
        if (currentChunk.length + segment.length <= optimalSize) {
            currentChunk.append(segment)
        } else {
            // Save current chunk
            chunks.add(createSection(currentChunk.toString(), currentStart))
            currentChunk = StringBuilder(segment)
            currentStart = breakPoint
        }
    }
    
    return chunks
}
```

### Phase 6: Enhanced UI (2 days)

#### 6.1 Create DetailedProgressScreen
```kotlin
@Composable
fun DetailedProgressScreen(
    workInfo: WorkInfo,
    onNavigateToPartialResults: () -> Unit
) {
    val progress = workInfo.progress.getInt("progress", 0)
    val currentSection = workInfo.progress.getInt("current_section", 0)
    val totalSections = workInfo.progress.getInt("total_sections", 0)
    
    Column {
        // Overall progress
        CircularProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier.size(120.dp)
        )
        
        Text("${progress}% Complete")
        
        // Section details
        Card {
            Column {
                Text("Processing Section $currentSection of $totalSections")
                LinearProgressIndicator(
                    progress = currentSection.toFloat() / totalSections
                )
                
                // Estimated time
                val estimatedRemaining = calculateRemainingTime(
                    currentSection, 
                    totalSections, 
                    averageTimePerSection = 15 // seconds
                )
                Text("Estimated time: ${formatDuration(estimatedRemaining)}")
            }
        }
        
        // Options
        Row {
            TextButton(onClick = onNavigateToPartialResults) {
                Text("View Completed Sections")
            }
            
            OutlinedButton(onClick = { /* Background */ }) {
                Text("Continue in Background")
            }
        }
    }
}
```

#### 6.2 Add notification for background processing
```kotlin
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun showProgressNotification(
        documentId: String,
        progress: Int,
        currentSection: Int,
        totalSections: Int
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Summarizing Document")
            .setContentText("Section $currentSection of $totalSections")
            .setProgress(100, progress, false)
            .setSmallIcon(R.drawable.ic_sumup_logo)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_pause,
                "Pause",
                getPausePendingIntent(documentId)
            )
            .build()
            
        NotificationManagerCompat.from(context)
            .notify(documentId.hashCode(), notification)
    }
}
```

## Testing Strategy

### Unit Tests
- Test parallel processing with different batch sizes
- Test chunking algorithm with various document sizes
- Test pause/resume logic
- Test streaming results

### Integration Tests
- Test WorkManager with network constraints
- Test notification updates
- Test database partial saves

### Performance Tests
- Measure processing time for 30, 50, 100 page documents
- Memory usage monitoring
- Battery impact assessment

## Migration Plan

1. **Release 1**: Parallel processing only (Phase 1)
2. **Release 2**: Add WorkManager (Phase 2)
3. **Release 3**: Full feature set (Phases 3-6)

## Success Metrics

- Process 50-page document in <3 minutes
- Process 100-page document in <6 minutes
- <5% failure rate
- Smooth UI with no ANRs
- Battery usage <10% for 100-page document

## Risk Mitigation

1. **API Rate Limits**: Implement exponential backoff
2. **Memory Issues**: Process in smaller batches
3. **Network Failures**: Save progress locally
4. **User Confusion**: Clear progress indicators and help text