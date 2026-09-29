package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DailyCheckinData(
    val date: String,
    val mood: Int,
    val urgeLevel: Int,
    val triggers: List<String>,
    val win: String,
    val stayedStrong: Boolean
)

class DailyCheckinViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)
    private val gson = Gson()

    fun saveCheckin(
        mood: Int,
        urgeLevel: Int,
        triggers: List<String>,
        win: String,
        stayedStrong: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val prefs = repository.preferencesFlow.first()
            val savedJson = prefs[UserPreferencesRepository.CHECKINS_KEY] ?: "{}"
            
            val type = object : TypeToken<MutableMap<String, DailyCheckinData>>() {}.type
            val map: MutableMap<String, DailyCheckinData> = gson.fromJson(savedJson, type) ?: mutableMapOf()
            
            val todayIso = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            map[todayIso] = DailyCheckinData(todayIso, mood, urgeLevel, triggers, win, stayedStrong)
            
            repository.saveDailyCheckin(gson.toJson(map), stayedStrong)
            
            onSuccess()
        }
    }
}
