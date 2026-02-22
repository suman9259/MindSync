package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.repository.WorkoutRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWorkoutsUseCaseTest {

    private lateinit var repository: WorkoutRepository
    private lateinit var useCase: GetWorkoutsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetWorkoutsUseCase(repository)
    }

    @Test
    fun `invoke returns workouts from repository`() = runTest {
        val userId = "test-user-id"
        val expectedWorkouts = listOf(
            Workout(
                id = "1",
                userId = userId,
                name = "Chest Day",
                category = WorkoutCategory.STRENGTH,
                durationMinutes = 45
            ),
            Workout(
                id = "2",
                userId = userId,
                name = "HIIT Cardio",
                category = WorkoutCategory.HIIT,
                durationMinutes = 30
            )
        )

        every { repository.getWorkouts(userId) } returns flowOf(expectedWorkouts)

        val result = useCase(userId).first()

        assertEquals(expectedWorkouts, result)
        verify(exactly = 1) { repository.getWorkouts(userId) }
    }

    @Test
    fun `invoke returns empty list when no workouts exist`() = runTest {
        val userId = "test-user-id"

        every { repository.getWorkouts(userId) } returns flowOf(emptyList())

        val result = useCase(userId).first()

        assertEquals(emptyList<Workout>(), result)
        verify(exactly = 1) { repository.getWorkouts(userId) }
    }
}
