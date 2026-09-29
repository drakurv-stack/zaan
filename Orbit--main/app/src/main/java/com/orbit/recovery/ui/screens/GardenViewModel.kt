package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GardenViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)
    
    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak

    init {
        viewModelScope.launch {
            repository.preferencesFlow.collect { prefs ->
                _streak.value = prefs[UserPreferencesRepository.STREAK_KEY] ?: 0
            }
        }
    }
}
