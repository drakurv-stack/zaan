package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.launch

class PersonalizationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)

    fun saveQuizResults(
        name: String,
        reasons: Set<String>,
        habit: String,
        age: String,
        duration: String,
        increased: String,
        changes: String,
        financial: String,
        religion: String
    ) {
        viewModelScope.launch {
            repository.savePreferences(
                name, reasons, habit, age, duration, increased, changes, financial, religion
            )
        }
    }
}
