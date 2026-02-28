package com.example.mindsync.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_progress")
data class DailyProgressEntity(
    @PrimaryKey
    val id: String, // Format: "userId_yyyy-MM-dd"
    val userId: String,
    val date: Long, // Start of day timestamp
    val dateString: String, // "yyyy-MM-dd" for easy querying
    
    // Meditation progress
    val meditationSessionsCompleted: Int = 0,
    val meditationMinutes: Int = 0,
    
    // Workout progress
    val workoutsCompleted: Int = 0,
    val workoutMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    
    // Medicine progress
    val medicinesTaken: Int = 0,
    val medicinesTotal: Int = 0,
    
    // Skincare progress
    val skincareRoutinesCompleted: Int = 0,
    val skincareRoutinesTotal: Int = 0,
    
    // Overall
    val totalTasksCompleted: Int = 0,
    val totalTasks: Int = 0,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
