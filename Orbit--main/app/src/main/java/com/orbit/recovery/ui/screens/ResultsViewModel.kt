package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.launch

class ResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.completeOnboarding()
            onComplete()
        }
    }
}
