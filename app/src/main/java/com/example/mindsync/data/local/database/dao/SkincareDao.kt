package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.SkincareRoutineEntity
import com.example.mindsync.data.local.database.entity.SkincareStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkincareDao {

    @Query("SELECT * FROM skincare_routines WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getRoutinesByUser(userId: String): Flow<List<SkincareRoutineEntity>>

    @Query("SELECT * FROM skincare_routines WHERE id = :id")
    suspend fun getRoutineById(id: String): SkincareRoutineEntity?

    @Query("SELECT * FROM skincare_routines WHERE id = :id")
    fun getRoutineByIdFlow(id: String): Flow<SkincareRoutineEntity?>

    @Query("SELECT * FROM skincare_routines WHERE userId = :userId AND routineType = :type")
    fun getRoutinesByType(userId: String, type: String): Flow<List<SkincareRoutineEntity>>

    @Query("SELECT * FROM skincare_routines WHERE isSynced = 0")
    suspend fun getUnsyncedRoutines(): List<SkincareRoutineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: SkincareRoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<SkincareRoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: SkincareRoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: SkincareRoutineEntity)

    @Query("DELETE FROM skincare_routines WHERE id = :id")
    suspend fun deleteRoutineById(id: String)

    @Query("UPDATE skincare_routines SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE skincare_routines SET completedToday = :completed, completedCount = completedCount + 1 WHERE id = :id")
    suspend fun markAsCompleted(id: String, completed: Boolean = true)

    @Query("UPDATE skincare_routines SET completedToday = 0")
    suspend fun resetDailyStatus()

    @Query("SELECT * FROM skincare_steps WHERE routineId = :routineId ORDER BY orderIndex")
    fun getStepsByRoutine(routineId: String): Flow<List<SkincareStepEntity>>

    @Query("SELECT * FROM skincare_steps WHERE routineId = :routineId ORDER BY orderIndex")
    suspend fun getStepsByRoutineSync(routineId: String): List<SkincareStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: SkincareStepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<SkincareStepEntity>)

    @Delete
    suspend fun deleteStep(step: SkincareStepEntity)

    @Query("DELETE FROM skincare_steps WHERE routineId = :routineId")
    suspend fun deleteStepsByRoutine(routineId: String)

    @Transaction
    suspend fun insertRoutineWithSteps(routine: SkincareRoutineEntity, steps: List<SkincareStepEntity>) {
        insertRoutine(routine)
        insertSteps(steps)
    }

    @Transaction
    suspend fun deleteRoutineWithSteps(routineId: String) {
        deleteStepsByRoutine(routineId)
        deleteRoutineById(routineId)
    }

    @Query("SELECT COUNT(*) FROM skincare_routines WHERE userId = :userId AND completedToday = 1")
    suspend fun getCompletedTodayCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM skincare_routines WHERE userId = :userId")
    suspend fun getRoutineCount(userId: String): Int

    @Query("DELETE FROM skincare_routines WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("DELETE FROM skincare_steps WHERE routineId IN (SELECT id FROM skincare_routines WHERE userId = :userId)")
    suspend fun deleteAllStepsForUser(userId: String)
}
