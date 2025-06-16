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
        persona: SummaryPersona = SummaryPersona.GENERAL
    ): Result<Summary> = try {
        // Validate and sanitize input
        when (val validationResult = inputValidator.validateTextInput(text)) {
            is InputValidator.ValidationResult.Success -> {
                val summary = repository.summarizeText(validationResult.sanitizedValue, persona)
                Result.success(summary)
            }
            is InputValidator.ValidationResult.Error -> {
                Result.failure(Exception(validationResult.message))
            }
        }
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}