package com.example.mindsync.data.repository

import com.example.mindsync.domain.model.DayOfWeek
import com.example.mindsync.domain.model.Reminder
import com.example.mindsync.domain.model.ReminderType
import com.example.mindsync.domain.model.RepeatType
import com.example.mindsync.domain.repository.ReminderRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReminderRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ReminderRepository {

    private val remindersCollection = firestore.collection("reminders")

    override fun getReminders(userId: String): Flow<List<Reminder>> = callbackFlow {
        val listener = remindersCollection
            .whereEqualTo("userId", userId)
            .orderBy("scheduledTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reminders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toReminder()
                } ?: emptyList()
                trySend(reminders)
            }
        awaitClose { listener.remove() }
    }

    override fun getRemindersByType(userId: String, type: ReminderType): Flow<List<Reminder>> = callbackFlow {
        val listener = remindersCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", type.name)
            .orderBy("scheduledTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reminders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toReminder()
                } ?: emptyList()
                trySend(reminders)
            }
        awaitClose { listener.remove() }
    }

    override fun getReminderById(id: String): Flow<Reminder?> = callbackFlow {
        val listener = remindersCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toReminder())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addReminder(reminder: Reminder): Result<Reminder> = runCatching {
        val docRef = remindersCollection.document(reminder.id)
        docRef.set(reminder.toMap()).await()
        reminder
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Reminder> = runCatching {
        val updated = reminder.copy(updatedAt = System.currentTimeMillis())
        remindersCollection.document(reminder.id).set(updated.toMap()).await()
        updated
    }

    override suspend fun deleteReminder(id: String): Result<Unit> = runCatching {
        remindersCollection.document(id).delete().await()
    }

    override suspend fun toggleReminder(id: String, enabled: Boolean): Result<Unit> = runCatching {
        remindersCollection.document(id).update(
            mapOf(
                "isEnabled" to enabled,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toReminder(): Reminder? {
        return try {
            @Suppress("UNCHECKED_CAST")
            val repeatDaysList = (get("repeatDays") as? List<String>)?.mapNotNull { dayName ->
                try { DayOfWeek.valueOf(dayName) } catch (e: Exception) { null }
            } ?: emptyList()

            Reminder(
                id = id,
                userId = getString("userId") ?: "",
                title = getString("title") ?: "",
                description = getString("description") ?: "",
                type = ReminderType.valueOf(getString("type") ?: "GENERAL"),
                referenceId = getString("referenceId") ?: "",
                scheduledTime = getLong("scheduledTime") ?: 0,
                repeatType = RepeatType.valueOf(getString("repeatType") ?: "ONCE"),
                repeatDays = repeatDaysList,
                isEnabled = getBoolean("isEnabled") ?: true,
                notes = getString("notes") ?: "",
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Reminder.toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "title" to title,
        "description" to description,
        "type" to type.name,
        "referenceId" to referenceId,
        "scheduledTime" to scheduledTime,
        "repeatType" to repeatType.name,
        "repeatDays" to repeatDays.map { it.name },
        "isEnabled" to isEnabled,
        "notes" to notes,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
