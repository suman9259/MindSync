package com.example.mindsync.domain.usecase.workout

import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.MuscleGroup
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddWorkoutUseCaseTest {

    private lateinit var repository: WorkoutRepository
    private lateinit var useCase: AddWorkoutUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddWorkoutUseCase(repository)
    }

    @Test
    fun `invoke successfully adds workout`() = runTest {
        val workout = Workout(
            id = "1",
            userId = "test-user",
            name = "Chest Day",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(
                    name = "Bench Press",
                    muscleGroup = MuscleGroup.CHEST,
                    sets = 4,
                    reps = 10,
                    weight = 60f
                )
            ),
            durationMinutes = 45,
            isCustom = true
        )

        coEvery { repository.addWorkout(workout) } returns Result.success(workout)

        val result = useCase(workout)

        assertTrue(result.isSuccess)
        assertEquals(workout, result.getOrNull())
        coVerify(exactly = 1) { repository.addWorkout(workout) }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val workout = Workout(
            id = "1",
            userId = "test-user",
            name = "Chest Day",
            category = WorkoutCategory.STRENGTH,
            durationMinutes = 45
        )
        val exception = Exception("Failed to add workout")

        coEvery { repository.addWorkout(workout) } returns Result.failure(exception)

        val result = useCase(workout)

        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.addWorkout(workout) }
    }
}
