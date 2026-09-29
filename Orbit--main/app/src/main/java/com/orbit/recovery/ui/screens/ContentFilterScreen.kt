package com.orbit.recovery.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.OrbitVpnService
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.theme.*

@Composable
fun ContentFilterScreen(
    onNavigateBack: () -> Unit,
    viewModel: ContentFilterViewModel = viewModel()
) {
    val context = LocalContext.current
    val isVpnEnabled by viewModel.isVpnEnabled.collectAsState()
    var isHowItWorksExpanded by remember { mutableStateOf(false) }

    val vpnLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = Intent(context, OrbitVpnService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            viewModel.setVpnEnabled(true)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = OrbitBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                Text(
                    text = "Content Filter",
                    style = Typography.titleLarge,
                    color = OrbitTextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(72.dp))
            }
            
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Main toggle card
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                            Text("🛡️", fontSize = 64.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Switch(
                                checked = isVpnEnabled,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        val intent = VpnService.prepare(context)
                                        if (intent != null) {
                                            vpnLauncher.launch(intent)
                                        } else {
                                            val serviceIntent = Intent(context, OrbitVpnService::class.java)
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                context.startForegroundService(serviceIntent)
                                            } else {
                                                context.startService(serviceIntent)
                                            }
                                            viewModel.setVpnEnabled(true)
                                        }
                                    } else {
                                        val serviceIntent = Intent(context, OrbitVpnService::class.java).apply { action = "STOP" }
                                        context.startService(serviceIntent)
                                        viewModel.setVpnEnabled(false)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrbitPrimary,
                                    uncheckedThumbColor = OrbitTextMuted,
                                    uncheckedTrackColor = OrbitBorder
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Safe DNS Shield", style = Typography.headlineLarge, color = OrbitTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Blocks adult, gambling, and malicious websites across all apps and browsers on this device.",
                            style = Typography.bodyLarge,
                            color = OrbitTextSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val dotColor = if (isVpnEnabled) Color(0xFF10B981) else OrbitTextMuted
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                            Spacer(modifier = Modifier.width(8.dp))
                            val statusText = if (isVpnEnabled) "Active — CleanBrowsing Family DNS" else "Shield is off"
                            val statusColor = if (isVpnEnabled) OrbitPrimary else OrbitTextMuted
                            Text(statusText, style = Typography.bodyMedium, color = statusColor)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("What Gets Blocked", style = Typography.titleLarge, color = OrbitTextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                
                FilterCategoryCard(
                    icon = "🚫",
                    title = "Adult Content",
                    desc = "Explicit websites and sexual material",
                    borderColor = Color(0xFFD47B7B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilterCategoryCard(
                    icon = "🎰",
                    title = "Gambling Sites",
                    desc = "Online betting, casinos, and sports gambling",
                    borderColor = OrbitAmber
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilterCategoryCard(
                    icon = "⚠️",
                    title = "Malicious Sites",
                    desc = "Phishing, malware, and scam websites",
                    borderColor = Color(0xFF6B7280)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Collapsible 
                Surface(
                    color = OrbitSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, OrbitBorder),
                    modifier = Modifier.fillMaxWidth().clickable { isHowItWorksExpanded = !isHowItWorksExpanded }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("How does this work?", style = Typography.titleMedium, color = OrbitTextPrimary, modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isHowItWorksExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = OrbitTextPrimary
                            )
                        }
                        AnimatedVisibility(visible = isHowItWorksExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Orbit sets up a local VPN on your device that redirects DNS queries to CleanBrowsing's family-safe servers. No internet traffic passes through our servers — only DNS lookups change. This blocks harmful sites across all browsers and apps.",
                                    style = Typography.bodyMedium,
                                    color = OrbitTextSecondary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun FilterCategoryCard(icon: String, title: String, desc: String, borderColor: Color) {
    Surface(
        color = OrbitSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OrbitBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(4.dp).background(borderColor))
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, style = Typography.titleMedium, color = OrbitTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(desc, style = Typography.bodyMedium, color = OrbitTextSecondary)
                }
            }
        }
    }
}
