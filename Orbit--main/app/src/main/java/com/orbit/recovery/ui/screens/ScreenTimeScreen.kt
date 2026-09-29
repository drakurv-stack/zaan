package com.orbit.recovery.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.orbit.recovery.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.ui.components.OrbitBottomNav
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.components.OrbitPrimaryButton
import com.orbit.recovery.ui.components.OrbitTab
import com.orbit.recovery.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeScreen(
    onNavigateTab: (OrbitTab) -> Unit,
    onNavigateToContentFilter: () -> Unit,
    viewModel: ScreenTimeViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission by viewModel.hasPermission.collectAsState()
    val appUsages by viewModel.appUsages.collectAsState()
    val totalTimeMillis by viewModel.totalTimeMillis.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermission()
                viewModel.loadUsageStats()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        bottomBar = { OrbitBottomNav(currentTab = OrbitTab.ScreenTime, onTabSelected = onNavigateTab) },
        containerColor = OrbitBackground
    ) { paddingValues ->
        if (!hasPermission) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("🕒", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Allow Usage Access", style = Typography.headlineLarge, color = OrbitTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Orbit needs to see which apps you're using to help you set healthy limits. Your data never leaves your device.",
                            style = Typography.bodyLarge,
                            color = OrbitTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OrbitPrimaryButton(
                            text = "Open Settings",
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            }
                        )
                    }
                }
            }
        } else {
            var selectedApp by remember { mutableStateOf<AppUsageInfo?>(null) }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ORBIT / Screen Time", style = Typography.labelSmall, color = OrbitTextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Today's Usage", style = Typography.headlineMedium, color = OrbitTextPrimary)
                        }
                        
                        Surface(
                            color = OrbitSurface,
                            shape = RoundedCornerShape(50.dp),
                            border = BorderStroke(1.dp, OrbitBorder),
                            onClick = onNavigateToContentFilter,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🛡️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Content Filter", style = Typography.labelSmall, color = OrbitTextPrimary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    OrbitCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("TODAY", style = Typography.labelSmall, color = OrbitTextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val hours = (totalTimeMillis / (1000 * 60 * 60)).toInt()
                            val minutes = ((totalTimeMillis / (1000 * 60)) % 60).toInt()
                            
                            Text("${hours}h ${minutes}m", style = Typography.displayLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("↑ 23 min more than yesterday", style = Typography.bodyMedium, color = OrbitPanicButton)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Surface(
                                    color = OrbitSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        val topApp = appUsages.firstOrNull()?.appName ?: "None"
                                        Text("Most used:", style = Typography.labelSmall, color = OrbitTextSecondary)
                                        Text(topApp, style = Typography.bodyMedium, color = OrbitTextPrimary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    color = OrbitSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Apps checked:", style = Typography.labelSmall, color = OrbitTextSecondary)
                                        Text("${appUsages.size} times", style = Typography.bodyMedium, color = OrbitTextPrimary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text("Apps Today", style = Typography.titleLarge, color = OrbitTextPrimary)
                    Text("Tap an app to set a daily limit", style = Typography.bodyMedium, color = OrbitTextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val maxUsage = appUsages.maxOfOrNull { it.usageTimeMillis } ?: 1L
                
                items(appUsages) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedApp = app }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (app.iconBitmap != null) {
                            Image(
                                bitmap = app.iconBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Box(modifier = Modifier.size(32.dp).background(OrbitBorder, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(app.appName, style = Typography.bodyLarge, color = OrbitTextPrimary)
                                val appMins = (app.usageTimeMillis / (1000 * 60)).toInt()
                                val appHrs = appMins / 60
                                val remainingMins = appMins % 60
                                val timeStr = if (appHrs > 0) "${appHrs}h ${remainingMins}m" else "${appMins}m"
                                Text(timeStr, style = Typography.bodyMedium, color = OrbitTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(OrbitBorder, RoundedCornerShape(2.dp))
                            ) {
                                val fraction = (app.usageTimeMillis.toFloat() / maxUsage.toFloat()).coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = fraction)
                                        .height(4.dp)
                                        .background(OrbitPrimary, RoundedCornerShape(2.dp))
                                )
                            }
                            
                            if (app.hasLimit) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = OrbitAmber.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Limit: ${app.limitMinutes}m",
                                        style = Typography.labelSmall,
                                        color = OrbitAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (selectedApp != null) {
                var sliderValue by remember { mutableStateOf(selectedApp!!.limitMinutes.toFloat().coerceAtLeast(5f)) }
                
                ModalBottomSheet(
                    onDismissRequest = { selectedApp = null },
                    containerColor = OrbitBackground
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedApp!!.iconBitmap != null) {
                            Image(
                                bitmap = selectedApp!!.iconBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(selectedApp!!.appName, style = Typography.headlineMedium, color = OrbitTextPrimary)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("Daily Limit", style = Typography.labelSmall, color = OrbitTextSecondary, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("${sliderValue.toInt()} minutes", style = Typography.displaySmall, color = OrbitTextPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 5f..180f,
                            steps = 34,
                            colors = SliderDefaults.colors(activeTrackColor = OrbitPrimary, thumbColor = OrbitPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        OrbitPrimaryButton(
                            text = "Set Limit",
                            onClick = {
                                viewModel.setLimit(selectedApp!!.packageName, sliderValue.toInt()) {
                                    selectedApp = null
                                }
                            }
                        )
                        
                        if (selectedApp!!.hasLimit) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(onClick = {
                                viewModel.removeLimit(selectedApp!!.packageName) {
                                    selectedApp = null
                                }
                            }) {
                                Text("Remove Limit", style = Typography.bodyLarge, color = OrbitPanicButton)
                            }
                        }
                    }
                }
            }
        }
    }
}
