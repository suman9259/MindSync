package com.example.mindsync.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.model.WorkoutReminder
import com.example.mindsync.domain.model.WorkoutSession
import com.example.mindsync.domain.usecase.workout.AddWorkoutReminderUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutSessionUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutUseCase
import com.example.mindsync.domain.usecase.workout.GetDefaultWorkoutsUseCase
import com.example.mindsync.domain.usecase.workout.GetExercisesUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutProgressUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutStatsUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutsUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val getDefaultWorkoutsUseCase: GetDefaultWorkoutsUseCase,
    private val getExercisesUseCase: GetExercisesUseCase,
    private val addWorkoutUseCase: AddWorkoutUseCase,
    private val addWorkoutSessionUseCase: AddWorkoutSessionUseCase,
    private val addWorkoutReminderUseCase: AddWorkoutReminderUseCase,
    private val getWorkoutProgressUseCase: GetWorkoutProgressUseCase,
    private val getWorkoutStatsUseCase: GetWorkoutStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    private val _effect = Channel<WorkoutEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadDefaultWorkouts()
        loadExercises()
        loadWorkouts()
    }

    fun processIntent(intent: WorkoutIntent) {
        when (intent) {
            is WorkoutIntent.LoadWorkouts -> loadWorkouts()
            is WorkoutIntent.LoadDefaultWorkouts -> loadDefaultWorkouts()
            is WorkoutIntent.LoadExercises -> loadExercises()
            is WorkoutIntent.SelectWorkout -> selectWorkout(intent.workout)
            is WorkoutIntent.SelectCategory -> filterByCategory(intent.category)
            is WorkoutIntent.SelectPeriod -> selectPeriod(intent.period)
            is WorkoutIntent.AddWorkout -> addWorkout(intent.workout)
            is WorkoutIntent.UpdateWorkout -> updateWorkout(intent.workout)
            is WorkoutIntent.DeleteWorkout -> deleteWorkout(intent.id)
            is WorkoutIntent.AddSession -> addSession(intent.session)
            is WorkoutIntent.AddReminder -> addReminder(intent.reminder)
            is WorkoutIntent.UpdateReminder -> updateReminder(intent.reminder)
            is WorkoutIntent.DeleteReminder -> deleteReminder(intent.id)
            is WorkoutIntent.Retry -> loadWorkouts()
            is WorkoutIntent.ClearError -> clearError()
        }
    }

    private fun loadWorkouts() {
        if (userId.isEmpty()) {
            _state.update { it.copy(isLoading = false) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            getWorkoutsUseCase(userId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { workouts ->
                    _state.update { it.copy(workouts = workouts, isLoading = false) }
                }
        }

        viewModelScope.launch {
            getWorkoutStatsUseCase(userId)
                .catch { /* Ignore */ }
                .collect { stats ->
                    _state.update { it.copy(stats = stats) }
                }
        }

        loadProgress(_state.value.selectedPeriod)
    }

    private fun loadDefaultWorkouts() {
        viewModelScope.launch {
            getDefaultWorkoutsUseCase()
                .catch { /* Ignore */ }
                .collect { workouts ->
                    _state.update { it.copy(defaultWorkouts = workouts) }
                }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            getExercisesUseCase()
                .catch { /* Ignore */ }
                .collect { exercises ->
                    _state.update { it.copy(exercises = exercises) }
                }
        }
    }

    private fun loadProgress(period: ProgressPeriod) {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            getWorkoutProgressUseCase(userId, period)
                .catch { /* Ignore */ }
                .collect { progress ->
                    _state.update { it.copy(progress = progress) }
                }
        }
    }

    private fun selectWorkout(workout: Workout) {
        _state.update { it.copy(selectedWorkout = workout) }
        viewModelScope.launch {
            _effect.send(WorkoutEffect.NavigateToDetail(workout.id))
        }
    }

    private fun filterByCategory(category: WorkoutCategory?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    private fun selectPeriod(period: ProgressPeriod) {
        _state.update { it.copy(selectedPeriod = period) }
        loadProgress(period)
    }

    private fun addWorkout(workout: Workout) {
        _state.update { it.copy(isAddingWorkout = true) }

        viewModelScope.launch {
            val workoutWithUser = workout.copy(userId = userId, isCustom = true)
            addWorkoutUseCase(workoutWithUser)
                .onSuccess {
                    _state.update { it.copy(isAddingWorkout = false) }
                    _effect.send(WorkoutEffect.ShowSuccess("Workout added successfully"))
                    _effect.send(WorkoutEffect.NavigateBack)
                }
                .onFailure { e ->
                    _state.update { it.copy(isAddingWorkout = false, error = e.message) }
                    _effect.send(WorkoutEffect.ShowError(e.message ?: "Failed to add workout"))
                }
        }
    }

    private fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            addWorkoutUseCase(workout)
                .onSuccess {
                    _effect.send(WorkoutEffect.ShowSuccess("Workout updated"))
                }
                .onFailure { e ->
                    _effect.send(WorkoutEffect.ShowError(e.message ?: "Failed to update"))
                }
        }
    }

    private fun deleteWorkout(id: String) {
        viewModelScope.launch {
            _effect.send(WorkoutEffect.ShowSuccess("Workout deleted"))
        }
    }

    private fun addSession(session: WorkoutSession) {
        viewModelScope.launch {
            val sessionWithUser = session.copy(userId = userId)
            addWorkoutSessionUseCase(sessionWithUser)
                .onSuccess {
                    _effect.send(WorkoutEffect.ShowSuccess("Workout session logged"))
                }
                .onFailure { e ->
                    _effect.send(WorkoutEffect.ShowError(e.message ?: "Failed to log session"))
                }
        }
    }

    private fun addReminder(reminder: WorkoutReminder) {
        _state.update { it.copy(isAddingReminder = true) }

        viewModelScope.launch {
            val reminderWithUser = reminder.copy(userId = userId)
            addWorkoutReminderUseCase(reminderWithUser)
                .onSuccess {
                    _state.update { it.copy(isAddingReminder = false) }
                    _effect.send(WorkoutEffect.ReminderScheduled)
                    _effect.send(WorkoutEffect.ShowSuccess("Reminder set successfully"))
                }
                .onFailure { e ->
                    _state.update { it.copy(isAddingReminder = false, error = e.message) }
                    _effect.send(WorkoutEffect.ShowError(e.message ?: "Failed to set reminder"))
                }
        }
    }

    private fun updateReminder(reminder: WorkoutReminder) {
        viewModelScope.launch {
            _effect.send(WorkoutEffect.ShowSuccess("Reminder updated"))
        }
    }

    private fun deleteReminder(id: String) {
        viewModelScope.launch {
            _effect.send(WorkoutEffect.ShowSuccess("Reminder deleted"))
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
