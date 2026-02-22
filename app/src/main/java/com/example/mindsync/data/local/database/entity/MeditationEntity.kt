package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditations")
data class MeditationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val category: String,
    val imageUrl: String,
    val audioUrl: String,
    val notes: String,
    val reminderEnabled: Boolean,
    val reminderTime: Long?,
    val completedSessions: Int,
    val totalMinutesMeditated: Int,
    val lastSessionDate: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

fun MeditationEntity.toDomain(): com.example.mindsync.domain.model.Meditation {
    return com.example.mindsync.domain.model.Meditation(
        id = id,
        userId = userId,
        title = title,
        description = description,
        durationMinutes = durationMinutes,
        category = com.example.mindsync.domain.model.MeditationCategory.valueOf(category),
        imageUrl = imageUrl,
        audioUrl = audioUrl,
        notes = notes,
        reminderEnabled = reminderEnabled,
        reminderTime = reminderTime,
        completedSessions = completedSessions,
        totalMinutesMeditated = totalMinutesMeditated,
        lastSessionDate = lastSessionDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun com.example.mindsync.domain.model.Meditation.toEntity(isSynced: Boolean = false): MeditationEntity {
    return MeditationEntity(
        id = id,
        userId = userId,
        title = title,
        description = description,
        durationMinutes = durationMinutes,
        category = category.name,
        imageUrl = imageUrl,
        audioUrl = audioUrl,
        notes = notes,
        reminderEnabled = reminderEnabled,
        reminderTime = reminderTime,
        completedSessions = completedSessions,
        totalMinutesMeditated = totalMinutesMeditated,
        lastSessionDate = lastSessionDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}
