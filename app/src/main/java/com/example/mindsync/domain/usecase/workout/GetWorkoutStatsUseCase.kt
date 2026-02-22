package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.WorkoutStats
import com.example.mindsync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutStatsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(userId: String): Flow<WorkoutStats> {
        return repository.getWorkoutStats(userId)
    }
}
