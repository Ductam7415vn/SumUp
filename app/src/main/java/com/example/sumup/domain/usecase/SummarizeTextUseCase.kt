package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import com.example.sumup.utils.InputValidator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummarizeTextUseCase @Inject constructor(
    private val repository: SummaryRepository,
    private val inputValidator: InputValidator
) {
    suspend operator fun invoke(
        text: String,
        persona: SummaryPersona = SummaryPersona.GENERAL,
        lengthMultiplier: Float = 1.0f
    ): Result<Summary> = try {
        android.util.Log.d("SummarizeTextUseCase", "=== USE CASE STARTED ===")
        android.util.Log.d("SummarizeTextUseCase", "Text length: ${text.length}")
        android.util.Log.d("SummarizeTextUseCase", "Persona: $persona")
        android.util.Log.d("SummarizeTextUseCase", "Length multiplier: $lengthMultiplier")
        
        // Validate and sanitize input
        android.util.Log.d("SummarizeTextUseCase", "Validating input...")
        when (val validationResult = inputValidator.validateTextInput(text)) {
            is InputValidator.ValidationResult.Success -> {
                android.util.Log.d("SummarizeTextUseCase", "Validation SUCCESS")
                android.util.Log.d("SummarizeTextUseCase", "Text length: ${text.length}")
                android.util.Log.d("SummarizeTextUseCase", "Calling repository.summarizeText...")
                
                val summary = repository.summarizeText(
                    text, 
                    persona,
                    lengthMultiplier
                )
                android.util.Log.d("SummarizeTextUseCase", "Repository returned summary with ID: ${summary.id}")
                Result.success(summary)
            }
            is InputValidator.ValidationResult.Warning -> {
                android.util.Log.d("SummarizeTextUseCase", "Validation WARNING: ${validationResult.message}")
                // Process with warning
                val summary = repository.summarizeText(
                    text,
                    persona,
                    lengthMultiplier
                )
                Result.success(summary)
            }
            is InputValidator.ValidationResult.Error -> {
                android.util.Log.e("SummarizeTextUseCase", "Validation FAILED: ${validationResult.message}")
                Result.failure(Exception(validationResult.message))
            }
        }
    } catch (exception: Exception) {
        android.util.Log.e("SummarizeTextUseCase", "=== USE CASE ERROR ===", exception)
        Result.failure(exception)
    }
}