package com.example.sumup.data.repository

import com.example.sumup.data.local.dao.SummaryDao
import com.example.sumup.data.mapper.SummaryMapper
import com.example.sumup.data.remote.api.GeminiApiService
import com.example.sumup.data.remote.dto.SummarizeRequest
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryMetrics
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val localDataSource: SummaryDao,
    private val remoteDataSource: GeminiApiService,
    private val mapper: SummaryMapper
) : SummaryRepository {

    override fun getAllSummaries(): Flow<List<Summary>> =
        localDataSource.getAllSummaries().map { entities ->
            entities.map(mapper::entityToDomain)
        }

    override suspend fun getSummaryById(id: String): Summary? =
        localDataSource.getSummaryById(id)?.let(mapper::entityToDomain)

    override suspend fun saveSummary(summary: Summary) {
        localDataSource.insertSummary(mapper.domainToEntity(summary))
    }
    
    override suspend fun updateSummary(summary: Summary) {
        localDataSource.insertSummary(mapper.domainToEntity(summary))
    }

    override suspend fun deleteSummary(id: String) {
        localDataSource.deleteSummaryById(id)
    }

    override suspend fun deleteAllSummaries() {
        localDataSource.deleteAllSummaries()
    }

    override suspend fun getSummaryCount(): Int =
        localDataSource.getSummaryCount()
    override suspend fun summarizeText(
        text: String,
        persona: SummaryPersona
    ): Summary {
        try {
            val request = SummarizeRequest(
                text = text,
                style = persona.apiStyle,
                maxLength = 150 // Default max length
            )

            val response = remoteDataSource.summarizeText(request)
            
            val originalWordCount = text.split("\\s+".toRegex()).size
            val summaryWordCount = response.summary.split("\\s+".toRegex()).size
            val originalReadingTime = ceil(originalWordCount / 200.0).toInt() // 200 WPM
            val summaryReadingTime = ceil(summaryWordCount / 200.0).toInt()
            val reductionPercent = ((originalWordCount - summaryWordCount) * 100 / originalWordCount)

            val metrics = SummaryMetrics(
                originalWordCount = originalWordCount,
                summaryWordCount = summaryWordCount,
                reductionPercentage = reductionPercent,
                originalReadingTime = originalReadingTime,
                summaryReadingTime = summaryReadingTime
            )

            val summary = Summary(
                id = java.util.UUID.randomUUID().toString(),
                originalText = text,
                summary = response.summary,
                bulletPoints = response.bullets,
                persona = persona,
                createdAt = System.currentTimeMillis(),
                isFavorite = false,
                metrics = metrics,
                confidence = response.confidence
            )
            
            android.util.Log.d("SummaryRepository", "Created summary with ${response.bullets.size} bullet points")
            android.util.Log.d("SummaryRepository", "Bullets: ${response.bullets}")

            // Save to local database
            saveSummary(summary)
            return summary

        } catch (exception: Exception) {
            throw Exception("Failed to summarize text: ${exception.message}", exception)
        }
    }
    
    override suspend fun generateSummary(request: com.example.sumup.domain.model.SummaryRequest): Summary {
        return summarizeText(request.text ?: "", request.persona)
    }
}