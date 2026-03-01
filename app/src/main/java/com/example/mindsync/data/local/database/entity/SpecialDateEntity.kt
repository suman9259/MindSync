package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "special_dates")
data class SpecialDateEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val personName: String,
    val dateType: String,
    val date: Long,
    val year: Int?,
    val reminderDaysBefore: Int = 1,
    val reminderEnabled: Boolean = true,
    val giftIdeas: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
