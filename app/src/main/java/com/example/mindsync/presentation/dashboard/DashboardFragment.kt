package com.example.mindsync.presentation.dashboard

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.example.mindsync.presentation.base.BaseFragment
import com.example.mindsync.presentation.theme.MindSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<DashboardState, DashboardIntent, DashboardEffect>() {

    override val viewModel: DashboardViewModel by viewModels()

    override fun handleEffect(effect: DashboardEffect) {
        when (effect) {
            is DashboardEffect.NavigateToTab -> handleNavigation(effect.destination)
            is DashboardEffect.ShowError -> showError(effect.message)
        }
    }

    @Composable
    override fun Content(state: DashboardState) {
        MindSyncTheme {
            DashboardScreen(
                state = state,
                onNavigationItemSelected = { index ->
                    sendIntent(DashboardIntent.SelectTab(index))
                },
                onRetry = {
                    sendIntent(DashboardIntent.Retry)
                },
                onStartRoutine = {
                    // Handle start routine action
                }
            )
        }
    }

    private fun handleNavigation(tab: DashboardTab) {
        when (tab) {
            DashboardTab.HOME -> { /* Navigate to home */ }
            DashboardTab.SHOPPING -> { /* Navigate to shopping */ }
            DashboardTab.HEALTH -> { /* Navigate to health */ }
            DashboardTab.REMINDERS -> { /* Navigate to reminders */ }
            DashboardTab.PROFILE -> { /* Navigate to profile */ }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }
}