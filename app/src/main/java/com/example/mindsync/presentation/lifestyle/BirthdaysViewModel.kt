package com.example.mindsync.presentation.lifestyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.database.dao.SpecialDateDao
import com.example.mindsync.data.local.database.entity.SpecialDateEntity
import com.example.mindsync.domain.model.SpecialDate
import com.example.mindsync.domain.model.SpecialDateType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class BirthdaysState(
    val specialDates: List<SpecialDate> = emptyList(),
    val isLoading: Boolean = false
)

class BirthdaysViewModel(
    private val specialDateDao: SpecialDateDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(BirthdaysState())
    val state: StateFlow<BirthdaysState> = _state.asStateFlow()
    
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    init {
        observeSpecialDates()
    }
    
    private fun observeSpecialDates() {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            specialDateDao.getSpecialDatesByUser(userId).collect { entities ->
                val specialDates = entities.map { entity ->
                    SpecialDate(
                        id = entity.id,
                        userId = entity.userId,
                        personName = entity.personName,
                        dateType = try { 
                            SpecialDateType.valueOf(entity.dateType) 
                        } catch (e: Exception) { 
                            SpecialDateType.BIRTHDAY 
                        },
                        date = entity.date,
                        year = entity.year,
                        reminderDaysBefore = entity.reminderDaysBefore,
                        reminderEnabled = entity.reminderEnabled,
                        giftIdeas = entity.giftIdeas,
                        notes = entity.notes,
                        createdAt = entity.createdAt
                    )
                }
                _state.value = BirthdaysState(
                    specialDates = specialDates,
                    isLoading = false
                )
            }
        }
    }
    
    fun addSpecialDate(
        personName: String,
        dateType: SpecialDateType,
        date: Long,
        year: Int? = null,
        giftIdeas: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val entity = SpecialDateEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                personName = personName,
                dateType = dateType.name,
                date = date,
                year = year,
                reminderDaysBefore = 1,
                reminderEnabled = true,
                giftIdeas = giftIdeas,
                notes = notes,
                createdAt = System.currentTimeMillis()
            )
            specialDateDao.insertSpecialDate(entity)
        }
    }
    
    fun deleteSpecialDate(id: String) {
        viewModelScope.launch {
            specialDateDao.deleteSpecialDate(id)
        }
    }
    
    fun updateSpecialDate(specialDate: SpecialDate) {
        viewModelScope.launch {
            val entity = SpecialDateEntity(
                id = specialDate.id,
                userId = userId,
                personName = specialDate.personName,
                dateType = specialDate.dateType.name,
                date = specialDate.date,
                year = specialDate.year,
                reminderDaysBefore = specialDate.reminderDaysBefore,
                reminderEnabled = specialDate.reminderEnabled,
                giftIdeas = specialDate.giftIdeas,
                notes = specialDate.notes,
                createdAt = specialDate.createdAt
            )
            specialDateDao.updateSpecialDate(entity)
        }
    }
}
