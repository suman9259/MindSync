package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.WorkoutProgress
import com.example.mindsync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutProgressUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(userId: String, period: ProgressPeriod): Flow<WorkoutProgress> {
        return repository.getWorkoutProgress(userId, period)
    }
}
