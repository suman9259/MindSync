package com.example.mindsync.domain.usecase.meditation

import com.example.mindsync.domain.model.MeditationStats
import com.example.mindsync.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.Flow

class GetMeditationStatsUseCase(
    private val repository: MeditationRepository
) {
    operator fun invoke(userId: String): Flow<MeditationStats> {
        return repository.getMeditationStats(userId)
    }
}
