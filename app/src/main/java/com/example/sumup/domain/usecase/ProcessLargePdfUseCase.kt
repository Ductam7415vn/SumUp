package com.example.sumup.domain.usecase

import android.net.Uri
import com.example.sumup.domain.model.*
import com.example.sumup.domain.repository.PdfRepository
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

data class PdfChunk(
    val startPage: Int,
    val endPage: Int,
    val text: String,
    val chunkIndex: Int,
    val totalChunks: Int
)

data class LargePdfProcessingState(
    val stage: ProcessingStage,
    val progress: Float,
    val currentChunk: Int = 0,
    val totalChunks: Int = 0,
    val processedPages: Int = 0,
    val totalPages: Int = 0
)

@Singleton
class ProcessLargePdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository,
    private val summaryRepository: SummaryRepository,
    private val summarizeTextUseCase: SummarizeTextUseCase
) {
    companion object {
        const val CHUNK_SIZE = 20 // Pages per chunk
        const val MAX_TEXT_PER_CHUNK = 50000 // Characters
        const val WARNING_PAGE_THRESHOLD = 50 // Show warning for PDFs with more pages
    }
    
    operator fun invoke(fileUri: Uri): Flow<FileUploadState> = flow {
        try {
            emit(FileUploadState.Processing(ProcessingStage.READING_FILE, 0.1f))
            
            // Get PDF metadata first
            val pdfDocument = pdfRepository.getPdfMetadata(fileUri.toString())
            val totalPages = pdfDocument.pageCount ?: 0
            
            // Check if this is a large PDF
            if (totalPages > WARNING_PAGE_THRESHOLD) {
                emit(FileUploadState.LargePdfDetected(
                    pageCount = totalPages,
                    estimatedProcessingTime = estimateProcessingTime(totalPages)
                ))
            }
            
            // Process in chunks
            val chunks = processInChunks(fileUri, totalPages)
            val combinedText = chunks.joinToString("\n\n") { it.text }
            
            when {
                combinedText.isBlank() -> {
                    emit(FileUploadState.Error(FileUploadError.NoTextFound))
                }
                combinedText.length < 50 -> {
                    emit(FileUploadState.Error(FileUploadError.NoTextFound))
                }
                else -> {
                    // For very large texts, use hierarchical summarization
                    if (chunks.size > 3) {
                        emit(FileUploadState.Processing(ProcessingStage.PREPARING_SUMMARY, 0.95f))
                        val summarizedText = performHierarchicalSummarization(chunks)
                        emit(FileUploadState.Success(summarizedText))
                    } else {
                        emit(FileUploadState.Success(combinedText.take(50000)))
                    }
                }
            }
        } catch (exception: Exception) {
            emit(FileUploadState.Error(
                FileUploadError.ProcessingFailed(exception.message ?: "Unknown error")
            ))
        }
    }
    
    private suspend fun processInChunks(
        fileUri: Uri,
        totalPages: Int
    ): List<PdfChunk> {
        val chunks = mutableListOf<PdfChunk>()
        val totalChunks = (totalPages + CHUNK_SIZE - 1) / CHUNK_SIZE
        
        for (chunkIndex in 0 until totalChunks) {
            val startPage = chunkIndex * CHUNK_SIZE + 1
            val endPage = minOf((chunkIndex + 1) * CHUNK_SIZE, totalPages)
            
            // Extract text for this chunk
            val chunkResult = pdfRepository.extractTextFromPdfRange(
                fileUri.toString(),
                startPage,
                endPage
            )
            
            if (chunkResult.success && chunkResult.extractedText.isNotBlank()) {
                val chunk = PdfChunk(
                    startPage = startPage,
                    endPage = endPage,
                    text = cleanExtractedText(chunkResult.extractedText),
                    chunkIndex = chunkIndex,
                    totalChunks = totalChunks
                )
                chunks.add(chunk)
            }
            
            // Check if we have enough text
            val totalTextLength = chunks.sumOf { it.text.length }
            if (totalTextLength > MAX_TEXT_PER_CHUNK * 3) {
                break // Stop processing if we have enough text
            }
        }
        
        return chunks
    }
    
    private suspend fun performHierarchicalSummarization(
        chunks: List<PdfChunk>
    ): String {
        // First level: Summarize each chunk
        val chunkSummaries = chunks.map { chunk ->
            val summaryResult = summarizeTextUseCase(
                text = chunk.text
            )
            val summaryText = summaryResult.getOrNull()?.summaryText ?: "Unable to summarize chunk"
            "Pages ${chunk.startPage}-${chunk.endPage}:\n$summaryText"
        }
        
        // Second level: Combine and create master summary
        val combinedSummaries = chunkSummaries.joinToString("\n\n")
        val masterSummaryResult = summarizeTextUseCase(
            text = "Create a comprehensive summary from these section summaries:\n$combinedSummaries"
        )
        
        return masterSummaryResult.getOrNull()?.summaryText ?: "Unable to create master summary"
    }
    
    private fun cleanExtractedText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[\u0000-\u001f\u007f-\u009f]"), "")
            .trim()
    }
    
    private fun estimateProcessingTime(pageCount: Int): Long {
        // Estimate ~2 seconds per page for processing
        return (pageCount * 2L).coerceAtMost(300) // Max 5 minutes
    }
}

