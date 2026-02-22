package com.example.mindsync.presentation.workout

import app.cash.turbine.test
import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.MuscleGroup
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.model.WorkoutProgress
import com.example.mindsync.domain.model.WorkoutReminder
import com.example.mindsync.domain.model.WorkoutStats
import com.example.mindsync.domain.usecase.workout.AddWorkoutReminderUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutSessionUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutUseCase
import com.example.mindsync.domain.usecase.workout.GetDefaultWorkoutsUseCase
import com.example.mindsync.domain.usecase.workout.GetExercisesUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutProgressUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutStatsUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModelTest {

    private lateinit var getWorkoutsUseCase: GetWorkoutsUseCase
    private lateinit var getDefaultWorkoutsUseCase: GetDefaultWorkoutsUseCase
    private lateinit var getExercisesUseCase: GetExercisesUseCase
    private lateinit var addWorkoutUseCase: AddWorkoutUseCase
    private lateinit var addWorkoutSessionUseCase: AddWorkoutSessionUseCase
    private lateinit var addWorkoutReminderUseCase: AddWorkoutReminderUseCase
    private lateinit var getWorkoutProgressUseCase: GetWorkoutProgressUseCase
    private lateinit var getWorkoutStatsUseCase: GetWorkoutStatsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getWorkoutsUseCase = mockk()
        getDefaultWorkoutsUseCase = mockk()
        getExercisesUseCase = mockk()
        addWorkoutUseCase = mockk()
        addWorkoutSessionUseCase = mockk()
        addWorkoutReminderUseCase = mockk()
        getWorkoutProgressUseCase = mockk()
        getWorkoutStatsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        setupDefaultMocks()

        val viewModel = createViewModel()

        val state = viewModel.state.value
        assertEquals(emptyList<Workout>(), state.workouts)
        assertEquals(ProgressPeriod.WEEKLY, state.selectedPeriod)
        assertFalse(state.isAddingWorkout)
    }

    @Test
    fun `selectCategory updates state correctly`() = runTest {
        setupDefaultMocks()

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(WorkoutIntent.SelectCategory(WorkoutCategory.STRENGTH))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(WorkoutCategory.STRENGTH, viewModel.state.value.selectedCategory)
    }

    @Test
    fun `selectPeriod updates state and loads progress`() = runTest {
        setupDefaultMocks()

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(WorkoutIntent.SelectPeriod(ProgressPeriod.MONTHLY))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ProgressPeriod.MONTHLY, viewModel.state.value.selectedPeriod)
    }

    @Test
    fun `addWorkout success updates state and sends effects`() = runTest {
        val workout = Workout(
            name = "Test Workout",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 4, reps = 10)
            ),
            durationMinutes = 45
        )

        setupDefaultMocks()
        coEvery { addWorkoutUseCase(any()) } returns Result.success(workout)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(WorkoutIntent.AddWorkout(workout))
            testDispatcher.scheduler.advanceUntilIdle()

            val successEffect = awaitItem()
            assertTrue(successEffect is WorkoutEffect.ShowSuccess)

            val navigateEffect = awaitItem()
            assertTrue(navigateEffect is WorkoutEffect.NavigateBack)

            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.state.value.isAddingWorkout)
    }

    @Test
    fun `addWorkout failure updates state with error`() = runTest {
        val workout = Workout(
            name = "Test Workout",
            category = WorkoutCategory.STRENGTH,
            durationMinutes = 45
        )
        val error = Exception("Failed to add workout")

        setupDefaultMocks()
        coEvery { addWorkoutUseCase(any()) } returns Result.failure(error)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(WorkoutIntent.AddWorkout(workout))
            testDispatcher.scheduler.advanceUntilIdle()

            val errorEffect = awaitItem()
            assertTrue(errorEffect is WorkoutEffect.ShowError)
            assertEquals("Failed to add workout", (errorEffect as WorkoutEffect.ShowError).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addReminder success sends ReminderScheduled effect`() = runTest {
        val reminder = WorkoutReminder(
            title = "Gym Time",
            scheduledTime = System.currentTimeMillis()
        )

        setupDefaultMocks()
        coEvery { addWorkoutReminderUseCase(any()) } returns Result.success(reminder)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(WorkoutIntent.AddReminder(reminder))
            testDispatcher.scheduler.advanceUntilIdle()

            val scheduledEffect = awaitItem()
            assertTrue(scheduledEffect is WorkoutEffect.ReminderScheduled)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadDefaultWorkouts populates defaultWorkouts`() = runTest {
        val defaultWorkouts = listOf(
            Workout(id = "1", name = "Chest Day", category = WorkoutCategory.STRENGTH),
            Workout(id = "2", name = "HIIT", category = WorkoutCategory.HIIT)
        )

        every { getWorkoutsUseCase(any()) } returns flowOf(emptyList())
        every { getDefaultWorkoutsUseCase() } returns flowOf(defaultWorkouts)
        every { getExercisesUseCase() } returns flowOf(emptyList())
        every { getWorkoutStatsUseCase(any()) } returns flowOf(WorkoutStats())
        every { getWorkoutProgressUseCase(any(), any()) } returns flowOf(WorkoutProgress())

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(defaultWorkouts, viewModel.state.value.defaultWorkouts)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        setupDefaultMocks()

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(WorkoutIntent.ClearError)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, viewModel.state.value.error)
    }

    private fun setupDefaultMocks() {
        every { getWorkoutsUseCase(any()) } returns flowOf(emptyList())
        every { getDefaultWorkoutsUseCase() } returns flowOf(emptyList())
        every { getExercisesUseCase() } returns flowOf(emptyList())
        every { getWorkoutStatsUseCase(any()) } returns flowOf(WorkoutStats())
        every { getWorkoutProgressUseCase(any(), any()) } returns flowOf(WorkoutProgress())
    }

    private fun createViewModel() = WorkoutViewModel(
        getWorkoutsUseCase = getWorkoutsUseCase,
        getDefaultWorkoutsUseCase = getDefaultWorkoutsUseCase,
        getExercisesUseCase = getExercisesUseCase,
        addWorkoutUseCase = addWorkoutUseCase,
        addWorkoutSessionUseCase = addWorkoutSessionUseCase,
        addWorkoutReminderUseCase = addWorkoutReminderUseCase,
        getWorkoutProgressUseCase = getWorkoutProgressUseCase,
        getWorkoutStatsUseCase = getWorkoutStatsUseCase
    )
}
