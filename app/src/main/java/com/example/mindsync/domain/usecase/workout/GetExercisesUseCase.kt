package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetExercisesUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Exercise>> {
        return repository.getExercises()
    }
}
