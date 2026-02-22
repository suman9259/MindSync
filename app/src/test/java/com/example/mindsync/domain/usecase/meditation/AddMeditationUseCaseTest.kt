package com.example.mindsync.domain.usecase.meditation

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.repository.MeditationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddMeditationUseCaseTest {

    private lateinit var repository: MeditationRepository
    private lateinit var useCase: AddMeditationUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddMeditationUseCase(repository)
    }

    @Test
    fun `invoke successfully adds meditation`() = runTest {
        val meditation = Meditation(
            id = "1",
            userId = "test-user",
            title = "Morning Meditation",
            category = MeditationCategory.MINDFULNESS,
            durationMinutes = 10
        )

        coEvery { repository.addMeditation(meditation) } returns Result.success(meditation)

        val result = useCase(meditation)

        assertTrue(result.isSuccess)
        assertEquals(meditation, result.getOrNull())
        coVerify(exactly = 1) { repository.addMeditation(meditation) }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val meditation = Meditation(
            id = "1",
            userId = "test-user",
            title = "Morning Meditation",
            category = MeditationCategory.MINDFULNESS,
            durationMinutes = 10
        )
        val exception = Exception("Failed to add meditation")

        coEvery { repository.addMeditation(meditation) } returns Result.failure(exception)

        val result = useCase(meditation)

        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.addMeditation(meditation) }
    }
}
