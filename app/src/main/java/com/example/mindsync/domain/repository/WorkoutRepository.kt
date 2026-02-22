package com.example.mindsync.domain.repository

import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutProgress
import com.example.mindsync.domain.model.WorkoutReminder
import com.example.mindsync.domain.model.WorkoutSession
import com.example.mindsync.domain.model.WorkoutStats
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getWorkouts(userId: String): Flow<List<Workout>>
    fun getWorkoutById(id: String): Flow<Workout?>
    fun getDefaultWorkouts(): Flow<List<Workout>>
    suspend fun addWorkout(workout: Workout): Result<Workout>
    suspend fun updateWorkout(workout: Workout): Result<Workout>
    suspend fun deleteWorkout(id: String): Result<Unit>
    
    fun getExercises(): Flow<List<Exercise>>
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>>
    
    fun getWorkoutSessions(userId: String): Flow<List<WorkoutSession>>
    fun getWorkoutSessionsByWorkoutId(workoutId: String): Flow<List<WorkoutSession>>
    suspend fun addWorkoutSession(session: WorkoutSession): Result<WorkoutSession>
    
    fun getWorkoutReminders(userId: String): Flow<List<WorkoutReminder>>
    suspend fun addWorkoutReminder(reminder: WorkoutReminder): Result<WorkoutReminder>
    suspend fun updateWorkoutReminder(reminder: WorkoutReminder): Result<WorkoutReminder>
    suspend fun deleteWorkoutReminder(id: String): Result<Unit>
    
    fun getWorkoutProgress(userId: String, period: ProgressPeriod): Flow<WorkoutProgress>
    fun getWorkoutStats(userId: String): Flow<WorkoutStats>
}
