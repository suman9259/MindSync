package com.example.mindsync.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindsync.data.local.database.entity.SpecialDateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecialDateDao {

    @Query("SELECT * FROM special_dates WHERE userId = :userId ORDER BY date ASC")
    fun getSpecialDatesByUser(userId: String): Flow<List<SpecialDateEntity>>

    @Query("SELECT * FROM special_dates WHERE id = :id")
    suspend fun getSpecialDateById(id: String): SpecialDateEntity?

    @Query("SELECT * FROM special_dates WHERE userId = :userId AND dateType = :type ORDER BY date ASC")
    fun getSpecialDatesByType(userId: String, type: String): Flow<List<SpecialDateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialDate(specialDate: SpecialDateEntity)

    @Update
    suspend fun updateSpecialDate(specialDate: SpecialDateEntity)

    @Query("DELETE FROM special_dates WHERE id = :id")
    suspend fun deleteSpecialDate(id: String)

    @Query("DELETE FROM special_dates WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
