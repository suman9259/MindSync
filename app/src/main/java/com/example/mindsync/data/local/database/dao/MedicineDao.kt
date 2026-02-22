package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getMedicinesByUser(userId: String): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity?

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineByIdFlow(id: String): Flow<MedicineEntity?>

    @Query("SELECT * FROM medicines WHERE userId = :userId AND reminderEnabled = 1")
    fun getActiveMedicines(userId: String): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE isSynced = 0")
    suspend fun getUnsyncedMedicines(): List<MedicineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicines(medicines: List<MedicineEntity>)

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: String)

    @Query("UPDATE medicines SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE medicines SET takenToday = :taken, takenCount = takenCount + 1 WHERE id = :id")
    suspend fun markAsTaken(id: String, taken: Boolean = true)

    @Query("UPDATE medicines SET takenToday = 0")
    suspend fun resetDailyStatus()

    @Query("SELECT COUNT(*) FROM medicines WHERE userId = :userId AND takenToday = 1")
    suspend fun getTakenTodayCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM medicines WHERE userId = :userId AND reminderEnabled = 1")
    suspend fun getActiveMedicineCount(userId: String): Int

    @Query("DELETE FROM medicines WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
