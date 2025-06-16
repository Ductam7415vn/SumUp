package com.example.sumup.data.local.dao

import androidx.room.*
import com.example.sumup.data.local.entity.SummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries ORDER BY createdAt DESC LIMIT 500")
    fun getAllSummaries(): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getSummariesPaged(limit: Int, offset: Int): List<SummaryEntity>

    @Query("SELECT * FROM summaries WHERE id = :id")
    suspend fun getSummaryById(id: String): SummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity)

    @Delete
    suspend fun deleteSummary(summary: SummaryEntity)

    @Query("DELETE FROM summaries WHERE id = :id")
    suspend fun deleteSummaryById(id: String)

    @Query("DELETE FROM summaries")
    suspend fun deleteAllSummaries()

    @Query("SELECT COUNT(*) FROM summaries")
    suspend fun getSummaryCount(): Int

    @Query("UPDATE summaries SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
}