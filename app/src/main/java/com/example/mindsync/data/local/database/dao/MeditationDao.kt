package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.MeditationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationDao {

    @Query("SELECT * FROM meditations WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getMeditationsByUser(userId: String): Flow<List<MeditationEntity>>

    @Query("SELECT * FROM meditations WHERE id = :id")
    suspend fun getMeditationById(id: String): MeditationEntity?

    @Query("SELECT * FROM meditations WHERE id = :id")
    fun getMeditationByIdFlow(id: String): Flow<MeditationEntity?>

    @Query("SELECT * FROM meditations WHERE userId = :userId AND category = :category ORDER BY updatedAt DESC")
    fun getMeditationsByCategory(userId: String, category: String): Flow<List<MeditationEntity>>

    @Query("SELECT * FROM meditations WHERE isSynced = 0")
    suspend fun getUnsyncedMeditations(): List<MeditationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditation(meditation: MeditationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditations(meditations: List<MeditationEntity>)

    @Update
    suspend fun updateMeditation(meditation: MeditationEntity)

    @Delete
    suspend fun deleteMeditation(meditation: MeditationEntity)

    @Query("DELETE FROM meditations WHERE id = :id")
    suspend fun deleteMeditationById(id: String)

    @Query("UPDATE meditations SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE meditations SET completedSessions = completedSessions + 1, totalMinutesMeditated = totalMinutesMeditated + :minutes, lastSessionDate = :date WHERE id = :id")
    suspend fun incrementSession(id: String, minutes: Int, date: Long)

    @Query("SELECT SUM(completedSessions) FROM meditations WHERE userId = :userId")
    suspend fun getTotalSessions(userId: String): Int?

    @Query("SELECT SUM(totalMinutesMeditated) FROM meditations WHERE userId = :userId")
    suspend fun getTotalMinutes(userId: String): Int?

    @Query("DELETE FROM meditations WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
