package com.example.mindsync.presentation.dashboard

import com.example.mindsync.presentation.base.MviEffect
import com.example.mindsync.presentation.base.MviIntent
import com.example.mindsync.presentation.base.MviState

// State
data class DashboardState(
    val userName: String = "",
    val progress: Float = 0f,
    val completedSteps: List<String> = emptyList(),
    val totalSteps: Int = 0,
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
) : MviState

// Intents
sealed class DashboardIntent : MviIntent {
    data class SelectTab(val index: Int) : DashboardIntent()
    data object LoadDashboardData : DashboardIntent()
    data object Retry : DashboardIntent()
}

// Effects
sealed class DashboardEffect : MviEffect {
    data class ShowError(val message: String) : DashboardEffect()
    data class NavigateToTab(val destination: DashboardTab) : DashboardEffect()
}

enum class DashboardTab {
    HOME, SHOPPING, HEALTH, REMINDERS, PROFILE
}
