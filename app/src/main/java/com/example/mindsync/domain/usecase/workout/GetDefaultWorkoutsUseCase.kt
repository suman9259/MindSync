package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetDefaultWorkoutsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> {
        return repository.getDefaultWorkouts()
    }
}
