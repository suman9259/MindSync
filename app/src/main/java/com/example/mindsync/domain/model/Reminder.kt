package com.example.mindsync.domain.model

import java.util.UUID

data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val type: ReminderType = ReminderType.MEDITATION,
    val referenceId: String = "",
    val scheduledTime: Long = 0,
    val repeatType: RepeatType = RepeatType.ONCE,
    val repeatDays: List<DayOfWeek> = emptyList(),
    val isEnabled: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ReminderType(val displayName: String) {
    MEDITATION("Meditation"),
    WORKOUT("Workout"),
    GENERAL("General")
}

enum class RepeatType(val displayName: String) {
    ONCE("Once"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    CUSTOM("Custom")
}

enum class DayOfWeek(val shortName: String, val fullName: String, val value: Int) {
    MONDAY("Mon", "Monday", 1),
    TUESDAY("Tue", "Tuesday", 2),
    WEDNESDAY("Wed", "Wednesday", 3),
    THURSDAY("Thu", "Thursday", 4),
    FRIDAY("Fri", "Friday", 5),
    SATURDAY("Sat", "Saturday", 6),
    SUNDAY("Sun", "Sunday", 7)
}
