package com.orbit.recovery.ui.screens

import android.app.AppOpsManager
import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Process
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val iconBitmap: Bitmap?,
    val usageTimeMillis: Long,
    val hasLimit: Boolean,
    val limitMinutes: Int
)

class ScreenTimeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)
    private val gson = Gson()
    
    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission
    
    private val _appUsages = MutableStateFlow<List<AppUsageInfo>>(emptyList())
    val appUsages: StateFlow<List<AppUsageInfo>> = _appUsages

    private val _totalTimeMillis = MutableStateFlow(0L)
    val totalTimeMillis: StateFlow<Long> = _totalTimeMillis
    
    fun checkPermission() {
        val appOps = getApplication<Application>().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            getApplication<Application>().packageName
        )
        _hasPermission.value = mode == AppOpsManager.MODE_ALLOWED
    }

    fun loadUsageStats() {
        if (!_hasPermission.value) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val usageStatsManager = getApplication<Application>().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val pm = getApplication<Application>().packageManager
                
                val startOfDay = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, System.currentTimeMillis())
                
                val prefs = repository.preferencesFlow.first()
                val limitsJson = prefs[UserPreferencesRepository.APP_LIMITS_KEY] ?: "{}"
                val type = object : TypeToken<Map<String, Int>>() {}.type
                val limitsMap: Map<String, Int> = gson.fromJson(limitsJson, type) ?: emptyMap()
                
                var total = 0L
                val list = stats
                    .filter { it.totalTimeInForeground > 0 }
                    .mapNotNull { stat ->
                        try {
                            val intent = pm.getLaunchIntentForPackage(stat.packageName)
                            if (intent != null) {
                                val info = pm.getApplicationInfo(stat.packageName, 0)
                                val name = pm.getApplicationLabel(info).toString()
                                val icon = pm.getApplicationIcon(info).toBitmap()
                                total += stat.totalTimeInForeground
                                val limitMinutes = limitsMap[stat.packageName] ?: 0
                                AppUsageInfo(
                                    packageName = stat.packageName,
                                    appName = name,
                                    iconBitmap = icon,
                                    usageTimeMillis = stat.totalTimeInForeground,
                                    hasLimit = limitsMap.containsKey(stat.packageName),
                                    limitMinutes = limitMinutes
                                )
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .sortedByDescending { it.usageTimeMillis }
                    .take(10)
                    
                _totalTimeMillis.value = total
                _appUsages.value = list
            }
        }
    }

    fun setLimit(packageName: String, minutes: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            val prefs = repository.preferencesFlow.first()
            val limitsJson = prefs[UserPreferencesRepository.APP_LIMITS_KEY] ?: "{}"
            val type = object : TypeToken<MutableMap<String, Int>>() {}.type
            val limitsMap: MutableMap<String, Int> = gson.fromJson(limitsJson, type) ?: mutableMapOf()
            
            limitsMap[packageName] = minutes
            repository.saveAppLimits(gson.toJson(limitsMap))
            
            try {
                val intent = Intent(getApplication(), Class.forName("com.orbit.recovery.AppBlockerService"))
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    getApplication<Application>().startForegroundService(intent)
                } else {
                    getApplication<Application>().startService(intent)
                }
            } catch (e: Exception) {}
            
            loadUsageStats()
            onComplete()
        }
    }

    fun removeLimit(packageName: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val prefs = repository.preferencesFlow.first()
            val limitsJson = prefs[UserPreferencesRepository.APP_LIMITS_KEY] ?: "{}"
            val type = object : TypeToken<MutableMap<String, Int>>() {}.type
            val limitsMap: MutableMap<String, Int> = gson.fromJson(limitsJson, type) ?: mutableMapOf()
            
            limitsMap.remove(packageName)
            repository.saveAppLimits(gson.toJson(limitsMap))
            
            loadUsageStats()
            onComplete()
        }
    }
}
