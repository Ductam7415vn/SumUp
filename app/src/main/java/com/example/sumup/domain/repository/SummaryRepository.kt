package com.example.sumup.domain.repository

import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    fun getAllSummaries(): Flow<List<Summary>>
    suspend fun getSummaryById(id: String): Summary?
    suspend fun saveSummary(summary: Summary)
    suspend fun updateSummary(summary: Summary)
    suspend fun deleteSummary(id: String)
    suspend fun deleteAllSummaries()
    suspend fun getSummaryCount(): Int
    suspend fun summarizeText(
        text: String, 
        persona: SummaryPersona = SummaryPersona.GENERAL
    ): Summary
}