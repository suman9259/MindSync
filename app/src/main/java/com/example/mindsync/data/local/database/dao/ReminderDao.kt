package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY scheduledTime ASC")
    fun getRemindersByUser(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderByIdFlow(id: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND type = :type ORDER BY scheduledTime ASC")
    fun getRemindersByType(userId: String, type: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND isEnabled = 1 ORDER BY scheduledTime ASC")
    fun getActiveReminders(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isSynced = 0")
    suspend fun getUnsyncedReminders(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: String)

    @Query("UPDATE reminders SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleReminder(id: String, enabled: Boolean)

    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM reminders WHERE referenceId = :referenceId")
    fun getRemindersByReference(referenceId: String): Flow<List<ReminderEntity>>
}
