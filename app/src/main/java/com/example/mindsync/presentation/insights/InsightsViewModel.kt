package com.example.mindsync.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.AggregatedProgress
import com.example.mindsync.data.local.database.dao.DailyProgressDao
import com.example.mindsync.data.local.database.dao.MedicineDao
import com.example.mindsync.data.local.database.dao.SkincareDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ReportPeriod {
    TODAY, WEEK, MONTH, YEAR
}

data class ProgressReport(
    val period: ReportPeriod = ReportPeriod.TODAY,
    val medicinesTaken: Int = 0,
    val medicinesTotal: Int = 0,
    val skincareCompleted: Int = 0,
    val skincareTotal: Int = 0,
    val meditationSessions: Int = 0,
    val meditationMinutes: Int = 0,
    val workoutsCompleted: Int = 0,
    val workoutMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val totalCompleted: Int = 0,
    val totalTasks: Int = 0,
    val daysTracked: Int = 0,
    val completionRate: Float = 0f
)

data class InsightsState(
    val medicinesTaken: Int = 0,
    val medicinesTotal: Int = 0,
    val skincareCompleted: Int = 0,
    val skincareTotal: Int = 0,
    val medicineStreak: Int = 0,
    val skincareStreak: Int = 0,
    val longestMedicineStreak: Int = 0,
    val longestSkincareStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val medicineProgress: Float = 0f,
    val skincareProgress: Float = 0f,
    val weeklyMedicineData: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val weeklySkincareData: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val achievements: List<Achievement> = emptyList(),
    val selectedPeriod: ReportPeriod = ReportPeriod.TODAY,
    val progressReport: ProgressReport = ProgressReport(),
    val weeklyProgress: List<ProgressReport> = emptyList(),
    val isLoading: Boolean = false
)

data class Achievement(
    val emoji: String,
    val title: String,
    val unlocked: Boolean
)

class InsightsViewModel(
    private val medicineDao: MedicineDao,
    private val skincareDao: SkincareDao,
    private val dailyProgressDao: DailyProgressDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state.asStateFlow()
    
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    init {
        loadInsights()
        loadProgressReport(ReportPeriod.TODAY)
    }
    
    fun selectPeriod(period: ReportPeriod) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadProgressReport(period)
    }
    
    private fun loadProgressReport(period: ReportPeriod) {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endDate = calendar.timeInMillis
            
            // Set start of today
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis
            
            val startDate = when (period) {
                ReportPeriod.TODAY -> todayStart
                ReportPeriod.WEEK -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    calendar.timeInMillis
                }
                ReportPeriod.MONTH -> {
                    calendar.timeInMillis = todayStart
                    calendar.add(Calendar.MONTH, -1)
                    calendar.timeInMillis
                }
                ReportPeriod.YEAR -> {
                    calendar.timeInMillis = todayStart
                    calendar.add(Calendar.YEAR, -1)
                    calendar.timeInMillis
                }
            }
            
            val aggregated = dailyProgressDao.getAggregatedProgress(userId, startDate, endDate)
            
            val report = if (aggregated != null) {
                val completionRate = if (aggregated.totalTasks > 0) {
                    aggregated.totalCompleted.toFloat() / aggregated.totalTasks
                } else 0f
                
                ProgressReport(
                    period = period,
                    medicinesTaken = aggregated.medicinesTaken,
                    medicinesTotal = aggregated.medicinesTotal,
                    skincareCompleted = aggregated.skincareCompleted,
                    skincareTotal = aggregated.skincareTotal,
                    meditationSessions = aggregated.meditationSessions,
                    meditationMinutes = aggregated.meditationMinutes,
                    workoutsCompleted = aggregated.workoutsCompleted,
                    workoutMinutes = aggregated.workoutMinutes,
                    caloriesBurned = aggregated.caloriesBurned,
                    totalCompleted = aggregated.totalCompleted,
                    totalTasks = aggregated.totalTasks,
                    daysTracked = aggregated.daysTracked,
                    completionRate = completionRate
                )
            } else {
                ProgressReport(period = period)
            }
            
            _state.value = _state.value.copy(progressReport = report)
        }
    }
    
    private fun loadInsights() {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            combine(
                medicineDao.getActiveMedicines(userId),
                skincareDao.getRoutinesByUser(userId)
            ) { medicines, skincareRoutines ->
                val medicinesTaken = medicines.count { it.takenToday }
                val medicinesTotal = medicines.size
                val skincareCompleted = skincareRoutines.count { it.completedToday }
                val skincareTotal = skincareRoutines.size
                
                val medicineStreak = medicines.maxOfOrNull { it.currentStreak } ?: 0
                val skincareStreak = skincareRoutines.maxOfOrNull { it.currentStreak } ?: 0
                
                val medicineProgress = if (medicinesTotal > 0) medicinesTaken.toFloat() / medicinesTotal else 0f
                val skincareProgress = if (skincareTotal > 0) skincareCompleted.toFloat() / skincareTotal else 0f
                
                // Calculate achievements based on real data
                val achievements = mutableListOf<Achievement>()
                
                // Streak achievements
                val maxStreak = maxOf(medicineStreak, skincareStreak)
                achievements.add(Achievement("🔥", "7 Day Streak", maxStreak >= 7))
                achievements.add(Achievement("⭐", "30 Day Streak", maxStreak >= 30))
                
                // Medicine achievements
                achievements.add(Achievement("💊", "First Medicine", medicinesTotal > 0))
                achievements.add(Achievement("💪", "10 Medicines Taken", medicines.sumOf { it.takenCount } >= 10))
                
                // Skincare achievements
                achievements.add(Achievement("✨", "First Routine", skincareTotal > 0))
                achievements.add(Achievement("🏆", "Skincare Master", skincareRoutines.sumOf { it.completedCount } >= 50))
                
                _state.value.copy(
                    medicinesTaken = medicinesTaken,
                    medicinesTotal = medicinesTotal,
                    skincareCompleted = skincareCompleted,
                    skincareTotal = skincareTotal,
                    medicineStreak = medicineStreak,
                    skincareStreak = skincareStreak,
                    longestMedicineStreak = medicineStreak,
                    longestSkincareStreak = skincareStreak,
                    totalActiveDays = maxStreak,
                    medicineProgress = medicineProgress,
                    skincareProgress = skincareProgress,
                    achievements = achievements,
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
