package com.example.mindsync.domain.model

import java.util.UUID

data class Assignment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val description: String = "",
    val dueDate: Long = System.currentTimeMillis(),
    val priority: AssignmentPriority = AssignmentPriority.MEDIUM,
    val status: AssignmentStatus = AssignmentStatus.PENDING,
    val reminderEnabled: Boolean = true,
    val reminderTime: Long? = null,
    val attachments: List<String> = emptyList(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AssignmentPriority(val displayName: String, val emoji: String) {
    LOW("Low", "🟢"),
    MEDIUM("Medium", "🟡"),
    HIGH("High", "🟠"),
    URGENT("Urgent", "🔴")
}

enum class AssignmentStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    SUBMITTED("Submitted"),
    OVERDUE("Overdue")
}

data class TestRevision(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val subject: String = "",
    val topic: String = "",
    val examDate: Long = System.currentTimeMillis(),
    val revisionSessions: List<RevisionSession> = emptyList(),
    val totalStudyMinutes: Int = 0,
    val confidence: Int = 0, // 0-100
    val notes: String = "",
    val reminderEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class RevisionSession(
    val id: String = UUID.randomUUID().toString(),
    val scheduledDate: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 30,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val notes: String = ""
)

data class ClassSchedule(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val subject: String = "",
    val teacher: String = "",
    val room: String = "",
    val dayOfWeek: Int = 1, // 1 = Monday, 7 = Sunday
    val startTime: String = "09:00",
    val endTime: String = "10:00",
    val reminderMinutesBefore: Int = 15,
    val color: Long = 0xFF6366F1
)

enum class Subject(val displayName: String, val emoji: String) {
    MATH("Mathematics", "📐"),
    SCIENCE("Science", "🔬"),
    ENGLISH("English", "📚"),
    HISTORY("History", "🏛️"),
    GEOGRAPHY("Geography", "🌍"),
    PHYSICS("Physics", "⚛️"),
    CHEMISTRY("Chemistry", "🧪"),
    BIOLOGY("Biology", "🧬"),
    COMPUTER_SCIENCE("Computer Science", "💻"),
    ART("Art", "🎨"),
    MUSIC("Music", "🎵"),
    PHYSICAL_EDUCATION("Physical Education", "🏃"),
    ECONOMICS("Economics", "📈"),
    PSYCHOLOGY("Psychology", "🧠"),
    OTHER("Other", "📖")
}
