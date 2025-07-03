package com.example.sumup.data.repository

import android.content.Context
import com.example.sumup.data.local.dao.SummaryDao
import com.example.sumup.data.mapper.SummaryMapper
import com.example.sumup.data.remote.api.GeminiApiService
import com.example.sumup.data.remote.dto.SummarizeRequest
import com.example.sumup.domain.model.Summary
import com.example.sumup.domain.model.SummaryMetrics
import com.example.sumup.domain.model.SummaryPersona
import com.example.sumup.domain.repository.SummaryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class SummaryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
    
    override fun getTodayCount(): Flow<Int> =
        localDataSource.getTodayCount()
    
    override fun getWeekCount(): Flow<Int> =
        localDataSource.getWeekCount()
    
    override fun getTotalCount(): Flow<Int> =
        localDataSource.getTotalCount()
        
    override suspend fun summarizeText(
        text: String,
        persona: SummaryPersona,
        lengthMultiplier: Float
    ): Summary {
        android.util.Log.d("SummaryRepository", "=== SUMMARIZE TEXT CALLED ===")
        android.util.Log.d("SummaryRepository", "Text length: ${text.length}")
        android.util.Log.d("SummaryRepository", "Persona: ${persona.name} (${persona.apiStyle})")
        android.util.Log.d("SummaryRepository", "Length multiplier: $lengthMultiplier")
        
        try {
            // Calculate target length as percentage of original text
            // lengthMultiplier: 0.05 (5%), 0.10 (10%), or 0.20 (20%)
            val wordCount = text.split("\\s+".toRegex()).size
            val targetWordCount = (wordCount * lengthMultiplier).toInt()
            
            // Convert word count to approximate character count (avg 5 chars per word)
            val targetLength = targetWordCount * 5
            
            android.util.Log.d("SummaryRepository", "Original word count: $wordCount")
            android.util.Log.d("SummaryRepository", "Target word count: $targetWordCount (${(lengthMultiplier * 100).toInt()}%)")
            android.util.Log.d("SummaryRepository", "Target character length: $targetLength")
            
            val request = SummarizeRequest(
                text = text,
                style = persona.apiStyle,
                maxLength = targetLength
            )
            
            android.util.Log.d("SummaryRepository", "Created request with:")
            android.util.Log.d("SummaryRepository", "  - Style: ${request.style}")
            android.util.Log.d("SummaryRepository", "  - Max length: ${request.maxLength}")
            android.util.Log.d("SummaryRepository", "Calling remoteDataSource.summarizeText...")

            val response = remoteDataSource.summarizeText(request)
            
            android.util.Log.d("SummaryRepository", "Got response from API:")
            android.util.Log.d("SummaryRepository", "  - Summary length: ${response.summary.length}")
            android.util.Log.d("SummaryRepository", "  - Bullets count: ${response.bullets.size}")
            android.util.Log.d("SummaryRepository", "  - Confidence: ${response.confidence}")
            android.util.Log.d("SummaryRepository", "  - Processing time: ${response.processingTime}ms")
            
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
                confidence = response.confidence,
                // Multi-tier content
                briefOverview = response.briefOverview,
                detailedSummary = response.detailedSummary,
                keyInsights = response.keyInsights,
                actionItems = response.actionItems,
                keywords = response.keywords
            )
            
            android.util.Log.d("SummaryRepository", "Created summary with ID: ${summary.id}")
            android.util.Log.d("SummaryRepository", "Summary has ${response.bullets.size} bullet points")
            android.util.Log.d("SummaryRepository", "Bullets: ${response.bullets}")

            // Save to local database
            saveSummary(summary)
            android.util.Log.d("SummaryRepository", "Summary saved to database with ID: ${summary.id}")
            
            return summary

        } catch (exception: Exception) {
            android.util.Log.e("SummaryRepository", "=== SUMMARIZE TEXT FAILED ===")
            android.util.Log.e("SummaryRepository", "Error type: ${exception.javaClass.simpleName}")
            android.util.Log.e("SummaryRepository", "Error message: ${exception.message}")
            android.util.Log.e("SummaryRepository", "Stack trace:", exception)
            throw Exception("Failed to summarize text: ${exception.message}", exception)
        }
    }
    
    override suspend fun generateSummary(request: com.example.sumup.domain.model.SummaryRequest): Summary {
        return summarizeText(request.text ?: "", request.persona)
    }
    
    override suspend fun getDatabaseSize(): String {
        return try {
            val dbFile = context.getDatabasePath("sumup_database")
            if (dbFile.exists()) {
                val sizeInBytes = dbFile.length()
                formatFileSize(sizeInBytes)
            } else {
                "0 MB"
            }
        } catch (e: Exception) {
            android.util.Log.e("SummaryRepository", "Error calculating database size", e)
            "0 MB"
        }
    }
    
    private fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "%.1f KB".format(sizeInBytes / 1024.0)
            sizeInBytes < 1024 * 1024 * 1024 -> "%.1f MB".format(sizeInBytes / (1024.0 * 1024))
            else -> "%.1f GB".format(sizeInBytes / (1024.0 * 1024 * 1024))
        }
    }
}