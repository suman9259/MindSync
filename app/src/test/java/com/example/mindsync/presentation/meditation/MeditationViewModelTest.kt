package com.example.mindsync.presentation.meditation

import app.cash.turbine.test
import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.model.MeditationReminder
import com.example.mindsync.domain.model.MeditationStats
import com.example.mindsync.domain.usecase.meditation.AddMeditationReminderUseCase
import com.example.mindsync.domain.usecase.meditation.AddMeditationUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationStatsUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationsUseCase
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
class MeditationViewModelTest {

    private lateinit var getMeditationsUseCase: GetMeditationsUseCase
    private lateinit var addMeditationUseCase: AddMeditationUseCase
    private lateinit var addMeditationReminderUseCase: AddMeditationReminderUseCase
    private lateinit var getMeditationStatsUseCase: GetMeditationStatsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMeditationsUseCase = mockk()
        addMeditationUseCase = mockk()
        addMeditationReminderUseCase = mockk()
        getMeditationStatsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())

        val viewModel = createViewModel()

        val state = viewModel.state.value
        assertEquals(emptyList<Meditation>(), state.meditations)
        assertFalse(state.isAddingMeditation)
        assertFalse(state.isAddingReminder)
    }

    @Test
    fun `selectCategory updates state correctly`() = runTest {
        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(MeditationIntent.SelectCategory(MeditationCategory.MINDFULNESS))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(MeditationCategory.MINDFULNESS, viewModel.state.value.selectedCategory)
    }

    @Test
    fun `addMeditation success updates state and sends effect`() = runTest {
        val meditation = Meditation(
            title = "Test Meditation",
            category = MeditationCategory.MINDFULNESS,
            durationMinutes = 10
        )

        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())
        coEvery { addMeditationUseCase(any()) } returns Result.success(meditation)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(MeditationIntent.AddMeditation(meditation))
            testDispatcher.scheduler.advanceUntilIdle()

            val successEffect = awaitItem()
            assertTrue(successEffect is MeditationEffect.ShowSuccess)

            val navigateEffect = awaitItem()
            assertTrue(navigateEffect is MeditationEffect.NavigateBack)

            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.state.value.isAddingMeditation)
    }

    @Test
    fun `addMeditation failure updates state with error`() = runTest {
        val meditation = Meditation(
            title = "Test Meditation",
            category = MeditationCategory.MINDFULNESS,
            durationMinutes = 10
        )
        val error = Exception("Failed to add")

        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())
        coEvery { addMeditationUseCase(any()) } returns Result.failure(error)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(MeditationIntent.AddMeditation(meditation))
            testDispatcher.scheduler.advanceUntilIdle()

            val errorEffect = awaitItem()
            assertTrue(errorEffect is MeditationEffect.ShowError)
            assertEquals("Failed to add", (errorEffect as MeditationEffect.ShowError).message)

            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.state.value.isAddingMeditation)
    }

    @Test
    fun `addReminder success sends ReminderScheduled effect`() = runTest {
        val reminder = MeditationReminder(
            title = "Morning Meditation",
            scheduledTime = System.currentTimeMillis()
        )

        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())
        coEvery { addMeditationReminderUseCase(any()) } returns Result.success(reminder)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.processIntent(MeditationIntent.AddReminder(reminder))
            testDispatcher.scheduler.advanceUntilIdle()

            val scheduledEffect = awaitItem()
            assertTrue(scheduledEffect is MeditationEffect.ReminderScheduled)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        every { getMeditationsUseCase(any()) } returns flowOf(emptyList())
        every { getMeditationStatsUseCase(any()) } returns flowOf(MeditationStats())

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(MeditationIntent.ClearError)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, viewModel.state.value.error)
    }

    private fun createViewModel() = MeditationViewModel(
        getMeditationsUseCase = getMeditationsUseCase,
        addMeditationUseCase = addMeditationUseCase,
        addMeditationReminderUseCase = addMeditationReminderUseCase,
        getMeditationStatsUseCase = getMeditationStatsUseCase
    )
}
