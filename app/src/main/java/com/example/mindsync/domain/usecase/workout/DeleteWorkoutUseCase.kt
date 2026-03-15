package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.repository.WorkoutRepository

class DeleteWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteWorkout(id)
    }
}
