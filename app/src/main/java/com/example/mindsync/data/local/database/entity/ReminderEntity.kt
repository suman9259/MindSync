package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val type: String,
    val referenceId: String?,
    val scheduledTime: Long,
    val repeatType: String,
    val repeatDays: String,
    val isEnabled: Boolean,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

fun ReminderEntity.toDomain(): com.example.mindsync.domain.model.Reminder {
    return com.example.mindsync.domain.model.Reminder(
        id = id,
        userId = userId,
        title = title,
        description = description,
        type = com.example.mindsync.domain.model.ReminderType.valueOf(type),
        referenceId = referenceId ?: "",
        scheduledTime = scheduledTime,
        repeatType = com.example.mindsync.domain.model.RepeatType.valueOf(repeatType),
        repeatDays = repeatDays.split(",")
            .filter { it.isNotBlank() }
            .map { com.example.mindsync.domain.model.DayOfWeek.valueOf(it.trim()) },
        isEnabled = isEnabled,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun com.example.mindsync.domain.model.Reminder.toEntity(isSynced: Boolean = false): ReminderEntity {
    return ReminderEntity(
        id = id,
        userId = userId,
        title = title,
        description = description,
        type = type.name,
        referenceId = referenceId,
        scheduledTime = scheduledTime,
        repeatType = repeatType.name,
        repeatDays = repeatDays.joinToString(",") { it.name },
        isEnabled = isEnabled,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}
