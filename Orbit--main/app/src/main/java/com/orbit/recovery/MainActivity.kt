package com.orbit.recovery

import android.content.Context
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import com.orbit.recovery.ui.theme.OrbitBackground
import com.orbit.recovery.ui.theme.OrbitTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var showPinDialog by mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("orbit_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("pending_deactivation", false)) {
            showPinDialog = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val repo = UserPreferencesRepository(applicationContext.dataStore)
            val prefs = repo.preferencesFlow.first()
            
            val vpnEnabled = prefs[UserPreferencesRepository.VPN_ENABLED_KEY] ?: false
            if (vpnEnabled) {
                val intent = Intent(this@MainActivity, OrbitVpnService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }

            val appLimitsJson = prefs[UserPreferencesRepository.APP_LIMITS_KEY] ?: "{}"
            val type = object : TypeToken<Map<String, Int>>() {}.type
            val limitsMap: Map<String, Int> = Gson().fromJson(appLimitsJson, type) ?: emptyMap()
            if (limitsMap.isNotEmpty()) {
                val intent = Intent(this@MainActivity, AppBlockerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
        }

        setContent {
            val onboardingComplete by applicationContext.dataStore.data
                .map { it[UserPreferencesRepository.ONBOARDING_COMPLETE_KEY] ?: false }
                .collectAsState(initial = false)
                
            OrbitTheme {
                if (showPinDialog) {
                    var enteredPin by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
                    var error by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    
                    val savedPin by applicationContext.dataStore.data
                        .map { it[UserPreferencesRepository.PROTECTION_PIN_KEY] ?: "" }
                        .collectAsState(initial = "")
                        
                    AlertDialog(
                        onDismissRequest = { /* forced */ },
                        title = { Text("Unlock Protection") },
                        text = {
                            Column {
                                Text("Enter your 4-digit PIN to disable uninstall protection.")
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = { enteredPin = it },
                                    isError = error,
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true
                                )
                                if (error) {
                                    Text("Incorrect PIN", color = androidx.compose.ui.graphics.Color.Red)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (enteredPin == savedPin) {
                                    val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                                    val comp = ComponentName(this@MainActivity, OrbitDeviceAdminReceiver::class.java)
                                    dpm.removeActiveAdmin(comp)
                                    
                                    getSharedPreferences("orbit_prefs", Context.MODE_PRIVATE)
                                        .edit().putBoolean("pending_deactivation", false).apply()
                                    showPinDialog = false
                                } else {
                                    error = true
                                }
                            }) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                getSharedPreferences("orbit_prefs", Context.MODE_PRIVATE)
                                    .edit().putBoolean("pending_deactivation", false).apply()
                                showPinDialog = false
                            }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Surface(modifier = Modifier.fillMaxSize(), color = OrbitBackground) {
                    val navController = rememberNavController()
                    val startDest = if (onboardingComplete) "home" else "welcome"
                    
                    AppNavigation(
                        navController = navController,
                        startDestination = startDest
                    )
                }
            }
        }
    }
}
