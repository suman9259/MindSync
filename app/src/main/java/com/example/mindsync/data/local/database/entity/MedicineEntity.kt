package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mindsync.domain.model.Medicine
import com.example.mindsync.domain.model.MedicineFrequency
import com.example.mindsync.domain.model.MedicineUnit

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val dosage: String,
    val unit: String,
    val frequency: String,
    val timesPerDay: Int,
    val scheduledTimes: String,
    val startDate: Long,
    val endDate: Long?,
    val instructions: String,
    val sideEffects: String,
    val imageUrl: String,
    val reminderEnabled: Boolean,
    val takenToday: Boolean,
    val takenCount: Int,
    val missedCount: Int,
    val currentStreak: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

fun MedicineEntity.toDomain(): Medicine {
    return Medicine(
        id = id,
        userId = userId,
        name = name,
        description = description,
        dosage = dosage,
        unit = MedicineUnit.valueOf(unit),
        frequency = MedicineFrequency.valueOf(frequency),
        timesPerDay = timesPerDay,
        scheduledTimes = scheduledTimes.split(",").filter { it.isNotBlank() }.map { it.toLong() },
        startDate = startDate,
        endDate = endDate,
        instructions = instructions,
        sideEffects = sideEffects,
        imageUrl = imageUrl,
        reminderEnabled = reminderEnabled,
        takenToday = takenToday,
        takenCount = takenCount,
        missedCount = missedCount,
        currentStreak = currentStreak,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Medicine.toEntity(isSynced: Boolean = false): MedicineEntity {
    return MedicineEntity(
        id = id,
        userId = userId,
        name = name,
        description = description,
        dosage = dosage,
        unit = unit.name,
        frequency = frequency.name,
        timesPerDay = timesPerDay,
        scheduledTimes = scheduledTimes.joinToString(","),
        startDate = startDate,
        endDate = endDate,
        instructions = instructions,
        sideEffects = sideEffects,
        imageUrl = imageUrl,
        reminderEnabled = reminderEnabled,
        takenToday = takenToday,
        takenCount = takenCount,
        missedCount = missedCount,
        currentStreak = currentStreak,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}
