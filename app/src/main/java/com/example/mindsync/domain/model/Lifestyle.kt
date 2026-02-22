package com.example.mindsync.domain.model

import java.util.UUID

// Water Intake Tracking
data class WaterIntake(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val targetGlasses: Int = 8,
    val completedGlasses: Int = 0,
    val glassSize: Int = 250, // ml
    val logs: List<WaterLog> = emptyList(),
    val reminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 60
)

data class WaterLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val amount: Int = 250 // ml
)

// Bill Payment Reminders
data class BillReminder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val category: BillCategory = BillCategory.OTHER,
    val amount: Double = 0.0,
    val dueDate: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurringType: RecurringType = RecurringType.MONTHLY,
    val isPaid: Boolean = false,
    val paidDate: Long? = null,
    val reminderDaysBefore: Int = 3,
    val reminderEnabled: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class BillCategory(val displayName: String, val emoji: String) {
    ELECTRICITY("Electricity", "⚡"),
    WATER("Water", "💧"),
    GAS("Gas", "🔥"),
    INTERNET("Internet", "🌐"),
    PHONE("Phone", "📱"),
    RENT("Rent", "🏠"),
    INSURANCE("Insurance", "🛡️"),
    CREDIT_CARD("Credit Card", "💳"),
    LOAN("Loan", "🏦"),
    SUBSCRIPTION("Subscription", "📺"),
    OTHER("Other", "📄")
}

enum class RecurringType(val displayName: String) {
    WEEKLY("Weekly"),
    BIWEEKLY("Bi-weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly")
}

// Birthday & Anniversary Reminders
data class SpecialDate(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val personName: String = "",
    val dateType: SpecialDateType = SpecialDateType.BIRTHDAY,
    val date: Long = System.currentTimeMillis(), // Store as month/day
    val year: Int? = null, // Birth year for age calculation
    val reminderDaysBefore: Int = 1,
    val reminderEnabled: Boolean = true,
    val giftIdeas: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class SpecialDateType(val displayName: String, val emoji: String) {
    BIRTHDAY("Birthday", "🎂"),
    ANNIVERSARY("Anniversary", "💍"),
    WEDDING("Wedding", "💒"),
    GRADUATION("Graduation", "🎓"),
    OTHER("Other", "🎉")
}

// Sleep Tracking
data class SleepSchedule(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val bedTime: String = "22:00",
    val wakeTime: String = "06:00",
    val targetHours: Float = 8f,
    val bedtimeReminderEnabled: Boolean = true,
    val bedtimeReminderMinutesBefore: Int = 30,
    val wakeReminderEnabled: Boolean = true,
    val daysActive: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7) // 1=Mon, 7=Sun
)

data class SleepLog(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val bedTime: Long = 0,
    val wakeTime: Long = 0,
    val quality: SleepQuality = SleepQuality.GOOD,
    val notes: String = ""
)

enum class SleepQuality(val displayName: String, val emoji: String) {
    EXCELLENT("Excellent", "😴"),
    GOOD("Good", "🙂"),
    FAIR("Fair", "😐"),
    POOR("Poor", "😫"),
    TERRIBLE("Terrible", "😵")
}

// Vehicle Maintenance
data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val type: VehicleType = VehicleType.CAR,
    val make: String = "",
    val model: String = "",
    val year: Int = 2024,
    val licensePlate: String = "",
    val maintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val insuranceExpiry: Long? = null,
    val pollutionExpiry: Long? = null,
    val reminderEnabled: Boolean = true
)

enum class VehicleType(val displayName: String, val emoji: String) {
    CAR("Car", "🚗"),
    MOTORCYCLE("Motorcycle", "🏍️"),
    BICYCLE("Bicycle", "🚲"),
    SCOOTER("Scooter", "🛵"),
    TRUCK("Truck", "🚚")
}

data class MaintenanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: MaintenanceType = MaintenanceType.OIL_CHANGE,
    val date: Long = System.currentTimeMillis(),
    val nextDueDate: Long? = null,
    val mileage: Int = 0,
    val cost: Double = 0.0,
    val notes: String = ""
)

enum class MaintenanceType(val displayName: String, val emoji: String) {
    OIL_CHANGE("Oil Change", "🛢️"),
    TIRE_ROTATION("Tire Rotation", "🔄"),
    BRAKE_SERVICE("Brake Service", "🛑"),
    BATTERY("Battery", "🔋"),
    AIR_FILTER("Air Filter", "💨"),
    SERVICE("General Service", "🔧"),
    INSURANCE("Insurance Renewal", "📋"),
    POLLUTION_CHECK("Pollution Check", "🌿"),
    OTHER("Other", "🔩")
}

// Plant Care
data class Plant(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val species: String = "",
    val location: String = "",
    val wateringFrequencyDays: Int = 3,
    val lastWatered: Long? = null,
    val nextWatering: Long? = null,
    val fertilizerFrequencyDays: Int = 30,
    val lastFertilized: Long? = null,
    val notes: String = "",
    val reminderEnabled: Boolean = true
)
