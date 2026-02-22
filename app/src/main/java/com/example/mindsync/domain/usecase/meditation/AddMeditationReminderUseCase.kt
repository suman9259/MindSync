package com.example.mindsync.domain.usecase.meditation

import com.example.mindsync.domain.model.MeditationReminder
import com.example.mindsync.domain.repository.MeditationRepository

class AddMeditationReminderUseCase(
    private val repository: MeditationRepository
) {
    suspend operator fun invoke(reminder: MeditationReminder): Result<MeditationReminder> {
        return repository.addMeditationReminder(reminder)
    }
}
