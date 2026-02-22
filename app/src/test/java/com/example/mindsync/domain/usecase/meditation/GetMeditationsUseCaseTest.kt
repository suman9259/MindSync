package com.example.mindsync.domain.usecase.meditation

import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.repository.MeditationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMeditationsUseCaseTest {

    private lateinit var repository: MeditationRepository
    private lateinit var useCase: GetMeditationsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetMeditationsUseCase(repository)
    }

    @Test
    fun `invoke returns meditations from repository`() = runTest {
        val userId = "test-user-id"
        val expectedMeditations = listOf(
            Meditation(
                id = "1",
                userId = userId,
                title = "Morning Meditation",
                category = MeditationCategory.MINDFULNESS,
                durationMinutes = 10
            ),
            Meditation(
                id = "2",
                userId = userId,
                title = "Evening Relaxation",
                category = MeditationCategory.SLEEP,
                durationMinutes = 15
            )
        )

        every { repository.getMeditations(userId) } returns flowOf(expectedMeditations)

        val result = useCase(userId).first()

        assertEquals(expectedMeditations, result)
        verify(exactly = 1) { repository.getMeditations(userId) }
    }

    @Test
    fun `invoke returns empty list when no meditations exist`() = runTest {
        val userId = "test-user-id"

        every { repository.getMeditations(userId) } returns flowOf(emptyList())

        val result = useCase(userId).first()

        assertEquals(emptyList<Meditation>(), result)
        verify(exactly = 1) { repository.getMeditations(userId) }
    }
}
