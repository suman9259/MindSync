package com.example.mindsync.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.MedicineDao
import com.example.mindsync.data.local.database.dao.ReminderDao
import com.example.mindsync.data.local.database.dao.SkincareDao
import com.example.mindsync.data.local.database.dao.SpecialDateDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
    private val medicineDao: MedicineDao,
    private val skincareDao: SkincareDao,
    private val reminderDao: ReminderDao,
    private val specialDateDao: SpecialDateDao,
    private val userPreferences: com.example.mindsync.data.local.UserPreferences
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private val _effect = Channel<DashboardEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadDashboardData()
        observeUpcomingTasks()
        observeQuickNotes()
    }

    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
            is DashboardIntent.SelectTab -> selectTab(intent.index)
            is DashboardIntent.Retry -> loadDashboardData()
            is DashboardIntent.AddQuickNote -> addQuickNote(intent.note)
            is DashboardIntent.DeleteQuickNote -> deleteQuickNote(intent.index)
        }
    }
    
    private fun observeQuickNotes() {
        viewModelScope.launch {
            userPreferences.quickNotes.collect { notes ->
                _state.update { it.copy(quickNotes = notes) }
            }
        }
    }
    
    private fun addQuickNote(note: String) {
        if (note.isBlank()) return
        viewModelScope.launch {
            val currentNotes = _state.value.quickNotes.toMutableList()
            currentNotes.add(note)
            userPreferences.saveQuickNotes(currentNotes)
        }
    }
    
    private fun deleteQuickNote(index: Int) {
        viewModelScope.launch {
            val currentNotes = _state.value.quickNotes.toMutableList()
            if (index in currentNotes.indices) {
                currentNotes.removeAt(index)
                userPreferences.saveQuickNotes(currentNotes)
            }
        }
    }

    private fun loadDashboardData() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid ?: ""
                val userName = currentUser?.displayName 
                    ?: currentUser?.phoneNumber 
                    ?: "User"

                // Get current day's live data
                val medicinesTaken = if (userId.isNotEmpty()) {
                    medicineDao.getTakenTodayCount(userId)
                } else 0
                
                val medicinesTotal = if (userId.isNotEmpty()) {
                    medicineDao.getActiveMedicineCount(userId)
                } else 0
                
                val skincareCompleted = if (userId.isNotEmpty()) {
                    skincareDao.getCompletedTodayCount(userId)
                } else 0
                
                // Build upcoming tasks from real data first to get accurate count
                val upcomingTasks = mutableListOf<UpcomingTask>()
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                
                // Add medicines as upcoming tasks
                if (userId.isNotEmpty()) {
                    val medicines = medicineDao.getActiveMedicines(userId).first()
                    medicines.forEach { medicine ->
                        // Parse scheduled times (comma-separated timestamps)
                        val times = medicine.scheduledTimes.split(",").filter { it.isNotBlank() }
                        val firstTime = times.firstOrNull()?.toLongOrNull()
                        val displayTime = if (firstTime != null) {
                            timeFormat.format(Date(firstTime))
                        } else {
                            "${medicine.timesPerDay}x daily"
                        }
                        upcomingTasks.add(
                            UpcomingTask(
                                id = medicine.id,
                                emoji = "💊",
                                title = medicine.name,
                                time = displayTime,
                                category = "medicine",
                                priority = if (firstTime != null) getPriorityFromTimestamp(firstTime) else 2
                            )
                        )
                    }
                }
                
                // Add skincare routines from database
                if (userId.isNotEmpty()) {
                    val skincareRoutines = skincareDao.getRoutinesByUser(userId).first()
                    skincareRoutines.forEach { routine ->
                        if (!routine.completedToday) {
                            val routineTime = if (routine.scheduledTime > 0) {
                                timeFormat.format(Date(routine.scheduledTime))
                            } else {
                                "Not scheduled"
                            }
                            val priority = if (routine.scheduledTime > 0) {
                                getPriorityFromTimestamp(routine.scheduledTime)
                            } else {
                                4 // Default priority for unscheduled
                            }
                            upcomingTasks.add(
                                UpcomingTask(
                                    id = routine.id,
                                    emoji = "✨",
                                    title = routine.name,
                                    time = routineTime,
                                    category = "skincare",
                                    priority = priority
                                )
                            )
                        }
                    }
                }
                
                // Add water reminder (always present)
                upcomingTasks.add(
                    UpcomingTask(
                        id = "water_reminder",
                        emoji = "💧",
                        title = "Drink Water",
                        time = "Every 2 hours",
                        category = "water",
                        priority = 1
                    )
                )
                
                // Add reminders from database
                if (userId.isNotEmpty()) {
                    val reminders = reminderDao.getActiveReminders(userId).first()
                    reminders.forEach { reminder ->
                        val reminderTime = Date(reminder.scheduledTime)
                        upcomingTasks.add(
                            UpcomingTask(
                                id = reminder.id,
                                emoji = getEmojiForType(reminder.type),
                                title = reminder.title,
                                time = timeFormat.format(reminderTime),
                                category = reminder.type.lowercase(),
                                priority = getPriorityFromTimestamp(reminder.scheduledTime)
                            )
                        )
                    }
                }
                
                // Sort by priority
                val sortedTasks = upcomingTasks.sortedBy { it.priority }
                
                // Calculate total tasks from actual upcoming tasks count
                val skincareRoutinesList = if (userId.isNotEmpty()) {
                    skincareDao.getRoutinesByUser(userId).first()
                } else emptyList()
                val skincareTotal = skincareRoutinesList.size
                val skincareCompletedCount = skincareRoutinesList.count { it.completedToday }
                val totalTasks = sortedTasks.size
                val completedTasks = medicinesTaken + skincareCompletedCount
                
                val progress = if (totalTasks > 0) {
                    completedTasks.toFloat() / totalTasks
                } else 0f

                _state.update {
                    it.copy(
                        userName = userName,
                        progress = progress,
                        completedSteps = emptyList(),
                        totalSteps = totalTasks,
                        completedTasksCount = completedTasks,
                        medicinesTaken = medicinesTaken,
                        medicinesTotal = medicinesTotal,
                        skincareCompleted = skincareCompletedCount,
                        skincareTotal = skincareTotal,
                        upcomingTasks = sortedTasks,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.send(DashboardEffect.ShowError("Failed to load dashboard data"))
            }
        }
    }
    
    private fun getPriorityFromTime(timeString: String, currentHour: Int): Int {
        return try {
            val hour = timeString.substringBefore(":").toIntOrNull() ?: 12
            val isPM = timeString.uppercase().contains("PM")
            val actualHour = if (isPM && hour != 12) hour + 12 else if (!isPM && hour == 12) 0 else hour
            if (actualHour > currentHour) actualHour - currentHour else actualHour + 24 - currentHour
        } catch (e: Exception) {
            5
        }
    }
    
    private fun getPriorityFromTimestamp(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = timestamp - now
        return when {
            diff < 0 -> 10 // Past
            diff < 3600000 -> 0 // Within 1 hour
            diff < 7200000 -> 1 // Within 2 hours
            diff < 14400000 -> 2 // Within 4 hours
            else -> 5
        }
    }
    
    private fun getEmojiForType(type: String): String {
        return when (type.uppercase()) {
            "MEDITATION" -> "🧘"
            "WORKOUT" -> "💪"
            "MEDICINE" -> "💊"
            "WATER" -> "💧"
            "SKINCARE" -> "✨"
            else -> "⏰"
        }
    }

    private fun selectTab(index: Int) {
        if (index !in 0..4) return

        val tab = DashboardTab.entries[index]
        _state.update { it.copy(selectedTab = index) }
        viewModelScope.launch {
            _effect.send(DashboardEffect.NavigateToTab(tab))
        }
    }
    
    private fun observeUpcomingTasks() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return
        
        viewModelScope.launch {
            combine(
                medicineDao.getActiveMedicines(userId),
                reminderDao.getActiveReminders(userId),
                skincareDao.getRoutinesByUser(userId),
                specialDateDao.getSpecialDatesByUser(userId)
            ) { medicines, reminders, skincareRoutines, specialDates ->
                val upcomingTasks = mutableListOf<UpcomingTask>()
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                
                // Count completed medicines
                val medicinesTakenCount = medicines.count { it.takenToday }
                
                // Add medicines (only those not taken yet)
                medicines.forEach { medicine ->
                    if (!medicine.takenToday) {
                        val times = medicine.scheduledTimes.split(",").filter { it.isNotBlank() }
                        val firstTime = times.firstOrNull()?.toLongOrNull()
                        val displayTime = if (firstTime != null) {
                            timeFormat.format(Date(firstTime))
                        } else {
                            "${medicine.timesPerDay}x daily"
                        }
                        upcomingTasks.add(
                            UpcomingTask(
                                id = medicine.id,
                                emoji = "💊",
                                title = medicine.name,
                                time = displayTime,
                                category = "medicine",
                                priority = if (firstTime != null) getPriorityFromTimestamp(firstTime) else 2
                            )
                        )
                    }
                }
                
                // Count completed skincare routines
                val skincareCompletedCount = skincareRoutines.count { it.completedToday }
                
                // Add skincare routines from database (only those not completed)
                skincareRoutines.forEach { routine ->
                    if (!routine.completedToday) {
                        val routineTime = if (routine.scheduledTime > 0) {
                            timeFormat.format(Date(routine.scheduledTime))
                        } else {
                            "Not scheduled"
                        }
                        val priority = if (routine.scheduledTime > 0) {
                            getPriorityFromTimestamp(routine.scheduledTime)
                        } else {
                            4 // Default priority for unscheduled
                        }
                        upcomingTasks.add(
                            UpcomingTask(
                                id = routine.id,
                                emoji = "✨",
                                title = routine.name,
                                time = routineTime,
                                category = "skincare",
                                priority = priority
                            )
                        )
                    }
                }
                
                // Add water reminder
                upcomingTasks.add(
                    UpcomingTask(
                        id = "water_reminder",
                        emoji = "💧",
                        title = "Drink Water",
                        time = "Every 2 hours",
                        category = "water",
                        priority = 1
                    )
                )
                
                // Add reminders
                reminders.forEach { reminder ->
                    val reminderTime = Date(reminder.scheduledTime)
                    upcomingTasks.add(
                        UpcomingTask(
                            id = reminder.id,
                            emoji = getEmojiForType(reminder.type),
                            title = reminder.title,
                            time = timeFormat.format(reminderTime),
                            category = reminder.type.lowercase(),
                            priority = getPriorityFromTimestamp(reminder.scheduledTime)
                        )
                    )
                }
                
                // Add birthdays and events (show 1 day before or on the day)
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val oneDayMs = 24 * 60 * 60 * 1000L
                val currentTime = System.currentTimeMillis()
                
                specialDates.forEach { specialDate ->
                    val daysUntil = (specialDate.date - currentTime) / oneDayMs
                    // Show if today (0 days) or tomorrow (1 day away)
                    if (daysUntil in 0..1) {
                        val emoji = when (specialDate.dateType.uppercase()) {
                            "BIRTHDAY" -> "🎂"
                            "ANNIVERSARY" -> "💍"
                            "GRADUATION" -> "🎓"
                            else -> "🎉"
                        }
                        val timeText = when (daysUntil.toInt()) {
                            0 -> "Today!"
                            1 -> "Tomorrow"
                            else -> dateFormat.format(Date(specialDate.date))
                        }
                        upcomingTasks.add(
                            UpcomingTask(
                                id = specialDate.id,
                                emoji = emoji,
                                title = "${specialDate.personName}'s ${specialDate.dateType.lowercase().replaceFirstChar { it.uppercase() }}",
                                time = timeText,
                                category = "event",
                                priority = if (daysUntil == 0L) -1 else 0 // High priority for today/tomorrow
                            )
                        )
                    }
                }
                
                val sortedTasks = upcomingTasks.sortedBy { it.priority }
                
                // Calculate total tasks (including completed ones) and progress
                val totalCompletedTasks = medicinesTakenCount + skincareCompletedCount
                val totalTasks = sortedTasks.size + totalCompletedTasks // pending + completed
                val progress = if (totalTasks > 0) totalCompletedTasks.toFloat() / totalTasks else 0f
                
                // Return all needed data for state update
                DashboardUpdateData(
                    sortedTasks = sortedTasks,
                    totalTasks = totalTasks,
                    completedTasks = totalCompletedTasks,
                    progress = progress,
                    medicinesTaken = medicinesTakenCount,
                    medicinesTotal = medicines.size,
                    skincareCompleted = skincareCompletedCount,
                    skincareTotal = skincareRoutines.size
                )
            }.collect { data ->
                _state.update { 
                    it.copy(
                        upcomingTasks = data.sortedTasks,
                        totalSteps = data.totalTasks,
                        completedTasksCount = data.completedTasks,
                        medicinesTaken = data.medicinesTaken,
                        medicinesTotal = data.medicinesTotal,
                        skincareCompleted = data.skincareCompleted,
                        skincareTotal = data.skincareTotal,
                        progress = data.progress
                    ) 
                }
            }
        }
    }
}

private data class DashboardUpdateData(
    val sortedTasks: List<UpcomingTask>,
    val totalTasks: Int,
    val completedTasks: Int,
    val progress: Float,
    val medicinesTaken: Int,
    val medicinesTotal: Int,
    val skincareCompleted: Int,
    val skincareTotal: Int
)