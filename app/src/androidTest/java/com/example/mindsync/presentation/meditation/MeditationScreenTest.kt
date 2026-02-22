package com.example.mindsync.presentation.meditation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.model.MeditationStats
import com.example.mindsync.domain.usecase.meditation.AddMeditationReminderUseCase
import com.example.mindsync.domain.usecase.meditation.AddMeditationUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationStatsUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationsUseCase
import com.example.mindsync.presentation.theme.MindSyncTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MeditationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getMeditationsUseCase: GetMeditationsUseCase
    private lateinit var addMeditationUseCase: AddMeditationUseCase
    private lateinit var addMeditationReminderUseCase: AddMeditationReminderUseCase
    private lateinit var getMeditationStatsUseCase: GetMeditationStatsUseCase
    private lateinit var viewModel: MeditationViewModel

    @Before
    fun setup() {
        getMeditationsUseCase = mockk()
        addMeditationUseCase = mockk()
        addMeditationReminderUseCase = mockk()
        getMeditationStatsUseCase = mockk()

        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())

        viewModel = MeditationViewModel(
            getMeditationsUseCase = getMeditationsUseCase,
            addMeditationUseCase = addMeditationUseCase,
            addMeditationReminderUseCase = addMeditationReminderUseCase,
            getMeditationStatsUseCase = getMeditationStatsUseCase
        )
    }

    @Test
    fun meditationScreen_displaysHeader() {
        composeTestRule.setContent {
            MindSyncTheme {
                MeditationScreen(
                    onNavigateBack = {},
                    onNavigateToAddMeditation = {},
                    onNavigateToAddReminder = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Find Your Peace").assertIsDisplayed()
    }

    @Test
    fun meditationScreen_displaysEmptyState() {
        composeTestRule.setContent {
            MindSyncTheme {
                MeditationScreen(
                    onNavigateBack = {},
                    onNavigateToAddMeditation = {},
                    onNavigateToAddReminder = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("No meditations yet").assertIsDisplayed()
    }

    @Test
    fun meditationScreen_displaysMeditations() {
        val meditations = listOf(
            Meditation(
                id = "1",
                title = "Morning Meditation",
                category = MeditationCategory.MINDFULNESS,
                durationMinutes = 10
            )
        )

        every { getMeditationsUseCase(any()) } returns flowOf(meditations)

        viewModel = MeditationViewModel(
            getMeditationsUseCase = getMeditationsUseCase,
            addMeditationUseCase = addMeditationUseCase,
            addMeditationReminderUseCase = addMeditationReminderUseCase,
            getMeditationStatsUseCase = getMeditationStatsUseCase
        )

        composeTestRule.setContent {
            MindSyncTheme {
                MeditationScreen(
                    onNavigateBack = {},
                    onNavigateToAddMeditation = {},
                    onNavigateToAddReminder = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Morning Meditation").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 min").assertIsDisplayed()
    }

    @Test
    fun meditationScreen_categoryFilterWorks() {
        composeTestRule.setContent {
            MindSyncTheme {
                MeditationScreen(
                    onNavigateBack = {},
                    onNavigateToAddMeditation = {},
                    onNavigateToAddReminder = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mindfulness").assertIsDisplayed()
        composeTestRule.onNodeWithText("Breathing").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Mindfulness").performClick()
    }

    @Test
    fun meditationScreen_statsAreDisplayed() {
        val stats = MeditationStats(
            totalSessions = 10,
            totalMinutes = 150,
            currentStreak = 5
        )

        every { getMeditationStatsUseCase(any()) } returns flowOf(stats)

        viewModel = MeditationViewModel(
            getMeditationsUseCase = getMeditationsUseCase,
            addMeditationUseCase = addMeditationUseCase,
            addMeditationReminderUseCase = addMeditationReminderUseCase,
            getMeditationStatsUseCase = getMeditationStatsUseCase
        )

        composeTestRule.setContent {
            MindSyncTheme {
                MeditationScreen(
                    onNavigateBack = {},
                    onNavigateToAddMeditation = {},
                    onNavigateToAddReminder = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("150").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }
}
