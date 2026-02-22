package com.example.mindsync.domain.usecase.meditation

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.Flow

class GetMeditationsUseCase(
    private val repository: MeditationRepository
) {
    operator fun invoke(userId: String): Flow<List<Meditation>> {
        return repository.getMeditations(userId)
    }
}
