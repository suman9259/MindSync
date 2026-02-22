package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mindsync.domain.model.SkincareCategory
import com.example.mindsync.domain.model.SkincareRoutine
import com.example.mindsync.domain.model.SkincareRoutineType
import com.example.mindsync.domain.model.SkincareStep

@Entity(tableName = "skincare_routines")
data class SkincareRoutineEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val routineType: String,
    val scheduledTime: Long,
    val estimatedMinutes: Int,
    val reminderEnabled: Boolean,
    val completedToday: Boolean,
    val completedCount: Int,
    val currentStreak: Int,
    val imageUrl: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

@Entity(tableName = "skincare_steps")
data class SkincareStepEntity(
    @PrimaryKey
    val id: String,
    val routineId: String,
    val name: String,
    val productName: String,
    val productBrand: String,
    val category: String,
    val instructions: String,
    val durationSeconds: Int,
    val orderIndex: Int,
    val isCompleted: Boolean,
    val imageUrl: String,
    val isSynced: Boolean = false
)

fun SkincareRoutineEntity.toDomain(steps: List<SkincareStepEntity>): SkincareRoutine {
    return SkincareRoutine(
        id = id,
        userId = userId,
        name = name,
        description = description,
        routineType = SkincareRoutineType.valueOf(routineType),
        steps = steps.map { it.toDomain() },
        scheduledTime = scheduledTime,
        estimatedMinutes = estimatedMinutes,
        reminderEnabled = reminderEnabled,
        completedToday = completedToday,
        completedCount = completedCount,
        currentStreak = currentStreak,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun SkincareStepEntity.toDomain(): SkincareStep {
    return SkincareStep(
        id = id,
        name = name,
        productName = productName,
        productBrand = productBrand,
        category = SkincareCategory.valueOf(category),
        instructions = instructions,
        durationSeconds = durationSeconds,
        orderIndex = orderIndex,
        isCompleted = isCompleted,
        imageUrl = imageUrl
    )
}

fun SkincareRoutine.toEntity(isSynced: Boolean = false): SkincareRoutineEntity {
    return SkincareRoutineEntity(
        id = id,
        userId = userId,
        name = name,
        description = description,
        routineType = routineType.name,
        scheduledTime = scheduledTime,
        estimatedMinutes = estimatedMinutes,
        reminderEnabled = reminderEnabled,
        completedToday = completedToday,
        completedCount = completedCount,
        currentStreak = currentStreak,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}

fun SkincareStep.toEntity(routineId: String, isSynced: Boolean = false): SkincareStepEntity {
    return SkincareStepEntity(
        id = id,
        routineId = routineId,
        name = name,
        productName = productName,
        productBrand = productBrand,
        category = category.name,
        instructions = instructions,
        durationSeconds = durationSeconds,
        orderIndex = orderIndex,
        isCompleted = isCompleted,
        imageUrl = imageUrl,
        isSynced = isSynced
    )
}
