# Large PDF Handling Solution for SumUp

## Current Limitations

The app currently has these PDF processing limitations:
- **Page limit**: 100 pages maximum (hardcoded in `PdfRepositoryImpl.kt:91`)
- **File size**: 50MB maximum (line 45)
- **Text truncation**: 10,000 characters (in `ProcessPdfUseCase.kt:35`)
- **Memory constraints**: May crash with OutOfMemoryError on complex PDFs

## Solutions for 100+ Page PDFs

### 1. Intelligent Chunking Strategy
Split large PDFs into manageable chunks:
```kotlin
data class PdfChunk(
    val startPage: Int,
    val endPage: Int,
    val text: String,
    val chunkIndex: Int,
    val totalChunks: Int
)
```

### 2. Progressive Processing
- Process PDF in 20-page chunks
- Show progress to user
- Allow partial summarization

### 3. Smart Text Selection
- Extract table of contents if available
- Let users select specific pages/chapters
- Implement page range picker

### 4. Streaming Processing
- Use streaming API to process pages without loading entire document
- Process pages one by one, accumulating text

### 5. Multi-tier Summarization
For very large documents:
1. Summarize each chunk (20 pages)
2. Combine chunk summaries
3. Generate final master summary

## Implementation Plan

### Phase 1: Remove Hard Limits
- Make page limit configurable
- Add warning dialogs for large files
- Implement memory-aware processing

### Phase 2: Chunking System
```kotlin
class PdfChunkProcessor {
    fun processLargePdf(
        document: PDDocument,
        chunkSize: Int = 20
    ): Flow<PdfChunk> = flow {
        val totalPages = document.numberOfPages
        val totalChunks = ceil(totalPages / chunkSize.toFloat()).toInt()
        
        for (chunkIndex in 0 until totalChunks) {
            val startPage = chunkIndex * chunkSize + 1
            val endPage = min((chunkIndex + 1) * chunkSize, totalPages)
            
            val stripper = PDFTextStripper().apply {
                this.startPage = startPage
                this.endPage = endPage
            }
            
            emit(PdfChunk(
                startPage = startPage,
                endPage = endPage,
                text = stripper.getText(document),
                chunkIndex = chunkIndex,
                totalChunks = totalChunks
            ))
        }
    }
}
```

### Phase 3: UI Updates
- Add page selection dialog
- Show chunk processing progress
- Allow users to preview extracted text before summarization

### Phase 4: Advanced Features
- OCR support for scanned PDFs
- Smart content detection (skip blank pages)
- Table/image extraction and description

## API Considerations

### Token Limits
Gemini API has token limits:
- Input: 30,720 tokens (~24,000 words)
- Output: 2,048 tokens

### Solution: Hierarchical Summarization
```kotlin
suspend fun summarizeLargePdf(chunks: List<PdfChunk>): String {
    // Step 1: Summarize each chunk
    val chunkSummaries = chunks.map { chunk ->
        geminiApi.summarize(chunk.text, maxTokens = 500)
    }
    
    // Step 2: Combine and summarize again
    val combined = chunkSummaries.joinToString("\n\n")
    return geminiApi.summarize(
        "Create a comprehensive summary from these section summaries:\n$combined",
        maxTokens = 2000
    )
}
```

## Memory Management

### Strategies:
1. **Lazy Loading**: Only load pages being processed
2. **Garbage Collection**: Explicitly close resources after each chunk
3. **File Streaming**: Use InputStreams instead of loading entire file
4. **Cache Management**: Clear caches between chunks

### Example Implementation:
```kotlin
suspend fun processLargePdfSafely(uri: Uri): PdfExtractionResult {
    var totalText = StringBuilder()
    var processedPages = 0
    
    return withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val document = PDDocument.load(stream)
            val totalPages = document.numberOfPages
            
            try {
                for (page in 1..totalPages step 20) {
                    val endPage = min(page + 19, totalPages)
                    
                    // Process chunk
                    val stripper = PDFTextStripper().apply {
                        startPage = page
                        this.endPage = endPage
                    }
                    
                    val chunkText = stripper.getText(document)
                    totalText.append(chunkText)
                    
                    processedPages = endPage
                    
                    // Memory management
                    if (totalText.length > 50000) {
                        // Start summarizing early if text gets too large
                        break
                    }
                    
                    // Allow garbage collection
                    System.gc()
                }
            } finally {
                document.close()
            }
            
            PdfExtractionResult(
                extractedText = totalText.toString(),
                pageCount = processedPages,
                success = true
            )
        }
    }
}
```

## User Experience Improvements

### 1. Page Range Selection
```kotlin
@Composable
fun PdfPageRangeDialog(
    totalPages: Int,
    onRangeSelected: (IntRange) -> Unit
) {
    // UI for selecting specific pages
    // Options: All, First N pages, Last N pages, Custom range
}
```

### 2. Progress Indication
```kotlin
data class PdfProcessingProgress(
    val currentPage: Int,
    val totalPages: Int,
    val extractedTextSize: Int,
    val estimatedTimeRemaining: Duration?
)
```

### 3. Preview and Confirm
- Show extracted text preview
- Display estimated summary cost (tokens)
- Let users refine selection before processing

## Recommended Approach

For immediate implementation:

1. **Increase page limit** to 200 with warning
2. **Implement chunking** for 50+ page PDFs
3. **Add page selection UI** for user control
4. **Use streaming processing** to handle memory
5. **Show clear progress** during extraction

This approach balances performance, user experience, and API constraints while handling large PDFs effectively.