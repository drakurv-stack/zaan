package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)

    private val _name = MutableStateFlow("User")
    val name: StateFlow<String> = _name

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak

    private val _orbs = MutableStateFlow(0)
    val orbs: StateFlow<Int> = _orbs

    private val _daysSince = MutableStateFlow(0L)
    val daysSince: StateFlow<Long> = _daysSince

    init {
        viewModelScope.launch {
            repository.preferencesFlow.collect { prefs ->
                _name.value = prefs[UserPreferencesRepository.NAME_KEY] ?: "User"
                _streak.value = prefs[UserPreferencesRepository.STREAK_KEY] ?: 0
                _orbs.value = prefs[UserPreferencesRepository.ORBS_KEY] ?: 0
                
                val start = prefs[UserPreferencesRepository.START_DATE_KEY] ?: System.currentTimeMillis()
                val diff = System.currentTimeMillis() - start
                val days = diff / (1000 * 60 * 60 * 24)
                _daysSince.value = days.coerceAtLeast(0)
            }
        }
    }

    fun resetData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAll()
            onComplete()
        }
    }

    fun saveProtectionPin(pin: String) {
        viewModelScope.launch {
            repository.saveProtectionPin(pin)
        }
    }
}
