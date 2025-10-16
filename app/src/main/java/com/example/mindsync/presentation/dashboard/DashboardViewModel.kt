package com.example.mindsync.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.example.mindsync.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() :
    BaseViewModel<DashboardState, DashboardIntent, DashboardEffect>(DashboardState()) {

    init {
        processIntent(DashboardIntent.LoadDashboardData)
    }

    override fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboardData -> loadDashboardData()
            is DashboardIntent.SelectTab -> selectTab(intent.index)
            is DashboardIntent.Retry -> loadDashboardData()
        }
    }

    private fun loadDashboardData() {
        setState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Simulate API call or data loading
                delay(500) // Simulate network delay

                setState {
                    copy(
                        progress = 0.5f,
                        completedSteps = listOf("Gentle cleanser", "Vitamin C serum"),
                        totalSteps = 4,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
                setEffect { DashboardEffect.ShowError("Failed to load dashboard data") }
            }
        }
    }

    private fun selectTab(index: Int) {
        if (index !in 0..4) return

        val tab = DashboardTab.entries[index]
        setState { copy(selectedTab = index) }
        setEffect { DashboardEffect.NavigateToTab(tab) }
    }
}