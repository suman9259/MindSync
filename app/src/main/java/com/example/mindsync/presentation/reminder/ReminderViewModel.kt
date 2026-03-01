package com.example.mindsync.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.MedicineDao
import com.example.mindsync.data.local.database.dao.ReminderDao
import com.example.mindsync.data.local.database.dao.SkincareDao
import com.example.mindsync.data.local.database.entity.ReminderEntity
import com.example.mindsync.domain.model.ReminderType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

data class ReminderState(
    val reminders: List<ReminderEntity> = emptyList(),
    val medicineCount: Int = 0,
    val skincareCount: Int = 0,
    val workoutCount: Int = 0,
    val meditationCount: Int = 0,
    val otherCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val upcomingCount: Int = 0,
    val isLoading: Boolean = false
)

class ReminderViewModel(
    private val reminderDao: ReminderDao,
    private val medicineDao: MedicineDao,
    private val skincareDao: SkincareDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(ReminderState())
    val state: StateFlow<ReminderState> = _state.asStateFlow()
    
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    init {
        observeReminders()
    }
    
    private fun observeReminders() {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            combine(
                reminderDao.getActiveReminders(userId),
                medicineDao.getActiveMedicines(userId),
                skincareDao.getRoutinesByUser(userId)
            ) { reminders, medicines, skincareRoutines ->
                val currentTime = System.currentTimeMillis()
                
                // Count by type from reminders table
                val workoutCount = reminders.count { it.type.equals("WORKOUT", ignoreCase = true) }
                val meditationCount = reminders.count { it.type.equals("MEDITATION", ignoreCase = true) }
                val otherCount = reminders.count { 
                    !it.type.equals("WORKOUT", ignoreCase = true) && 
                    !it.type.equals("MEDITATION", ignoreCase = true) 
                }
                
                // Pending = not completed today (medicines + skincare not done + active reminders)
                val pendingMedicines = medicines.count { !it.takenToday }
                val pendingSkincare = skincareRoutines.count { !it.completedToday }
                val pendingReminders = reminders.size // All active reminders are pending
                val pendingCount = pendingMedicines + pendingSkincare + pendingReminders
                
                // Completed today
                val completedMedicines = medicines.count { it.takenToday }
                val completedSkincare = skincareRoutines.count { it.completedToday }
                val completedCount = completedMedicines + completedSkincare
                
                // Upcoming = scheduled in the future
                val upcomingCount = reminders.count { it.scheduledTime > currentTime }
                
                ReminderState(
                    reminders = reminders,
                    medicineCount = medicines.size,
                    skincareCount = skincareRoutines.size,
                    workoutCount = workoutCount,
                    meditationCount = meditationCount,
                    otherCount = otherCount,
                    pendingCount = pendingCount,
                    completedCount = completedCount,
                    upcomingCount = upcomingCount,
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
    
    fun addReminder(
        title: String,
        type: ReminderType,
        scheduledTime: Long,
        repeatDays: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                description = "",
                type = type.name,
                referenceId = null,
                scheduledTime = scheduledTime,
                repeatType = "NONE",
                repeatDays = repeatDays,
                isEnabled = true,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            reminderDao.insertReminder(reminder)
        }
    }
    
    fun toggleReminder(reminderId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            reminderDao.updateReminderEnabled(reminderId, isEnabled)
        }
    }
    
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            reminderDao.deleteReminder(reminderId)
        }
    }
}
