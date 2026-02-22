package com.example.mindsync.domain.model

import java.util.UUID

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val dosage: String = "",
    val unit: MedicineUnit = MedicineUnit.TABLET,
    val frequency: MedicineFrequency = MedicineFrequency.DAILY,
    val timesPerDay: Int = 1,
    val scheduledTimes: List<Long> = emptyList(),
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val instructions: String = "",
    val sideEffects: String = "",
    val imageUrl: String = "",
    val reminderEnabled: Boolean = true,
    val takenToday: Boolean = false,
    val takenCount: Int = 0,
    val missedCount: Int = 0,
    val currentStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class MedicineUnit(val displayName: String) {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    ML("ml"),
    MG("mg"),
    DROPS("Drops"),
    INJECTION("Injection"),
    CREAM("Cream"),
    SPRAY("Spray"),
    PATCH("Patch"),
    OTHER("Other")
}

enum class MedicineFrequency(val displayName: String) {
    ONCE("Once"),
    DAILY("Daily"),
    TWICE_DAILY("Twice Daily"),
    THREE_TIMES_DAILY("3 Times Daily"),
    EVERY_OTHER_DAY("Every Other Day"),
    WEEKLY("Weekly"),
    AS_NEEDED("As Needed")
}

data class MedicineLog(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String = "",
    val userId: String = "",
    val takenAt: Long = System.currentTimeMillis(),
    val scheduledTime: Long = 0,
    val status: MedicineLogStatus = MedicineLogStatus.TAKEN,
    val notes: String = ""
)

enum class MedicineLogStatus(val displayName: String) {
    TAKEN("Taken"),
    MISSED("Missed"),
    SKIPPED("Skipped"),
    DELAYED("Delayed")
}

data class MedicineReminder(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String = "",
    val userId: String = "",
    val title: String = "",
    val scheduledTime: Long = 0,
    val repeatType: RepeatType = RepeatType.DAILY,
    val isEnabled: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class MedicineStats(
    val totalMedicines: Int = 0,
    val activeMedicines: Int = 0,
    val takenToday: Int = 0,
    val missedToday: Int = 0,
    val adherenceRate: Float = 0f,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)
