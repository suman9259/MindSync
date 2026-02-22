package com.example.mindsync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MeditationTest {

    @Test
    fun `meditation creates with default values`() {
        val meditation = Meditation()
        
        assertNotNull(meditation.id)
        assertEquals("", meditation.userId)
        assertEquals("", meditation.title)
        assertEquals("", meditation.description)
        assertEquals(10, meditation.durationMinutes)
        assertEquals(MeditationCategory.MINDFULNESS, meditation.category)
        assertFalse(meditation.reminderEnabled)
        assertEquals(0, meditation.completedSessions)
    }

    @Test
    fun `meditation creates with custom values`() {
        val meditation = Meditation(
            id = "test-id",
            userId = "user-123",
            title = "Morning Meditation",
            description = "Start your day right",
            durationMinutes = 15,
            category = MeditationCategory.BREATHING,
            reminderEnabled = true,
            notes = "Focus on breath"
        )
        
        assertEquals("test-id", meditation.id)
        assertEquals("user-123", meditation.userId)
        assertEquals("Morning Meditation", meditation.title)
        assertEquals("Start your day right", meditation.description)
        assertEquals(15, meditation.durationMinutes)
        assertEquals(MeditationCategory.BREATHING, meditation.category)
        assertTrue(meditation.reminderEnabled)
        assertEquals("Focus on breath", meditation.notes)
    }

    @Test
    fun `meditationCategory has correct display names`() {
        assertEquals("Mindfulness", MeditationCategory.MINDFULNESS.displayName)
        assertEquals("Breathing", MeditationCategory.BREATHING.displayName)
        assertEquals("Sleep", MeditationCategory.SLEEP.displayName)
        assertEquals("Stress Relief", MeditationCategory.STRESS_RELIEF.displayName)
        assertEquals("Focus", MeditationCategory.FOCUS.displayName)
        assertEquals("Gratitude", MeditationCategory.GRATITUDE.displayName)
        assertEquals("Body Scan", MeditationCategory.BODY_SCAN.displayName)
        assertEquals("Guided", MeditationCategory.GUIDED.displayName)
    }

    @Test
    fun `meditationMood has correct display names and emojis`() {
        assertEquals("Very Calm", MeditationMood.VERY_CALM.displayName)
        assertEquals("😌", MeditationMood.VERY_CALM.emoji)
        assertEquals("Calm", MeditationMood.CALM.displayName)
        assertEquals("🙂", MeditationMood.CALM.emoji)
        assertEquals("Neutral", MeditationMood.NEUTRAL.displayName)
        assertEquals("😐", MeditationMood.NEUTRAL.emoji)
    }

    @Test
    fun `meditationSession creates correctly`() {
        val session = MeditationSession(
            id = "session-1",
            meditationId = "meditation-1",
            userId = "user-1",
            durationMinutes = 20,
            mood = MeditationMood.CALM,
            notes = "Great session"
        )
        
        assertEquals("session-1", session.id)
        assertEquals("meditation-1", session.meditationId)
        assertEquals("user-1", session.userId)
        assertEquals(20, session.durationMinutes)
        assertEquals(MeditationMood.CALM, session.mood)
        assertEquals("Great session", session.notes)
    }

    @Test
    fun `meditationReminder creates correctly`() {
        val reminder = MeditationReminder(
            id = "reminder-1",
            meditationId = "meditation-1",
            userId = "user-1",
            title = "Morning Meditation",
            scheduledTime = 1000L,
            repeatDays = listOf(1, 2, 3),
            isEnabled = true,
            notes = "Don't forget!"
        )
        
        assertEquals("reminder-1", reminder.id)
        assertEquals("meditation-1", reminder.meditationId)
        assertEquals("Morning Meditation", reminder.title)
        assertEquals(1000L, reminder.scheduledTime)
        assertEquals(listOf(1, 2, 3), reminder.repeatDays)
        assertTrue(reminder.isEnabled)
    }

    @Test
    fun `meditationStats creates with defaults`() {
        val stats = MeditationStats()
        
        assertEquals(0, stats.totalSessions)
        assertEquals(0, stats.totalMinutes)
        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.longestStreak)
        assertEquals(0f, stats.averageSessionDuration)
    }
}
