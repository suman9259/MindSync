package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.repository.WorkoutRepository

class AddWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout): Result<Workout> {
        return repository.addWorkout(workout)
    }
}
