package com.example.mindsync.domain.model

import java.util.UUID

data class Meditation(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val durationMinutes: Int = 10,
    val category: MeditationCategory = MeditationCategory.MINDFULNESS,
    val imageUrl: String = "",
    val audioUrl: String = "",
    val reminderTime: Long? = null,
    val reminderEnabled: Boolean = false,
    val notes: String = "",
    val completedSessions: Int = 0,
    val totalMinutesMeditated: Int = 0,
    val lastSessionDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class MeditationCategory(val displayName: String, val iconName: String) {
    MINDFULNESS("Mindfulness", "self_improvement"),
    BREATHING("Breathing", "air"),
    SLEEP("Sleep", "bedtime"),
    STRESS_RELIEF("Stress Relief", "spa"),
    FOCUS("Focus", "center_focus_strong"),
    GRATITUDE("Gratitude", "favorite"),
    BODY_SCAN("Body Scan", "accessibility"),
    GUIDED("Guided", "headphones")
}

data class MeditationSession(
    val id: String = UUID.randomUUID().toString(),
    val meditationId: String = "",
    val userId: String = "",
    val durationMinutes: Int = 0,
    val completedAt: Long = System.currentTimeMillis(),
    val mood: MeditationMood? = null,
    val notes: String = ""
)

enum class MeditationMood(val displayName: String, val emoji: String) {
    VERY_CALM("Very Calm", "😌"),
    CALM("Calm", "🙂"),
    NEUTRAL("Neutral", "😐"),
    STRESSED("Stressed", "😟"),
    VERY_STRESSED("Very Stressed", "😰")
}

data class MeditationReminder(
    val id: String = UUID.randomUUID().toString(),
    val meditationId: String = "",
    val userId: String = "",
    val title: String = "",
    val scheduledTime: Long = 0,
    val repeatDays: List<Int> = emptyList(),
    val isEnabled: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class MeditationStats(
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val averageSessionDuration: Float = 0f,
    val favoriteCategory: MeditationCategory? = null,
    val weeklyMinutes: Map<Int, Int> = emptyMap(),
    val monthlyMinutes: Map<Int, Int> = emptyMap()
)
