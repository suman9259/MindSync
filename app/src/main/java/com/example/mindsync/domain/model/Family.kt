package com.example.mindsync.domain.model

import java.util.UUID

data class FamilyMember(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val relationship: FamilyRelationship = FamilyRelationship.OTHER,
    val age: Int? = null,
    val phoneNumber: String = "",
    val emergencyContact: Boolean = false,
    val medicines: List<FamilyMedicine> = emptyList(),
    val notes: String = "",
    val avatarEmoji: String = "👤",
    val createdAt: Long = System.currentTimeMillis()
)

enum class FamilyRelationship(val displayName: String, val emoji: String) {
    FATHER("Father", "👨"),
    MOTHER("Mother", "👩"),
    GRANDFATHER("Grandfather", "👴"),
    GRANDMOTHER("Grandmother", "👵"),
    SPOUSE("Spouse", "💑"),
    CHILD("Child", "👶"),
    SIBLING("Sibling", "👫"),
    OTHER("Other", "👤")
}

data class FamilyMedicine(
    val id: String = UUID.randomUUID().toString(),
    val familyMemberId: String = "",
    val name: String = "",
    val dosage: String = "",
    val frequency: MedicineFrequency = MedicineFrequency.DAILY,
    val scheduledTimes: List<String> = emptyList(), // e.g., ["08:00", "20:00"]
    val instructions: String = "",
    val prescribedBy: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val stockCount: Int = 0,
    val refillReminder: Boolean = true,
    val refillThreshold: Int = 5,
    val reminderEnabled: Boolean = true,
    val takenToday: Boolean = false,
    val notes: String = ""
)
