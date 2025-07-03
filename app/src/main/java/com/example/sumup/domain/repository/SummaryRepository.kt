package com.example.sumup.domain.repository

import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.model.SummaryRequest
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {
    fun getAllSummaries(): Flow<List<Summary>>
    suspend fun getSummaryById(id: String): Summary?
    suspend fun saveSummary(summary: Summary)
    suspend fun updateSummary(summary: Summary)
    suspend fun deleteSummary(id: String)
    suspend fun deleteAllSummaries()
    suspend fun getSummaryCount(): Int
    fun getTodayCount(): Flow<Int>
    fun getWeekCount(): Flow<Int>
    fun getTotalCount(): Flow<Int>
    suspend fun summarizeText(
        text: String, 
        persona: SummaryPersona = SummaryPersona.GENERAL,
        lengthMultiplier: Float = 1.0f
    ): Summary
    suspend fun generateSummary(request: SummaryRequest): Summary
    suspend fun getDatabaseSize(): String
}