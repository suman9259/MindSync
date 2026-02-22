package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(userId: String): Flow<List<Workout>> {
        return repository.getWorkouts(userId)
    }
}
