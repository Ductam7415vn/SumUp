package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.model.PdfExtractionResult
import com.example.sumup.domain.repository.PdfRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtractPdfTextUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(pdfDocument: PdfDocument): Result<PdfExtractionResult> = try {
        // Validation
        when {
            pdfDocument.sizeBytes > MAX_FILE_SIZE_BYTES -> {
                Result.failure(Exception("File too large. Maximum size is ${MAX_FILE_SIZE_MB}MB"))
            }
            pdfDocument.isPasswordProtected -> {
                Result.failure(Exception("Password-protected PDFs are not supported"))
            }
            else -> {
                val startTime = System.currentTimeMillis()
                val result = pdfRepository.extractTextFromPdf(pdfDocument)
                val extractionTime = System.currentTimeMillis() - startTime
                
                val enhancedResult = result.copy(extractionTimeMs = extractionTime)
                
                if (enhancedResult.isTextExtractable) {
                    Result.success(enhancedResult)
                } else {
                    Result.failure(Exception("Could not extract readable text from PDF. Try a text-based PDF instead of scanned images."))
                }
            }
        }
    } catch (exception: Exception) {
        Result.failure(Exception("Failed to process PDF: ${exception.message}", exception))
    }
    
    companion object {
        private const val MAX_FILE_SIZE_MB = 10
        private const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024L
    }
}