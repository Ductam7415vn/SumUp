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
            
            emit(FileUploadState.Processing(ProcessingStage.CLEANING_TEXT, 0.7f))
            val cleanedText = cleanExtractedText(extractedText)
            
            when {
                cleanedText.isBlank() -> emit(FileUploadState.Error(FileUploadError.NoTextFound))
                cleanedText.length < 50 -> emit(FileUploadState.Error(FileUploadError.NoTextFound))
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
        return text
            .replace(Regex("\\s+"), " ") // Replace multiple whitespace with single space
            .replace(Regex("[\u0000-\u001f\u007f-\u009f]"), "") // Remove control characters
            .trim()
    }
}