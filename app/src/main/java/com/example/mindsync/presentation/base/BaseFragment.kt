package com.example.mindsync.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.mindsync.presentation.theme.MindSyncTheme

/**
 * Base Fragment class for MVI architecture with Jetpack Compose
 */
abstract class BaseFragment<STATE : Any, INTENT, EVENT, VM : BaseViewModel<*, *, EVENT, INTENT>> : Fragment() {

    /**
     * The ViewModel that will handle the MVI logic
     */
    protected abstract val viewModel: VM

    /**
     * Handle one-time UI events
     */
    protected abstract fun handleEvent(event: EVENT)

    /**
     * Compose content for the fragment
     */
    @Composable
    protected abstract fun Content(state: STATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
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
            }
        }
    }
    
    /**
     * Send intent to ViewModel
     */
    protected fun sendIntent(intent: INTENT) {
        viewModel.processIntent(intent)
    }
    

}