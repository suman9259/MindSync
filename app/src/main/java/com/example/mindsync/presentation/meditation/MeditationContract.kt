package com.example.mindsync.presentation.meditation

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.model.MeditationReminder
import com.example.mindsync.domain.model.MeditationStats
import com.example.mindsync.presentation.base.MviEffect
import com.example.mindsync.presentation.base.MviIntent
import com.example.mindsync.presentation.base.MviState

data class MeditationState(
    val meditations: List<Meditation> = emptyList(),
    val reminders: List<MeditationReminder> = emptyList(),
    val stats: MeditationStats = MeditationStats(),
    val selectedMeditation: Meditation? = null,
    val selectedCategory: MeditationCategory? = null,
    val isLoading: Boolean = false,
    val isAddingMeditation: Boolean = false,
    val isAddingReminder: Boolean = false,
    val error: String? = null
) : MviState

sealed class MeditationIntent : MviIntent {
    data object LoadMeditations : MeditationIntent()
    data class SelectMeditation(val meditation: Meditation) : MeditationIntent()
    data class SelectCategory(val category: MeditationCategory?) : MeditationIntent()
    data class AddMeditation(val meditation: Meditation) : MeditationIntent()
    data class UpdateMeditation(val meditation: Meditation) : MeditationIntent()
    data class DeleteMeditation(val id: String) : MeditationIntent()
    data class AddReminder(val reminder: MeditationReminder) : MeditationIntent()
    data class UpdateReminder(val reminder: MeditationReminder) : MeditationIntent()
    data class DeleteReminder(val id: String) : MeditationIntent()
    data object Retry : MeditationIntent()
    data object ClearError : MeditationIntent()
}

sealed class MeditationEffect : MviEffect {
    data class ShowError(val message: String) : MeditationEffect()
    data class ShowSuccess(val message: String) : MeditationEffect()
    data object NavigateBack : MeditationEffect()
    data class NavigateToDetail(val meditationId: String) : MeditationEffect()
    data object ReminderScheduled : MeditationEffect()
}
