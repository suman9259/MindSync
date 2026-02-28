package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.DailyProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyProgressDao {

    @Query("SELECT * FROM daily_progress WHERE userId = :userId ORDER BY date DESC")
    fun getProgressByUser(userId: String): Flow<List<DailyProgressEntity>>

    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND dateString = :dateString")
    suspend fun getProgressForDate(userId: String, dateString: String): DailyProgressEntity?

    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND dateString = :dateString")
    fun getProgressForDateFlow(userId: String, dateString: String): Flow<DailyProgressEntity?>

    @Query("SELECT * FROM daily_progress WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentProgress(userId: String, limit: Int): Flow<List<DailyProgressEntity>>

    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getProgressInRange(userId: String, startDate: Long, endDate: Long): Flow<List<DailyProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: DailyProgressEntity)

    @Update
    suspend fun updateProgress(progress: DailyProgressEntity)

    @Query("DELETE FROM daily_progress WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM daily_progress WHERE id = :id")
    suspend fun getProgressById(id: String): DailyProgressEntity?
}
