package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummarizeTextUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    suspend operator fun invoke(
        text: String,
        persona: SummaryPersona = SummaryPersona.GENERAL
    ): Result<Summary> = try {
        val trimmedText = text.trim()
        
        // Validation
        when {
            trimmedText.length < 50 -> {
                Result.failure(Exception("Text too short. Need at least 50 characters."))
            }
            trimmedText.length > 5000 -> {
                Result.failure(Exception("Text too long. Maximum 5000 characters."))
            }
            else -> {
                val summary = repository.summarizeText(trimmedText, persona)
                Result.success(summary)
            }
        }
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}