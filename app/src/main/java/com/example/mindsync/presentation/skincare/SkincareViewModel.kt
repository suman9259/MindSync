package com.example.mindsync.presentation.skincare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.SkincareDao
import com.example.mindsync.data.local.database.entity.SkincareRoutineEntity
import com.example.mindsync.data.local.database.entity.SkincareStepEntity
import com.example.mindsync.data.local.database.entity.toDomain
import com.example.mindsync.domain.model.SkincareCategory
import com.example.mindsync.domain.model.SkincareRoutine
import com.example.mindsync.domain.model.SkincareRoutineType
import com.example.mindsync.domain.model.SkincareStep
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SkincareState(
    val routines: List<SkincareRoutine> = emptyList(),
    val completedTodayCount: Int = 0,
    val totalRoutinesCount: Int = 0,
    val totalProductsCount: Int = 0,
    val maxStreak: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class SkincareViewModel(
    private val skincareDao: SkincareDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(SkincareState())
    val state: StateFlow<SkincareState> = _state.asStateFlow()
    
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    init {
        observeRoutines()
    }
    
    private fun observeRoutines() {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            skincareDao.getRoutinesByUser(userId).collect { entities ->
                val routinesWithSteps = entities.map { routineEntity ->
                    val steps = skincareDao.getStepsByRoutineSync(routineEntity.id)
                    routineEntity.toDomain(steps)
                }
                
                val completedCount = routinesWithSteps.count { it.completedToday }
                val totalProducts = routinesWithSteps.sumOf { it.steps.size }
                val maxStreak = routinesWithSteps.maxOfOrNull { it.currentStreak } ?: 0
                
                _state.update {
                    it.copy(
                        routines = routinesWithSteps,
                        completedTodayCount = completedCount,
                        totalRoutinesCount = routinesWithSteps.size,
                        totalProductsCount = totalProducts,
                        maxStreak = maxStreak,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun addRoutine(
        name: String,
        description: String,
        routineType: SkincareRoutineType,
        estimatedMinutes: Int,
        scheduledTime: Long = 0L,
        reminderEnabled: Boolean = true,
        steps: List<SkincareStep> = emptyList()
    ) {
        if (userId.isEmpty() || name.isBlank()) return
        
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val routineId = UUID.randomUUID().toString()
            
            val routineEntity = SkincareRoutineEntity(
                id = routineId,
                userId = userId,
                name = name,
                description = description,
                routineType = routineType.name,
                scheduledTime = scheduledTime,
                estimatedMinutes = estimatedMinutes,
                reminderEnabled = reminderEnabled,
                completedToday = false,
                completedCount = 0,
                currentStreak = 0,
                imageUrl = "",
                createdAt = now,
                updatedAt = now,
                isSynced = false
            )
            
            val stepEntities = steps.mapIndexed { index, step ->
                SkincareStepEntity(
                    id = UUID.randomUUID().toString(),
                    routineId = routineId,
                    name = step.name,
                    productName = step.productName,
                    productBrand = step.productBrand,
                    category = step.category.name,
                    instructions = step.instructions,
                    durationSeconds = step.durationSeconds,
                    orderIndex = index,
                    isCompleted = false,
                    imageUrl = "",
                    isSynced = false
                )
            }
            
            skincareDao.insertRoutineWithSteps(routineEntity, stepEntities)
        }
    }
    
    fun updateRoutine(
        id: String,
        name: String,
        description: String,
        routineType: SkincareRoutineType,
        estimatedMinutes: Int,
        scheduledTime: Long = 0L,
        reminderEnabled: Boolean = true
    ) {
        viewModelScope.launch {
            val existing = skincareDao.getRoutineById(id) ?: return@launch
            val updated = existing.copy(
                name = name,
                description = description,
                routineType = routineType.name,
                estimatedMinutes = estimatedMinutes,
                scheduledTime = scheduledTime,
                reminderEnabled = reminderEnabled,
                updatedAt = System.currentTimeMillis()
            )
            skincareDao.updateRoutine(updated)
        }
    }
    
    fun deleteRoutine(id: String) {
        viewModelScope.launch {
            skincareDao.deleteRoutineWithSteps(id)
        }
    }
    
    fun toggleRoutineCompleted(id: String) {
        viewModelScope.launch {
            val routine = skincareDao.getRoutineById(id) ?: return@launch
            val newCompletedStatus = !routine.completedToday
            val newStreak = if (newCompletedStatus) routine.currentStreak + 1 else maxOf(0, routine.currentStreak - 1)
            
            val updated = routine.copy(
                completedToday = newCompletedStatus,
                completedCount = if (newCompletedStatus) routine.completedCount + 1 else routine.completedCount,
                currentStreak = newStreak,
                updatedAt = System.currentTimeMillis()
            )
            skincareDao.updateRoutine(updated)
        }
    }
    
    fun startRoutine(id: String) {
        viewModelScope.launch {
            skincareDao.markAsCompleted(id, true)
        }
    }
    
    fun addStepToRoutine(
        routineId: String,
        name: String,
        productName: String,
        productBrand: String = "",
        category: SkincareCategory,
        instructions: String = "",
        durationSeconds: Int = 60
    ) {
        viewModelScope.launch {
            val existingSteps = skincareDao.getStepsByRoutineSync(routineId)
            val newOrderIndex = existingSteps.size
            
            val stepEntity = SkincareStepEntity(
                id = UUID.randomUUID().toString(),
                routineId = routineId,
                name = name,
                productName = productName,
                productBrand = productBrand,
                category = category.name,
                instructions = instructions,
                durationSeconds = durationSeconds,
                orderIndex = newOrderIndex,
                isCompleted = false,
                imageUrl = "",
                isSynced = false
            )
            skincareDao.insertStep(stepEntity)
        }
    }
    
    fun deleteStep(step: SkincareStepEntity) {
        viewModelScope.launch {
            skincareDao.deleteStep(step)
        }
    }
}
