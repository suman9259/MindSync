package com.example.mindsync.domain.usecase.meditation

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.repository.MeditationRepository

class AddMeditationUseCase(
    private val repository: MeditationRepository
) {
    suspend operator fun invoke(meditation: Meditation): Result<Meditation> {
        return repository.addMeditation(meditation)
    }
}
