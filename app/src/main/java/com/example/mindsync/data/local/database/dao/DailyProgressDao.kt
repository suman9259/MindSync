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
    
    // Aggregation queries for reports
    @Query("""
        SELECT 
            SUM(medicinesTaken) as medicinesTaken,
            SUM(medicinesTotal) as medicinesTotal,
            SUM(skincareRoutinesCompleted) as skincareCompleted,
            SUM(skincareRoutinesTotal) as skincareTotal,
            SUM(meditationSessionsCompleted) as meditationSessions,
            SUM(meditationMinutes) as meditationMinutes,
            SUM(workoutsCompleted) as workoutsCompleted,
            SUM(workoutMinutes) as workoutMinutes,
            SUM(caloriesBurned) as caloriesBurned,
            SUM(totalTasksCompleted) as totalCompleted,
            SUM(totalTasks) as totalTasks,
            COUNT(*) as daysTracked
        FROM daily_progress 
        WHERE userId = :userId AND date >= :startDate AND date <= :endDate
    """)
    suspend fun getAggregatedProgress(userId: String, startDate: Long, endDate: Long): AggregatedProgress?
}

data class AggregatedProgress(
    val medicinesTaken: Int,
    val medicinesTotal: Int,
    val skincareCompleted: Int,
    val skincareTotal: Int,
    val meditationSessions: Int,
    val meditationMinutes: Int,
    val workoutsCompleted: Int,
    val workoutMinutes: Int,
    val caloriesBurned: Int,
    val totalCompleted: Int,
    val totalTasks: Int,
    val daysTracked: Int
)
