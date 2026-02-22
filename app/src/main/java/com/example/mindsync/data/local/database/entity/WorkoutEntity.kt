package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val category: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val imageUrl: String,
    val isCustom: Boolean,
    val reminderEnabled: Boolean,
    val reminderTime: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val workoutId: String,
    val name: String,
    val description: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val weightUnit: String,
    val restSeconds: Int,
    val imageUrl: String,
    val videoUrl: String,
    val orderIndex: Int,
    val isSynced: Boolean = false
)

fun WorkoutEntity.toDomain(exercises: List<ExerciseEntity>): com.example.mindsync.domain.model.Workout {
    return com.example.mindsync.domain.model.Workout(
        id = id,
        userId = userId,
        name = name,
        description = description,
        category = com.example.mindsync.domain.model.WorkoutCategory.valueOf(category),
        exercises = exercises.map { it.toDomain() },
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        imageUrl = imageUrl,
        isCustom = isCustom,
        reminderEnabled = reminderEnabled,
        reminderTime = reminderTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ExerciseEntity.toDomain(): com.example.mindsync.domain.model.Exercise {
    return com.example.mindsync.domain.model.Exercise(
        id = id,
        name = name,
        description = description,
        muscleGroup = com.example.mindsync.domain.model.MuscleGroup.valueOf(muscleGroup),
        sets = sets,
        reps = reps,
        weight = weight,
        weightUnit = com.example.mindsync.domain.model.WeightUnit.valueOf(weightUnit),
        restSeconds = restSeconds,
        imageUrl = imageUrl,
        videoUrl = videoUrl
    )
}

fun com.example.mindsync.domain.model.Workout.toEntity(isSynced: Boolean = false): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        userId = userId,
        name = name,
        description = description,
        category = category.name,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        imageUrl = imageUrl,
        isCustom = isCustom,
        reminderEnabled = reminderEnabled,
        reminderTime = reminderTime,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced
    )
}

fun com.example.mindsync.domain.model.Exercise.toEntity(workoutId: String, orderIndex: Int, isSynced: Boolean = false): ExerciseEntity {
    return ExerciseEntity(
        id = id,
        workoutId = workoutId,
        name = name,
        description = description,
        muscleGroup = muscleGroup.name,
        sets = sets,
        reps = reps,
        weight = weight,
        weightUnit = weightUnit.name,
        restSeconds = restSeconds,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        orderIndex = orderIndex,
        isSynced = isSynced
    )
}
