package com.example.mindsync.presentation.workout

import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.model.WorkoutProgress
import com.example.mindsync.domain.model.WorkoutReminder
import com.example.mindsync.domain.model.WorkoutSession
import com.example.mindsync.domain.model.WorkoutStats
import com.example.mindsync.presentation.base.MviEffect
import com.example.mindsync.presentation.base.MviIntent
import com.example.mindsync.presentation.base.MviState

data class WorkoutState(
    val workouts: List<Workout> = emptyList(),
    val defaultWorkouts: List<Workout> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val reminders: List<WorkoutReminder> = emptyList(),
    val sessions: List<WorkoutSession> = emptyList(),
    val stats: WorkoutStats = WorkoutStats(),
    val progress: WorkoutProgress = WorkoutProgress(),
    val selectedWorkout: Workout? = null,
    val selectedCategory: WorkoutCategory? = null,
    val selectedPeriod: ProgressPeriod = ProgressPeriod.WEEKLY,
    val isLoading: Boolean = false,
    val isAddingWorkout: Boolean = false,
    val isAddingReminder: Boolean = false,
    val error: String? = null
) : MviState

sealed class WorkoutIntent : MviIntent {
    data object LoadWorkouts : WorkoutIntent()
    data object LoadDefaultWorkouts : WorkoutIntent()
    data object LoadExercises : WorkoutIntent()
    data class SelectWorkout(val workout: Workout) : WorkoutIntent()
    data class SelectCategory(val category: WorkoutCategory?) : WorkoutIntent()
    data class SelectPeriod(val period: ProgressPeriod) : WorkoutIntent()
    data class AddWorkout(val workout: Workout) : WorkoutIntent()
    data class UpdateWorkout(val workout: Workout) : WorkoutIntent()
    data class DeleteWorkout(val id: String) : WorkoutIntent()
    data class AddSession(val session: WorkoutSession) : WorkoutIntent()
    data class AddReminder(val reminder: WorkoutReminder) : WorkoutIntent()
    data class UpdateReminder(val reminder: WorkoutReminder) : WorkoutIntent()
    data class DeleteReminder(val id: String) : WorkoutIntent()
    data object Retry : WorkoutIntent()
    data object ClearError : WorkoutIntent()
}

sealed class WorkoutEffect : MviEffect {
    data class ShowError(val message: String) : WorkoutEffect()
    data class ShowSuccess(val message: String) : WorkoutEffect()
    data object NavigateBack : WorkoutEffect()
    data class NavigateToDetail(val workoutId: String) : WorkoutEffect()
    data object ReminderScheduled : WorkoutEffect()
}
