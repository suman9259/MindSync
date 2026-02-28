package com.example.mindsync.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.MedicineDao
import com.example.mindsync.data.local.database.dao.SkincareDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class DashboardViewModel(
    private val medicineDao: MedicineDao,
    private val skincareDao: SkincareDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private val _effect = Channel<DashboardEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadDashboardData()
    }

    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
            is DashboardIntent.SelectTab -> selectTab(intent.index)
            is DashboardIntent.Retry -> loadDashboardData()
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
                
                // Calculate total tasks (medicines + 2 skincare routines morning/evening)
                val skincareTotal = 2
                val totalTasks = medicinesTotal + skincareTotal
                val completedTasks = medicinesTaken + skincareCompleted
                
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
                        skincareCompleted = skincareCompleted,
                        skincareTotal = skincareTotal,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.send(DashboardEffect.ShowError("Failed to load dashboard data"))
            }
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
}