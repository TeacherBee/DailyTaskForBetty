package com.example.dailytaskforbetty.data

import androidx.room.*
import com.example.dailytaskforbetty.model.RetroactiveHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RetroactiveHistoryDao {
    @Insert
    suspend fun insertRetroactiveHistory(history: RetroactiveHistoryEntity)

    @Query("SELECT * FROM retroactive_history WHERE taskId = :taskId AND year = :year AND month = :month")
    suspend fun getRetroactiveCountForTask(taskId: String, year: Int, month: Int): List<RetroactiveHistoryEntity>

    @Query("SELECT COUNT(*) FROM retroactive_history WHERE taskId = :taskId AND year = :year AND month = :month")
    suspend fun getRetroactiveCount(taskId: String, year: Int, month: Int): Int

    @Query("SELECT * FROM retroactive_history ORDER BY retroactiveDate DESC")
    fun observeAllRetroactiveHistories(): Flow<List<RetroactiveHistoryEntity>>

    @Delete
    suspend fun deleteRetroactiveHistory(history: RetroactiveHistoryEntity)
}
