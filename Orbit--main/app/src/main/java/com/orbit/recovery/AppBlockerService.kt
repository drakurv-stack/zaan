package com.orbit.recovery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class AppBlockerService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val gson = Gson()
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var repository: UserPreferencesRepository

    private val runnable = object : Runnable {
        override fun run() {
            checkLimits()
            handler.postDelayed(this, 30_000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        repository = UserPreferencesRepository(applicationContext.dataStore)
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.removeCallbacks(runnable)
        handler.post(runnable)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkLimits() {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 5000 

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime)
        val foregroundApp = stats?.maxByOrNull { it.lastTimeUsed }?.packageName ?: return
        
        if (foregroundApp == packageName) return

        runBlocking {
            val prefs = repository.preferencesFlow.first()
            val limitsJson = prefs[UserPreferencesRepository.APP_LIMITS_KEY] ?: "{}"
            val overridesJson = prefs[UserPreferencesRepository.APP_OVERRIDES_KEY] ?: "{}"
            
            val typeLimits = object : TypeToken<Map<String, Int>>() {}.type
            val limitsMap: Map<String, Int> = gson.fromJson(limitsJson, typeLimits) ?: emptyMap()
            
            val typeOverrides = object : TypeToken<Map<String, Long>>() {}.type
            val overridesMap: Map<String, Long> = gson.fromJson(overridesJson, typeOverrides) ?: emptyMap()

            if (limitsMap.containsKey(foregroundApp)) {
                val overrideExpiry = overridesMap[foregroundApp]
                if (overrideExpiry != null && System.currentTimeMillis() < overrideExpiry) {
                    return@runBlocking 
                }

                val limitMillis = limitsMap[foregroundApp]!! * 60 * 1000L
                
                val startOfDay = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val dailyStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, System.currentTimeMillis())
                val appDailyStat = dailyStats?.find { it.packageName == foregroundApp }
                val totalUsed = appDailyStat?.totalTimeInForeground ?: 0L

                if (totalUsed >= limitMillis) {
                    val intent = Intent(this@AppBlockerService, AppLimitReachedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("package_name", foregroundApp)
                    intent.putExtra("limit_minutes", limitsMap[foregroundApp])
                    startActivity(intent)
                }
            }
        }
    }

    private fun buildNotification() = NotificationCompat.Builder(this, "orbit_blocker")
        .setContentTitle("Orbit is protecting your focus \uD83C\uDF3F")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("orbit_blocker", "App Blocker", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
