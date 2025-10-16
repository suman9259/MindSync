package com.example.mindsync.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mindsync.presentation.theme.MindSyncTheme

/**
 * Base Activity class for MVI architecture with Jetpack Compose
 */
abstract class BaseActivity<STATE : Any, INTENT, EVENT> : ComponentActivity() {

    /**
     * The ViewModel that will handle the MVI logic
     */
    protected abstract val viewModel: BaseViewModel<*, *, EVENT, INTENT>

    /**
     * Handle one-time UI events
     */
    protected abstract fun handleEvent(event: EVENT)

    /**
     * Compose content for the activity
     */
    @Composable
    protected abstract fun Content(state: STATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            
            MindSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Main content
                    Content(state = uiState as STATE)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Blue)
                    }
                }
            }
            
            // Handle one-time events
            LaunchedEffect(key1 = viewModel) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
            
            // Handle back press if needed
            BackPressHandler()
        }
    }
    
    /**
     * Send intent to ViewModel
     */
    protected fun sendIntent(intent: INTENT) {
        viewModel.processIntent(intent)
    }
    
    /**
     * Handle back press if needed
     */
    @Composable
    private fun BackPressHandler() {
        // Handle back press if needed
        // You can override this in child activities
    }
}