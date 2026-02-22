package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.WorkoutReminder
import com.example.mindsync.domain.repository.WorkoutRepository

class AddWorkoutReminderUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(reminder: WorkoutReminder): Result<WorkoutReminder> {
        return repository.addWorkoutReminder(reminder)
    }
}
