package com.orbit.recovery.ui.screens

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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.orbit.recovery.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.components.OrbitPrimaryButton
import com.orbit.recovery.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    onNavigateToContentFilter: () -> Unit,
    onNavigateToScreenTime: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val name by viewModel.name.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val orbs by viewModel.orbs.collectAsState()
    val daysSince by viewModel.daysSince.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Delete Plan & Reset", style = Typography.titleLarge, color = OrbitTextPrimary) },
            text = { Text("Are you sure? This will delete all local progress, streaks, and personalization data. This action cannot be undone.", style = Typography.bodyMedium, color = OrbitTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    viewModel.resetData {
                        onNavigateToWelcome()
                    }
                }) {
                    Text("Delete Everything", color = OrbitPanicButton, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = OrbitTextPrimary)
                }
            },
            containerColor = OrbitSurface
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = OrbitBackground) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, OrbitBorder),
                    color = Color.Transparent,
                    onClick = onNavigateBack,
                    modifier = Modifier.height(36.dp).wrapContentWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(16.dp),
                            tint = OrbitTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", style = Typography.labelSmall, color = OrbitTextPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Profile", style = Typography.headlineLarge, color = OrbitTextPrimary)
                Spacer(modifier = Modifier.height(24.dp))

                // User section
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(OrbitPrimaryLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase(),
                                style = Typography.headlineLarge,
                                color = OrbitPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Level 1 · Recovery Journey", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        }
                        TextButton(onClick = { }) {
                            Text("Edit", style = Typography.bodyMedium, color = OrbitPrimary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniStatCard("🔥", "Streak", streak.toString(), "days", modifier = Modifier.weight(1f))
                    MiniStatCard("💎", "Orbs", orbs.toString(), "collected", modifier = Modifier.weight(1f))
                    MiniStatCard("📅", "Days", daysSince.toString(), "total", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Settings list
                Text("SETTINGS", style = Typography.labelSmall, color = OrbitTextMuted)
                Spacer(modifier = Modifier.height(8.dp))

                SettingsRow("🔔", "Notifications", "Daily reminders") {}
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow("🛡️", "Content Filter", "DNS protection settings") { onNavigateToContentFilter() }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow("⏱️", "Screen Time", "App limits") { onNavigateToScreenTime() }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow("🎯", "My Habit", "Update your focus area") {}
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow("📝", "My Note", "Update your personal note") {}
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow("📅", "Free Since", "Update your start date") {}

                Spacer(modifier = Modifier.height(32.dp))

                // Danger zone
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = OrbitPanicBg.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, OrbitPanicBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("PROTOTYPE TOOLS", style = Typography.labelSmall, color = OrbitTextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Delete your plan and reset local data. A cooldown gives you time to change your mind.",
                            style = Typography.bodyMedium,
                            color = OrbitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrbitPanicButton),
                            border = BorderStroke(1.dp, OrbitPanicButton),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("↺ Delete plan & reset", style = Typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))


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

                // Disclaimer
                Text(
                    text = "Orbit provides support tools and habit tracking, not medical advice.",
                    style = Typography.bodyMedium,
                    color = OrbitTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
fun MiniStatCard(icon: String, label: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Surface(
        color = OrbitSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OrbitBorder),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, style = Typography.labelSmall, color = OrbitTextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = Typography.headlineMedium, color = OrbitTextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(caption, style = Typography.labelSmall, color = OrbitTextMuted)
        }
    }
}

@Composable
fun SettingsRow(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        color = OrbitSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OrbitBorder),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = Typography.titleMedium, color = OrbitTextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, style = Typography.bodyMedium, color = OrbitTextSecondary)
            }
            Text("→", style = Typography.titleLarge, color = OrbitTextMuted)
        }
    }
}
