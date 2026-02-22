package com.example.mindsync.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    
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
                delay(500)
                
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userName = currentUser?.displayName 
                    ?: currentUser?.phoneNumber 
                    ?: "User"

                _state.update {
                    it.copy(
                        userName = userName,
                        progress = 0.5f,
                        completedSteps = listOf("Gentle cleanser", "Vitamin C serum"),
                        totalSteps = 4,
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