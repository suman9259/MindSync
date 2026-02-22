package com.example.mindsync.domain.model

import java.util.UUID

data class SkincareRoutine(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val routineType: SkincareRoutineType = SkincareRoutineType.MORNING,
    val steps: List<SkincareStep> = emptyList(),
    val scheduledTime: Long = 0,
    val estimatedMinutes: Int = 15,
    val reminderEnabled: Boolean = true,
    val completedToday: Boolean = false,
    val completedCount: Int = 0,
    val currentStreak: Int = 0,
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class SkincareRoutineType(val displayName: String, val emoji: String) {
    MORNING("Morning", "🌅"),
    EVENING("Evening", "🌙"),
    WEEKLY("Weekly", "📅"),
    TREATMENT("Treatment", "💆")
}

data class SkincareStep(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val productName: String = "",
    val productBrand: String = "",
    val category: SkincareCategory = SkincareCategory.CLEANSER,
    val instructions: String = "",
    val durationSeconds: Int = 60,
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val imageUrl: String = ""
)

enum class SkincareCategory(val displayName: String, val emoji: String) {
    CLEANSER("Cleanser", "🧼"),
    TONER("Toner", "💧"),
    SERUM("Serum", "✨"),
    MOISTURIZER("Moisturizer", "🧴"),
    SUNSCREEN("Sunscreen", "☀️"),
    EYE_CREAM("Eye Cream", "👁️"),
    FACE_MASK("Face Mask", "🎭"),
    EXFOLIATOR("Exfoliator", "🌟"),
    TREATMENT("Treatment", "💊"),
    FACE_OIL("Face Oil", "🫒"),
    LIP_CARE("Lip Care", "💋"),
    OTHER("Other", "📦")
}

data class SkincareLog(
    val id: String = UUID.randomUUID().toString(),
    val routineId: String = "",
    val userId: String = "",
    val completedAt: Long = System.currentTimeMillis(),
    val completedSteps: List<String> = emptyList(),
    val skippedSteps: List<String> = emptyList(),
    val skinCondition: SkinCondition = SkinCondition.NORMAL,
    val notes: String = ""
)

enum class SkinCondition(val displayName: String, val emoji: String) {
    GREAT("Great", "😊"),
    GOOD("Good", "🙂"),
    NORMAL("Normal", "😐"),
    DRY("Dry", "🏜️"),
    OILY("Oily", "💦"),
    IRRITATED("Irritated", "😣"),
    BREAKOUT("Breakout", "😰")
}

data class SkincareReminder(
    val id: String = UUID.randomUUID().toString(),
    val routineId: String = "",
    val userId: String = "",
    val title: String = "",
    val scheduledTime: Long = 0,
    val repeatType: RepeatType = RepeatType.DAILY,
    val isEnabled: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class SkincareStats(
    val totalRoutines: Int = 0,
    val completedToday: Int = 0,
    val pendingToday: Int = 0,
    val weeklyCompletionRate: Float = 0f,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val favoriteProducts: List<String> = emptyList()
)
