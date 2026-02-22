package com.example.mindsync.data.repository

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.model.MeditationReminder
import com.example.mindsync.domain.model.MeditationSession
import com.example.mindsync.domain.model.MeditationStats
import com.example.mindsync.domain.repository.MeditationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MeditationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : MeditationRepository {

    private val meditationsCollection = firestore.collection("meditations")
    private val sessionsCollection = firestore.collection("meditation_sessions")
    private val remindersCollection = firestore.collection("meditation_reminders")

    override fun getMeditations(userId: String): Flow<List<Meditation>> = callbackFlow {
        val listener = meditationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val meditations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toMeditation()
                } ?: emptyList()
                trySend(meditations)
            }
        awaitClose { listener.remove() }
    }

    override fun getMeditationById(id: String): Flow<Meditation?> = callbackFlow {
        val listener = meditationsCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toMeditation())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addMeditation(meditation: Meditation): Result<Meditation> = runCatching {
        val docRef = meditationsCollection.document(meditation.id)
        docRef.set(meditation.toMap()).await()
        meditation
    }

    override suspend fun updateMeditation(meditation: Meditation): Result<Meditation> = runCatching {
        val updated = meditation.copy(updatedAt = System.currentTimeMillis())
        meditationsCollection.document(meditation.id).set(updated.toMap()).await()
        updated
    }

    override suspend fun deleteMeditation(id: String): Result<Unit> = runCatching {
        meditationsCollection.document(id).delete().await()
    }

    override fun getMeditationSessions(userId: String): Flow<List<MeditationSession>> = callbackFlow {
        val listener = sessionsCollection
            .whereEqualTo("userId", userId)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toMeditationSession()
                } ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addMeditationSession(session: MeditationSession): Result<MeditationSession> = runCatching {
        val docRef = sessionsCollection.document(session.id)
        docRef.set(session.toMap()).await()
        session
    }

    override fun getMeditationReminders(userId: String): Flow<List<MeditationReminder>> = callbackFlow {
        val listener = remindersCollection
            .whereEqualTo("userId", userId)
            .orderBy("scheduledTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reminders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toMeditationReminder()
                } ?: emptyList()
                trySend(reminders)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addMeditationReminder(reminder: MeditationReminder): Result<MeditationReminder> = runCatching {
        val docRef = remindersCollection.document(reminder.id)
        docRef.set(reminder.toMap()).await()
        reminder
    }

    override suspend fun updateMeditationReminder(reminder: MeditationReminder): Result<MeditationReminder> = runCatching {
        remindersCollection.document(reminder.id).set(reminder.toMap()).await()
        reminder
    }

    override suspend fun deleteMeditationReminder(id: String): Result<Unit> = runCatching {
        remindersCollection.document(id).delete().await()
    }

    override fun getMeditationStats(userId: String): Flow<MeditationStats> = callbackFlow {
        val listener = sessionsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { it.toMeditationSession() } ?: emptyList()
                val stats = calculateStats(sessions)
                trySend(stats)
            }
        awaitClose { listener.remove() }
    }

    private fun calculateStats(sessions: List<MeditationSession>): MeditationStats {
        if (sessions.isEmpty()) return MeditationStats()
        
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        val averageDuration = totalMinutes.toFloat() / sessions.size
        
        return MeditationStats(
            totalSessions = sessions.size,
            totalMinutes = totalMinutes,
            averageSessionDuration = averageDuration
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMeditation(): Meditation? {
        return try {
            Meditation(
                id = id,
                userId = getString("userId") ?: "",
                title = getString("title") ?: "",
                description = getString("description") ?: "",
                durationMinutes = getLong("durationMinutes")?.toInt() ?: 10,
                category = MeditationCategory.valueOf(getString("category") ?: "MINDFULNESS"),
                reminderTime = getLong("reminderTime"),
                reminderEnabled = getBoolean("reminderEnabled") ?: false,
                notes = getString("notes") ?: "",
                completedSessions = getLong("completedSessions")?.toInt() ?: 0,
                totalMinutesMeditated = getLong("totalMinutesMeditated")?.toInt() ?: 0,
                lastSessionDate = getLong("lastSessionDate"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMeditationSession(): MeditationSession? {
        return try {
            MeditationSession(
                id = id,
                meditationId = getString("meditationId") ?: "",
                userId = getString("userId") ?: "",
                durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
                completedAt = getLong("completedAt") ?: System.currentTimeMillis(),
                notes = getString("notes") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMeditationReminder(): MeditationReminder? {
        return try {
            @Suppress("UNCHECKED_CAST")
            MeditationReminder(
                id = id,
                meditationId = getString("meditationId") ?: "",
                userId = getString("userId") ?: "",
                title = getString("title") ?: "",
                scheduledTime = getLong("scheduledTime") ?: 0,
                repeatDays = (get("repeatDays") as? List<Long>)?.map { it.toInt() } ?: emptyList(),
                isEnabled = getBoolean("isEnabled") ?: true,
                notes = getString("notes") ?: "",
                createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Meditation.toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "title" to title,
        "description" to description,
        "durationMinutes" to durationMinutes,
        "category" to category.name,
        "reminderTime" to reminderTime,
        "reminderEnabled" to reminderEnabled,
        "notes" to notes,
        "completedSessions" to completedSessions,
        "totalMinutesMeditated" to totalMinutesMeditated,
        "lastSessionDate" to lastSessionDate,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    private fun MeditationSession.toMap(): Map<String, Any?> = mapOf(
        "meditationId" to meditationId,
        "userId" to userId,
        "durationMinutes" to durationMinutes,
        "completedAt" to completedAt,
        "mood" to mood?.name,
        "notes" to notes
    )

    private fun MeditationReminder.toMap(): Map<String, Any?> = mapOf(
        "meditationId" to meditationId,
        "userId" to userId,
        "title" to title,
        "scheduledTime" to scheduledTime,
        "repeatDays" to repeatDays,
        "isEnabled" to isEnabled,
        "notes" to notes,
        "createdAt" to createdAt
    )
}
