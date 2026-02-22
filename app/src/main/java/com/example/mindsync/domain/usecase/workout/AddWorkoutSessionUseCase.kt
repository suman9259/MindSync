package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.WorkoutSession
import com.example.mindsync.domain.repository.WorkoutRepository

class AddWorkoutSessionUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(session: WorkoutSession): Result<WorkoutSession> {
        return repository.addWorkoutSession(session)
    }
}
