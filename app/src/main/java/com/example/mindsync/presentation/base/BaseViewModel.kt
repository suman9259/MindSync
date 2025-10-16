package com.example.mindsync.presentation.base

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModel<UI_STATE : Parcelable, PARTIAL_UI_STATE, EVENT, INTENT>(
    savedStateHandle: SavedStateHandle,
    initialState: UI_STATE,
) : ViewModel() {

    private val intentFlow = Channel<INTENT>(Channel.UNLIMITED)

    val uiState = savedStateHandle.getStateFlow("savedUiStateKey", initialState)

    private val eventChannel = Channel<EVENT>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            intentFlow
                .receiveAsFlow()
                .flatMapMerge {
                    mapIntents(it)
                }
                .scan(uiState.value, ::reduceUiState)
                .catch { throwable ->
                    handleError(throwable)
                }
                .collect {
                    savedStateHandle["savedUiStateKey"] = it
                }
        }
    }

    protected open fun handleError(throwable: Throwable) {
        // Handle or log the error as needed
        // Example: Log.e("BaseViewModel", "Error in ViewModel", throwable)
    }

    fun processIntent(intent: INTENT) {
        viewModelScope.launch {
            intentFlow.send(intent)
        }
    }

    protected fun sendEvent(event: EVENT) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun mapIntents(intent: INTENT): Flow<PARTIAL_UI_STATE>

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun reduceUiState(
        previousState: UI_STATE,
        partialState: PARTIAL_UI_STATE,
    ): UI_STATE
}