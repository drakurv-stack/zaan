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

class ContentFilterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)
    
    private val _isVpnEnabled = MutableStateFlow(false)
    val isVpnEnabled: StateFlow<Boolean> = _isVpnEnabled

    init {
        viewModelScope.launch {
            val prefs = repository.preferencesFlow.first()
            _isVpnEnabled.value = prefs[UserPreferencesRepository.VPN_ENABLED_KEY] ?: false
        }
    }

    fun setVpnEnabled(enabled: Boolean) {
        _isVpnEnabled.value = enabled
        viewModelScope.launch {
            repository.setVpnEnabled(enabled)
        }
    }
}
