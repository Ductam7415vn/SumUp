package com.example.sumup.domain.usecase

import com.example.sumup.domain.repository.SummaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteSummaryUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    suspend operator fun invoke(summaryId: String): Result<Unit> = try {
        repository.deleteSummary(summaryId)
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}

@Singleton 
class DeleteAllSummariesUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    suspend operator fun invoke(): Result<Unit> = try {
        repository.deleteAllSummaries()
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}