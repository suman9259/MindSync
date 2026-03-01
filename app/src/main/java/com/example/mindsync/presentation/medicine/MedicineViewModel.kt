package com.example.mindsync.presentation.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.MedicineDao
import com.example.mindsync.data.local.database.entity.MedicineEntity
import com.example.mindsync.data.local.database.entity.toDomain
import com.example.mindsync.domain.model.Medicine
import com.example.mindsync.domain.model.MedicineFrequency
import com.example.mindsync.domain.model.MedicineUnit
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MedicineState(
    val medicines: List<Medicine> = emptyList(),
    val takenCount: Int = 0,
    val pendingCount: Int = 0,
    val maxStreak: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MedicineViewModel(
    private val medicineDao: MedicineDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(MedicineState())
    val state: StateFlow<MedicineState> = _state.asStateFlow()
    
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    init {
        observeMedicines()
    }
    
    private fun observeMedicines() {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            medicineDao.getMedicinesByUser(userId).collect { entities ->
                val medicines = entities.map { it.toDomain() }
                val takenCount = medicines.count { it.takenToday }
                val pendingCount = medicines.count { !it.takenToday && it.reminderEnabled }
                val maxStreak = medicines.maxOfOrNull { it.currentStreak } ?: 0
                
                _state.update {
                    it.copy(
                        medicines = medicines,
                        takenCount = takenCount,
                        pendingCount = pendingCount,
                        maxStreak = maxStreak,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun addMedicine(
        name: String,
        description: String,
        dosage: String,
        unit: MedicineUnit,
        frequency: MedicineFrequency,
        timesPerDay: Int = 1,
        scheduledTimes: List<Long> = emptyList(),
        instructions: String = "",
        reminderEnabled: Boolean = true
    ) {
        if (userId.isEmpty() || name.isBlank()) return
        
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val entity = MedicineEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                description = description,
                dosage = dosage,
                unit = unit.name,
                frequency = frequency.name,
                timesPerDay = timesPerDay,
                scheduledTimes = scheduledTimes.joinToString(","),
                startDate = now,
                endDate = null,
                instructions = instructions,
                sideEffects = "",
                imageUrl = "",
                reminderEnabled = reminderEnabled,
                takenToday = false,
                takenCount = 0,
                missedCount = 0,
                currentStreak = 0,
                createdAt = now,
                updatedAt = now,
                isSynced = false
            )
            medicineDao.insertMedicine(entity)
        }
    }
    
    fun updateMedicine(
        id: String,
        name: String,
        description: String,
        dosage: String,
        unit: MedicineUnit,
        frequency: MedicineFrequency,
        instructions: String,
        reminderEnabled: Boolean,
        scheduledTimes: List<Long> = emptyList()
    ) {
        viewModelScope.launch {
            val existing = medicineDao.getMedicineById(id) ?: return@launch
            val updated = existing.copy(
                name = name,
                description = description,
                dosage = dosage,
                unit = unit.name,
                frequency = frequency.name,
                instructions = instructions,
                reminderEnabled = reminderEnabled,
                scheduledTimes = scheduledTimes.joinToString(","),
                updatedAt = System.currentTimeMillis()
            )
            medicineDao.updateMedicine(updated)
        }
    }
    
    fun deleteMedicine(id: String) {
        viewModelScope.launch {
            medicineDao.deleteMedicineById(id)
        }
    }
    
    fun toggleMedicineTaken(id: String) {
        viewModelScope.launch {
            val medicine = medicineDao.getMedicineById(id) ?: return@launch
            val newTakenStatus = !medicine.takenToday
            val newStreak = if (newTakenStatus) medicine.currentStreak + 1 else maxOf(0, medicine.currentStreak - 1)
            
            val updated = medicine.copy(
                takenToday = newTakenStatus,
                takenCount = if (newTakenStatus) medicine.takenCount + 1 else medicine.takenCount,
                currentStreak = newStreak,
                updatedAt = System.currentTimeMillis()
            )
            medicineDao.updateMedicine(updated)
        }
    }
    
    fun toggleReminder(id: String, enabled: Boolean) {
        viewModelScope.launch {
            val medicine = medicineDao.getMedicineById(id) ?: return@launch
            val updated = medicine.copy(
                reminderEnabled = enabled,
                updatedAt = System.currentTimeMillis()
            )
            medicineDao.updateMedicine(updated)
        }
    }
}
