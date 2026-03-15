package com.example.mindsync.domain.model

import java.util.UUID

data class Workout(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val category: WorkoutCategory = WorkoutCategory.STRENGTH,
    val exercises: List<Exercise> = emptyList(),
    val durationMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val imageUrl: String = "",
    val isCustom: Boolean = false,
    val reminderTime: Long? = null,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class WorkoutCategory(val displayName: String, val iconName: String) {
    STRENGTH("Strength", "fitness_center"),
    CARDIO("Cardio", "directions_run"),
    FLEXIBILITY("Flexibility", "self_improvement"),
    HIIT("HIIT", "flash_on"),
    YOGA("Yoga", "spa"),
    CROSSFIT("CrossFit", "sports_gymnastics"),
    BODYWEIGHT("Bodyweight", "accessibility"),
    POWERLIFTING("Powerlifting", "fitness_center")
}

data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val muscleGroup: MuscleGroup = MuscleGroup.CHEST,
    val sets: Int = 3,
    val reps: Int = 10,
    val weight: Float = 0f,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val restSeconds: Int = 60,
    val imageUrl: String = "",
    val videoUrl: String = "",
    val isCompleted: Boolean = false,
    val notes: String = "",
    val isWithWeight: Boolean = true
)

enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    LEGS("Legs"),
    QUADRICEPS("Quadriceps"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    CALVES("Calves"),
    ABS("Abs"),
    CORE("Core"),
    FULL_BODY("Full Body"),
    CARDIO("Cardio")
}

enum class WeightUnit(val symbol: String) {
    KG("kg"),
    LBS("lbs")
}

data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val workoutId: String = "",
    val userId: String = "",
    val exerciseLogs: List<ExerciseLog> = emptyList(),
    val totalDurationMinutes: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class ExerciseLog(
    val exerciseId: String = "",
    val exerciseName: String = "",
    val sets: List<SetLog> = emptyList(),
    val isCompleted: Boolean = false
)

data class SetLog(
    val setNumber: Int = 0,
    val reps: Int = 0,
    val weight: Float = 0f,
    val isCompleted: Boolean = false
)

data class WorkoutReminder(
    val id: String = UUID.randomUUID().toString(),
    val workoutId: String = "",
    val userId: String = "",
    val title: String = "",
    val scheduledTime: Long = 0,
    val repeatDays: List<Int> = emptyList(),
    val isEnabled: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class WorkoutProgress(
    val period: ProgressPeriod = ProgressPeriod.WEEKLY,
    val totalWorkouts: Int = 0,
    val totalMinutes: Int = 0,
    val totalCalories: Int = 0,
    val workoutsByDay: Map<Long, Int> = emptyMap(),
    val weightProgress: Map<String, List<WeightProgress>> = emptyMap(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)

enum class ProgressPeriod(val displayName: String, val days: Int) {
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365)
}

data class WeightProgress(
    val date: Long = 0,
    val weight: Float = 0f,
    val reps: Int = 0
)

data class WorkoutStats(
    val totalWorkouts: Int = 0,
    val totalMinutes: Int = 0,
    val totalCalories: Int = 0,
    val favoriteCategory: WorkoutCategory? = null,
    val strongestExercise: String = "",
    val maxWeight: Float = 0f,
    val currentStreak: Int = 0,
    val weeklyProgress: WorkoutProgress = WorkoutProgress(),
    val monthlyProgress: WorkoutProgress = WorkoutProgress(),
    val yearlyProgress: WorkoutProgress = WorkoutProgress()
)
