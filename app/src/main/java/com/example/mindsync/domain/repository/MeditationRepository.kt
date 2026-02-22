package com.example.mindsync.domain.repository

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationReminder
import com.example.mindsync.domain.model.MeditationSession
import com.example.mindsync.domain.model.MeditationStats
import kotlinx.coroutines.flow.Flow

interface MeditationRepository {
    fun getMeditations(userId: String): Flow<List<Meditation>>
    fun getMeditationById(id: String): Flow<Meditation?>
    suspend fun addMeditation(meditation: Meditation): Result<Meditation>
    suspend fun updateMeditation(meditation: Meditation): Result<Meditation>
    suspend fun deleteMeditation(id: String): Result<Unit>
    
    fun getMeditationSessions(userId: String): Flow<List<MeditationSession>>
    suspend fun addMeditationSession(session: MeditationSession): Result<MeditationSession>
    
    fun getMeditationReminders(userId: String): Flow<List<MeditationReminder>>
    suspend fun addMeditationReminder(reminder: MeditationReminder): Result<MeditationReminder>
    suspend fun updateMeditationReminder(reminder: MeditationReminder): Result<MeditationReminder>
    suspend fun deleteMeditationReminder(id: String): Result<Unit>
    
    fun getMeditationStats(userId: String): Flow<MeditationStats>
}
