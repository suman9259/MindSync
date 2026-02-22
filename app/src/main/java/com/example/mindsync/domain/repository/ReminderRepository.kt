package com.example.mindsync.domain.repository

import com.example.mindsync.domain.model.Reminder
import com.example.mindsync.domain.model.ReminderType
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminders(userId: String): Flow<List<Reminder>>
    fun getRemindersByType(userId: String, type: ReminderType): Flow<List<Reminder>>
    fun getReminderById(id: String): Flow<Reminder?>
    suspend fun addReminder(reminder: Reminder): Result<Reminder>
    suspend fun updateReminder(reminder: Reminder): Result<Reminder>
    suspend fun deleteReminder(id: String): Result<Unit>
    suspend fun toggleReminder(id: String, enabled: Boolean): Result<Unit>
}
