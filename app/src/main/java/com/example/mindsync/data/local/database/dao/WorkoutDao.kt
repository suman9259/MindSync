package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.ExerciseEntity
import com.example.mindsync.data.local.database.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getWorkoutsByUser(userId: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: String): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutByIdFlow(id: String): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts WHERE userId = :userId AND category = :category ORDER BY updatedAt DESC")
    fun getWorkoutsByCategory(userId: String, category: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE isCustom = 0")
    fun getDefaultWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE isSynced = 0")
    suspend fun getUnsyncedWorkouts(): List<WorkoutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: String)

    @Query("UPDATE workouts SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE workouts SET updatedAt = :date WHERE id = :id")
    suspend fun markAsCompleted(id: String, date: Long)

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun getExercisesByWorkout(workoutId: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    suspend fun getExercisesByWorkoutSync(workoutId: String): List<ExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesByWorkout(workoutId: String)

    @Transaction
    suspend fun insertWorkoutWithExercises(workout: WorkoutEntity, exercises: List<ExerciseEntity>) {
        insertWorkout(workout)
        insertExercises(exercises)
    }

    @Transaction
    suspend fun deleteWorkoutWithExercises(workoutId: String) {
        deleteExercisesByWorkout(workoutId)
        deleteWorkoutById(workoutId)
    }

    @Query("SELECT COUNT(*) FROM workouts WHERE userId = :userId")
    suspend fun getTotalWorkouts(userId: String): Int?

    @Query("SELECT SUM(durationMinutes) FROM workouts WHERE userId = :userId")
    suspend fun getTotalMinutes(userId: String): Int?

    @Query("SELECT SUM(caloriesBurned) FROM workouts WHERE userId = :userId")
    suspend fun getTotalCalories(userId: String): Int?

    @Query("DELETE FROM workouts WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("DELETE FROM exercises WHERE workoutId IN (SELECT id FROM workouts WHERE userId = :userId)")
    suspend fun deleteAllExercisesForUser(userId: String)
}
