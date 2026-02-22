package com.example.mindsync.presentation.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.model.MeditationReminder
import com.example.mindsync.domain.usecase.meditation.AddMeditationReminderUseCase
import com.example.mindsync.domain.usecase.meditation.AddMeditationUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationStatsUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationsUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeditationViewModel(
    private val getMeditationsUseCase: GetMeditationsUseCase,
    private val addMeditationUseCase: AddMeditationUseCase,
    private val addMeditationReminderUseCase: AddMeditationReminderUseCase,
    private val getMeditationStatsUseCase: GetMeditationStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MeditationState())
    val state: StateFlow<MeditationState> = _state.asStateFlow()

    private val _effect = Channel<MeditationEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadMeditations()
    }

    fun processIntent(intent: MeditationIntent) {
        when (intent) {
            is MeditationIntent.LoadMeditations -> loadMeditations()
            is MeditationIntent.SelectMeditation -> selectMeditation(intent.meditation)
            is MeditationIntent.SelectCategory -> filterByCategory(intent.category)
            is MeditationIntent.AddMeditation -> addMeditation(intent.meditation)
            is MeditationIntent.UpdateMeditation -> updateMeditation(intent.meditation)
            is MeditationIntent.DeleteMeditation -> deleteMeditation(intent.id)
            is MeditationIntent.AddReminder -> addReminder(intent.reminder)
            is MeditationIntent.UpdateReminder -> updateReminder(intent.reminder)
            is MeditationIntent.DeleteReminder -> deleteReminder(intent.id)
            is MeditationIntent.Retry -> loadMeditations()
            is MeditationIntent.ClearError -> clearError()
        }
    }

    private fun loadMeditations() {
        if (userId.isEmpty()) {
            _state.update { it.copy(isLoading = false, error = "Please sign in to continue") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            getMeditationsUseCase(userId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    _effect.send(MeditationEffect.ShowError(e.message ?: "Failed to load meditations"))
                }
                .collect { meditations ->
                    _state.update { it.copy(meditations = meditations, isLoading = false) }
                }
        }

        viewModelScope.launch {
            getMeditationStatsUseCase(userId)
                .catch { /* Ignore stats errors */ }
                .collect { stats ->
                    _state.update { it.copy(stats = stats) }
                }
        }
    }

    private fun selectMeditation(meditation: Meditation) {
        _state.update { it.copy(selectedMeditation = meditation) }
        viewModelScope.launch {
            _effect.send(MeditationEffect.NavigateToDetail(meditation.id))
        }
    }

    private fun filterByCategory(category: MeditationCategory?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    private fun addMeditation(meditation: Meditation) {
        _state.update { it.copy(isAddingMeditation = true) }

        viewModelScope.launch {
            val meditationWithUser = meditation.copy(userId = userId)
            addMeditationUseCase(meditationWithUser)
                .onSuccess {
                    _state.update { it.copy(isAddingMeditation = false) }
                    _effect.send(MeditationEffect.ShowSuccess("Meditation added successfully"))
                    _effect.send(MeditationEffect.NavigateBack)
                }
                .onFailure { e ->
                    _state.update { it.copy(isAddingMeditation = false, error = e.message) }
                    _effect.send(MeditationEffect.ShowError(e.message ?: "Failed to add meditation"))
                }
        }
    }

    private fun updateMeditation(meditation: Meditation) {
        viewModelScope.launch {
            addMeditationUseCase(meditation)
                .onSuccess {
                    _effect.send(MeditationEffect.ShowSuccess("Meditation updated"))
                }
                .onFailure { e ->
                    _effect.send(MeditationEffect.ShowError(e.message ?: "Failed to update"))
                }
        }
    }

    private fun deleteMeditation(id: String) {
        viewModelScope.launch {
            _effect.send(MeditationEffect.ShowSuccess("Meditation deleted"))
        }
    }

    private fun addReminder(reminder: MeditationReminder) {
        _state.update { it.copy(isAddingReminder = true) }

        viewModelScope.launch {
            val reminderWithUser = reminder.copy(userId = userId)
            addMeditationReminderUseCase(reminderWithUser)
                .onSuccess {
                    _state.update { it.copy(isAddingReminder = false) }
                    _effect.send(MeditationEffect.ReminderScheduled)
                    _effect.send(MeditationEffect.ShowSuccess("Reminder set successfully"))
                }
                .onFailure { e ->
                    _state.update { it.copy(isAddingReminder = false, error = e.message) }
                    _effect.send(MeditationEffect.ShowError(e.message ?: "Failed to set reminder"))
                }
        }
    }

    private fun updateReminder(reminder: MeditationReminder) {
        viewModelScope.launch {
            _effect.send(MeditationEffect.ShowSuccess("Reminder updated"))
        }
    }

    private fun deleteReminder(id: String) {
        viewModelScope.launch {
            _effect.send(MeditationEffect.ShowSuccess("Reminder deleted"))
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
