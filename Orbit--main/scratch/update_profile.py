import re

with open("app/src/main/java/com/orbit/recovery/ui/screens/ProfileScreen.kt", "r", encoding="utf-8") as f:
    content = f.read()

imports = """
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Shield
import com.orbit.recovery.OrbitDeviceAdminReceiver
"""

# Insert imports after package
content = content.replace("package com.orbit.recovery.ui.screens\n", "package com.orbit.recovery.ui.screens\n" + imports)

# We want to add the Protection section before the Disclaimer
protection_section = """
                // Protection section
                val context = LocalContext.current
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val componentName = ComponentName(context, OrbitDeviceAdminReceiver::class.java)
                
                var isAdminActive by remember { mutableStateOf(dpm.isAdminActive(componentName)) }
                var pin by remember { mutableStateOf("") }
                
                val adminLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    isAdminActive = dpm.isAdminActive(componentName)
                }

                Text("PROTECTION", style = Typography.labelSmall, color = OrbitTextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = if (isAdminActive) Icons.Default.Shield else Icons.Default.Shield, contentDescription = null, tint = if (isAdminActive) OrbitPrimary else OrbitTextSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAdminActive) "Uninstall Protection: Active" else "Uninstall Protection: Inactive", style = Typography.bodyLarge, color = OrbitTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!isAdminActive) {
                            OutlinedTextField(
                                value = pin,
                                onValueChange = { if (it.length <= 4) pin = it.filter { char -> char.isDigit() } },
                                label = { Text("Set 4-digit PIN", color = OrbitTextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                textStyle = Typography.bodyLarge.copy(color = OrbitTextPrimary),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrbitPrimary,
                                    unfocusedBorderColor = OrbitBorder
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            OrbitPrimaryButton(
                                text = "Enable Uninstall Protection",
                                enabled = pin.length == 4,
                                onClick = {
                                    viewModel.saveProtectionPin(pin)
                                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Activate device admin to prevent app uninstallation during urges.")
                                    }
                                    adminLauncher.launch(intent)
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
"""

content = content.replace("                // Disclaimer\n", protection_section + "\n                // Disclaimer\n")

with open("app/src/main/java/com/orbit/recovery/ui/screens/ProfileScreen.kt", "w", encoding="utf-8") as f:
    f.write(content)
print("Updated ProfileScreen.kt")
