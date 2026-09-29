import re

with open("app/src/main/java/com/orbit/recovery/MainActivity.kt", "r", encoding="utf-8") as f:
    content = f.read()

imports = """
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
"""

content = content.replace("package com.orbit.recovery\n", "package com.orbit.recovery\n" + imports)

# Add showPinDialog mutable state at the class level
class_start = "class MainActivity : ComponentActivity() {\n"
class_start_replacement = class_start + """
    private var showPinDialog by mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("orbit_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("pending_deactivation", false)) {
            showPinDialog = true
        }
    }
"""
content = content.replace(class_start, class_start_replacement)

# Add the dialog inside the setContent block
setContent_str = """
            OrbitTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = OrbitBackground) {
"""

dialog_str = """
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
"""

content = content.replace(setContent_str, dialog_str)

with open("app/src/main/java/com/orbit/recovery/MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(content)
print("Updated MainActivity.kt")
