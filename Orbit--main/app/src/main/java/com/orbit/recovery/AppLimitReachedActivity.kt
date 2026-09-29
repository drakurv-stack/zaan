package com.orbit.recovery

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.components.OrbitPrimaryButton
import com.orbit.recovery.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppLimitReachedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val packageName = intent.getStringExtra("package_name") ?: ""
        val limitMinutes = intent.getIntExtra("limit_minutes", 0)
        
        val pm = packageManager
        val appName = try {
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (e: Exception) {
            packageName
        }

        setContent {
            OrbitTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = OrbitPanicBg) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        OrbitCard(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔒", style = Typography.displayMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(appName, style = Typography.headlineMedium, color = OrbitTextPrimary, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("You've reached your limit", style = Typography.titleLarge, color = OrbitPanicButton, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "You set a $limitMinutes-minute daily limit for this app to protect your focus.",
                                    style = Typography.bodyLarge,
                                    color = OrbitTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                OrbitPrimaryButton(
                                    text = "← Go Back",
                                    onClick = {
                                        val homeIntent = Intent(Intent.ACTION_MAIN)
                                        homeIntent.addCategory(Intent.CATEGORY_HOME)
                                        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(homeIntent)
                                        finish()
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = {
                                    lifecycleScope.launch {
                                        val repo = UserPreferencesRepository(applicationContext.dataStore)
                                        val prefs = repo.preferencesFlow.first()
                                        val gson = Gson()
                                        val overridesJson = prefs[UserPreferencesRepository.APP_OVERRIDES_KEY] ?: "{}"
                                        val type = object : TypeToken<MutableMap<String, Long>>() {}.type
                                        val map: MutableMap<String, Long> = gson.fromJson(overridesJson, type) ?: mutableMapOf()
                                        
                                        // 15 mins
                                        map[packageName] = System.currentTimeMillis() + (15 * 60 * 1000L)
                                        repo.saveAppOverrides(gson.toJson(map))
                                        
                                        finish()
                                    }
                                }) {
                                    Text("Override for 15 minutes", style = Typography.bodyLarge, color = OrbitTextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                TextButton(onClick = {
                                    val intent = Intent(this@AppLimitReachedActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }) {
                                    Text("Edit Limit →", style = Typography.bodyLarge, color = OrbitTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
