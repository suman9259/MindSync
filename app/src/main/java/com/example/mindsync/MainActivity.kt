package com.example.mindsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mindsync.presentation.auth.AuthState
import com.example.mindsync.presentation.auth.AuthViewModel
import com.example.mindsync.presentation.auth.LoginScreen
import com.example.mindsync.presentation.dashboard.DashboardScreen
import com.example.mindsync.presentation.dashboard.DashboardState
import com.example.mindsync.presentation.dashboard.DashboardViewModel
import com.example.mindsync.presentation.theme.MindSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MindSyncApp()
                }
            }
        }
    }
}

@Composable
fun MindSyncApp() {
    val viewModel: AuthViewModel = hiltViewModel()
    val authState by viewModel.authState.collectAsState()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val dashboardState by dashboardViewModel.state.collectAsState()

    when (val state = authState) {
        is AuthState.Success -> {
            if (state.user != null) {
                // User is logged in, show dashboard
                DashboardScreen(
                    state = dashboardState,
                    onNavigationItemSelected = { /* Handle navigation item selection */ },
                    onRetry = { /* Handle retry action */ },
                    onStartRoutine = { /* Handle start routine action */ }
                )
            } else {
                // Show login screen
                LoginScreen(
                    onNavigateToSignUp = { /* Handle sign up navigation */ },
                    onLoginSuccess = { /* Handle successful login */ }
                )
            }
        }
        is AuthState.Loading -> {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthState.Error -> {
            // Show error message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.message}")
            }
        }
        is AuthState.Idle -> {
            // Initial state, show loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}