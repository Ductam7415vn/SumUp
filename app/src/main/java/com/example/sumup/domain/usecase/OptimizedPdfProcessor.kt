package com.example.sumup.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.sumup.domain.model.PdfExtractionResult
import com.example.sumup.domain.repository.PdfRepository
import com.tom_roush.pdfbox.pdmodel.PDDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Memory-optimized PDF processor that handles large PDFs efficiently
 * by processing them in chunks and releasing memory aggressively
 */
@Singleton
class OptimizedPdfProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfRepository: PdfRepository
) {
    
    companion object {
        private const val CHUNK_SIZE = 10 // Process 10 pages at a time
        private const val MAX_TEXT_LENGTH = 50_000 // Max chars per chunk
        private const val MEMORY_THRESHOLD = 0.8f // 80% memory usage threshold
    }
    
    /**
     * Process PDF with memory optimization
     */
    fun processPdfOptimized(uri: Uri): Flow<PdfProcessingState> = flow {
        emit(PdfProcessingState.Starting)
        
        try {
            // First, get PDF metadata
            val pdfDocument = pdfRepository.validatePdfFile(uri.toString())
            emit(PdfProcessingState.Validated(pdfDocument))
            
            // Check available memory before processing
            checkMemoryAvailability()
            
            // Get total page count
            val metadata = pdfRepository.getPdfMetadata(uri.toString())
            val totalPages = metadata.pageCount ?: 0
            
            if (totalPages == 0) {
                emit(PdfProcessingState.Error("PDF has no pages"))
                return@flow
            }
            
            emit(PdfProcessingState.Processing(0, totalPages))
            
            // Process in chunks
            val chunks = mutableListOf<String>()
            var currentPage = 1
            
            while (currentPage <= totalPages) {
                // Check memory before each chunk
                if (isMemoryLow()) {
                    // Force garbage collection
                    System.gc()
                    kotlinx.coroutines.delay(100)
                    
                    if (isMemoryLow()) {
                        emit(PdfProcessingState.Warning("Low memory - processing may be slow"))
                    }
                }
                
                val endPage = minOf(currentPage + CHUNK_SIZE - 1, totalPages)
                
                // Extract chunk
                val chunkResult = pdfRepository.extractTextFromPdfRange(
                    uri = uri.toString(),
                    startPage = currentPage,
                    endPage = endPage
                )
                
                if (chunkResult.success && chunkResult.extractedText.isNotEmpty()) {
                    chunks.add(chunkResult.extractedText)
                    
                    // Emit progress
                    emit(PdfProcessingState.Processing(endPage, totalPages))
                    
                    // If we've collected enough text, stop early to save memory
                    val totalLength = chunks.sumOf { it.length }
                    if (totalLength >= MAX_TEXT_LENGTH) {
                        emit(PdfProcessingState.Warning("Text limit reached, processing stopped at page $endPage"))
                        break
                    }
                }
                
                currentPage = endPage + 1
                
                // Small delay to prevent UI freezing
                kotlinx.coroutines.delay(10)
            }
            
            // Combine chunks
            val fullText = chunks.joinToString("\n\n")
            
            if (fullText.isBlank()) {
                emit(PdfProcessingState.Error("No text found in PDF"))
            } else {
                emit(PdfProcessingState.Success(fullText, totalPages))
            }
            
        } catch (e: OutOfMemoryError) {
            emit(PdfProcessingState.Error("Out of memory - PDF too large"))
            // Clear any cached data
            clearCache()
        } catch (e: Exception) {
            emit(PdfProcessingState.Error(e.message ?: "Failed to process PDF"))
        }
    }
    
    private fun checkMemoryAvailability() {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val usagePercent = usedMemory.toFloat() / maxMemory
        
        if (usagePercent > MEMORY_THRESHOLD) {
            throw OutOfMemoryError("Insufficient memory to process PDF")
        }
    }
    
    private fun isMemoryLow(): Boolean {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        return (usedMemory.toFloat() / maxMemory) > MEMORY_THRESHOLD
    }
    
    private suspend fun clearCache() = withContext(Dispatchers.IO) {
        try {
            // Clear app cache directory
            context.cacheDir.deleteRecursively()
            context.cacheDir.mkdirs()
            
            // Force garbage collection
            System.gc()
        } catch (e: Exception) {
            android.util.Log.e("OptimizedPdfProcessor", "Failed to clear cache", e)
        }
    }
    
    sealed class PdfProcessingState {
        object Starting : PdfProcessingState()
        data class Validated(val document: com.example.sumup.domain.model.PdfDocument) : PdfProcessingState()
        data class Processing(val currentPage: Int, val totalPages: Int) : PdfProcessingState()
        data class Warning(val message: String) : PdfProcessingState()
        data class Success(val extractedText: String, val totalPages: Int) : PdfProcessingState()
        data class Error(val message: String) : PdfProcessingState()
    }
}