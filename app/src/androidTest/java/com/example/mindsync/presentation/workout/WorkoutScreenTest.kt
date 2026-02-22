package com.example.mindsync.presentation.workout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.MuscleGroup
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.model.WorkoutProgress
import com.example.mindsync.domain.model.WorkoutStats
import com.example.mindsync.domain.usecase.workout.AddWorkoutReminderUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutSessionUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutUseCase
import com.example.mindsync.domain.usecase.workout.GetDefaultWorkoutsUseCase
import com.example.mindsync.domain.usecase.workout.GetExercisesUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutProgressUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutStatsUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutsUseCase
import com.example.mindsync.presentation.theme.MindSyncTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WorkoutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getWorkoutsUseCase: GetWorkoutsUseCase
    private lateinit var getDefaultWorkoutsUseCase: GetDefaultWorkoutsUseCase
    private lateinit var getExercisesUseCase: GetExercisesUseCase
    private lateinit var addWorkoutUseCase: AddWorkoutUseCase
    private lateinit var addWorkoutSessionUseCase: AddWorkoutSessionUseCase
    private lateinit var addWorkoutReminderUseCase: AddWorkoutReminderUseCase
    private lateinit var getWorkoutProgressUseCase: GetWorkoutProgressUseCase
    private lateinit var getWorkoutStatsUseCase: GetWorkoutStatsUseCase
    private lateinit var viewModel: WorkoutViewModel

    @Before
    fun setup() {
        getWorkoutsUseCase = mockk()
        getDefaultWorkoutsUseCase = mockk()
        getExercisesUseCase = mockk()
        addWorkoutUseCase = mockk()
        addWorkoutSessionUseCase = mockk()
        addWorkoutReminderUseCase = mockk()
        getWorkoutProgressUseCase = mockk()
        getWorkoutStatsUseCase = mockk()

        every { getWorkoutsUseCase(any()) } returns flowOf(emptyList())
        every { getDefaultWorkoutsUseCase() } returns flowOf(emptyList())
        every { getExercisesUseCase() } returns flowOf(emptyList())
        every { getWorkoutStatsUseCase(any()) } returns flowOf(WorkoutStats())
        every { getWorkoutProgressUseCase(any(), any()) } returns flowOf(WorkoutProgress())

        viewModel = createViewModel()
    }

    @Test
    fun workoutScreen_displaysHeader() {
        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Let's Get Moving! 💪").assertIsDisplayed()
    }

    @Test
    fun workoutScreen_displaysTabs() {
        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Workouts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress").assertIsDisplayed()
    }

    @Test
    fun workoutScreen_switchToProgressTab() {
        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.onNodeWithText("Weekly").assertIsDisplayed()
        composeTestRule.onNodeWithText("Monthly").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yearly").assertIsDisplayed()
    }

    @Test
    fun workoutScreen_displaysDefaultWorkouts() {
        val defaultWorkouts = listOf(
            Workout(
                id = "1",
                name = "Chest Day",
                category = WorkoutCategory.STRENGTH,
                durationMinutes = 45,
                exercises = listOf(
                    Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST)
                )
            )
        )

        every { getDefaultWorkoutsUseCase() } returns flowOf(defaultWorkouts)

        viewModel = createViewModel()

        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Chest Day").assertIsDisplayed()
    }

    @Test
    fun workoutScreen_displaysCustomWorkouts() {
        val customWorkouts = listOf(
            Workout(
                id = "1",
                userId = "test-user",
                name = "My Custom Workout",
                category = WorkoutCategory.STRENGTH,
                durationMinutes = 30,
                isCustom = true
            )
        )

        every { getWorkoutsUseCase(any()) } returns flowOf(customWorkouts)

        viewModel = createViewModel()

        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("My Custom Workout").assertIsDisplayed()
    }

    @Test
    fun workoutScreen_categoryFilterWorks() {
        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Strength").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Strength").performClick()
    }

    @Test
    fun workoutScreen_progressTabDisplaysStats() {
        val stats = WorkoutStats(
            totalWorkouts = 20,
            totalMinutes = 600,
            totalCalories = 5000,
            currentStreak = 7
        )

        every { getWorkoutStatsUseCase(any()) } returns flowOf(stats)

        viewModel = createViewModel()

        composeTestRule.setContent {
            MindSyncTheme {
                WorkoutScreen(
                    onNavigateBack = {},
                    onNavigateToAddWorkout = {},
                    onNavigateToAddReminder = {},
                    onNavigateToProgress = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.onNodeWithText("🔥 Current Streak").assertIsDisplayed()
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
