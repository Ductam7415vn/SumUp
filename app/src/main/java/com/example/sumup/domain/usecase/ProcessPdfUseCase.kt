package com.example.sumup.domain.usecase

import android.net.Uri
import com.example.sumup.domain.model.FileUploadError
import com.example.sumup.domain.model.FileUploadState
import com.example.sumup.domain.model.ProcessingStage
import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessPdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    operator fun invoke(fileUri: Uri): Flow<FileUploadState> = flow {
        try {
            emit(FileUploadState.Processing(ProcessingStage.READING_FILE, 0.1f))
            
            val pdfDocument = pdfRepository.validatePdfFile(fileUri.toString())
            
            emit(FileUploadState.Processing(ProcessingStage.EXTRACTING_TEXT, 0.3f))
            
            val extractionResult = pdfRepository.extractTextFromPdf(pdfDocument)
            val extractedText = extractionResult.extractedText

            android.util.Log.d("ProcessPdfUseCase", "=== PDF TEXT EXTRACTION ===")
            android.util.Log.d("ProcessPdfUseCase", "Extracted text length: ${extractedText.length}")
            android.util.Log.d("ProcessPdfUseCase", "First 200 chars: ${extractedText.take(200)}")
            android.util.Log.d("ProcessPdfUseCase", "Is blank: ${extractedText.isBlank()}")

            emit(FileUploadState.Processing(ProcessingStage.CLEANING_TEXT, 0.7f))
            val cleanedText = cleanExtractedText(extractedText)

            android.util.Log.d("ProcessPdfUseCase", "=== AFTER CLEANING ===")
            android.util.Log.d("ProcessPdfUseCase", "Cleaned text length: ${cleanedText.length}")
            android.util.Log.d("ProcessPdfUseCase", "First 200 chars: ${cleanedText.take(200)}")

            when {
                cleanedText.isBlank() -> {
                    android.util.Log.e("ProcessPdfUseCase", "Text is blank after cleaning")
                    emit(FileUploadState.Error(FileUploadError.NoTextFound))
                }
                cleanedText.length < 20 -> {
                    android.util.Log.e("ProcessPdfUseCase", "Text too short: ${cleanedText.length} chars")
                    emit(FileUploadState.Error(FileUploadError.NoTextFound))
                }
                cleanedText.length > 10000 -> {
                    val truncated = cleanedText.take(10000) + "\n\n[Text truncated]"
                    emit(FileUploadState.Processing(ProcessingStage.PREPARING_SUMMARY, 0.95f))
                    emit(FileUploadState.Success(truncated))
                }
                else -> {
                    emit(FileUploadState.Processing(ProcessingStage.PREPARING_SUMMARY, 0.95f))
                    emit(FileUploadState.Success(cleanedText))
                }
            }
        } catch (exception: Exception) {
            emit(FileUploadState.Error(FileUploadError.ProcessingFailed(exception.message ?: "Unknown error")))
        }
    }
    
    private fun cleanExtractedText(text: String): String {
        if (text.isBlank()) return ""

        return text
            // Remove null characters and other problematic control chars
            .replace(Regex("[\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u000B\u000C\u000E-\u001F\u007F-\u009F]"), "")
            // Normalize line breaks (keep \n and \r)
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            // Collapse multiple spaces to single space (but keep newlines)
            .replace(Regex("[ \\t]+"), " ")
            // Remove excessive blank lines (more than 2 consecutive newlines)
            .replace(Regex("\n{3,}"), "\n\n")
            // Trim each line
            .lines()
            .joinToString("\n") { it.trim() }
            // Final trim
            .trim()
    }
}