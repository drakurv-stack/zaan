package com.orbit.recovery.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.orbit.recovery.AppBlockerService
import com.orbit.recovery.OrbitVpnService
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.recovery.ui.components.*
import com.orbit.recovery.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.orbit.recovery.R

@Composable
fun HomeScreen(
    userName: String = "Alex",
    joinDate: String = "today",
    personalNote: String = "",
    onNavigateToWelcome: () -> Unit,
    onNavigateToPanic: () -> Unit,
    onNavigateToContentFilter: () -> Unit,
    onNavigateToGarden: () -> Unit,
    onNavigateToCoach: () -> Unit,
    onNavigateToDailyCheckin: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToScreenTime: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateTab: (OrbitTab) -> Unit
) {
    val context = LocalContext.current
    var shieldActive by remember { mutableStateOf(false) }

    val vpnLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = Intent(context, OrbitVpnService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            val blocker = Intent(context, AppBlockerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(blocker)
            } else {
                context.startService(blocker)
            }
            shieldActive = true
            onNavigateToPanic()
        }
    }

    Scaffold(
        bottomBar = {
            OrbitBottomNav(
                currentTab = OrbitTab.Home,
                onTabSelected = onNavigateTab
            )
        },
        containerColor = OrbitBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Top section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.orbit_logo),
                            contentDescription = "Orbit",
                            modifier = Modifier.height(32.dp).wrapContentWidth(),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hi $userName",
                            style = Typography.headlineLarge,
                            color = OrbitTextPrimary
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, OrbitBorder, RoundedCornerShape(50))
                            .clickable { onNavigateToWelcome() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Restart intro",
                            style = Typography.labelSmall,
                            color = OrbitTextPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Section 1 - Journey Card
            item {
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "YOUR JOURNEY",
                            style = Typography.labelSmall,
                            color = OrbitTextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Origin Seed",
                                    style = Typography.titleLarge,
                                    color = OrbitTextPrimary
                                )
                                Text(
                                    text = "Free since $joinDate",
                                    style = Typography.bodyMedium,
                                    color = OrbitTextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(OrbitSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = OrbitPrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Card 1
                            Surface(
                                color = OrbitSurfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = OrbitAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Streak", style = Typography.labelSmall, color = OrbitTextSecondary)
                                    Text("0", style = Typography.headlineMedium, color = OrbitTextPrimary)
                                    Text("days", style = Typography.bodyMedium, color = OrbitTextSecondary)
                                }
                            }
                            
                            // Card 2
                            Surface(
                                color = OrbitSurfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = OrbitPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Orbs", style = Typography.labelSmall, color = OrbitTextSecondary)
                                    Text("0", style = Typography.headlineMedium, color = OrbitTextPrimary)
                                    Text("collected", style = Typography.bodyMedium, color = OrbitTextSecondary)
                                }
                            }
                            
                            // Card 3
                            Surface(
                                color = OrbitSurfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = OrbitTextPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Today", style = Typography.labelSmall, color = OrbitTextSecondary)
                                    Text("Not yet", style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = OrbitTextPrimary)
                                    Text("One small step", style = Typography.bodyMedium, color = OrbitTextSecondary)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        OrbitPrimaryButton(
                            text = "Mark today complete",
                            onClick = { /* TODO */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OrbitOutlineButton(
                            text = "I slipped — reset streak",
                            onClick = { /* TODO */ }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Section 2 - Personal Note Card
            item {
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "PERSONAL NOTE",
                            style = Typography.labelSmall,
                            color = OrbitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = OrbitSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (personalNote.isNotBlank()) personalNote else "Add a note during setup",
                                style = Typography.bodyLarge,
                                color = if (personalNote.isNotBlank()) OrbitTextPrimary else OrbitTextMuted,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Section 3 - Need Support Now Card
            item {
                Surface(
                    color = OrbitPanicBg,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OrbitPanicButton),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "NEED SUPPORT NOW?",
                            style = Typography.labelSmall,
                            color = OrbitPanicButton
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A calmer next step",
                            style = Typography.titleLarge,
                            color = OrbitTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open a short guided reset when an urge feels close. You do not have to work through it alone.",
                            style = Typography.bodyMedium,
                            color = OrbitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                val vpnIntent = VpnService.prepare(context)
                                if (vpnIntent != null) {
                                    vpnLauncher.launch(vpnIntent)
                                } else {
                                    val intent = Intent(context, OrbitVpnService::class.java)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(intent)
                                    } else {
                                        context.startService(intent)
                                    }
                                    val blocker = Intent(context, AppBlockerService::class.java)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(blocker)
                                    } else {
                                        context.startService(blocker)
                                    }
                                    shieldActive = true
                                    onNavigateToPanic()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrbitPanicButton)
                        ) {
                            Text(
                                text = "Open Panic Button",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .border(1.dp, if (shieldActive) OrbitPrimary.copy(alpha = 0.5f) else OrbitPanicButton.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    .clickable { onNavigateToContentFilter() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (shieldActive) "🛡 Shield: ON" else "🛡 Shield: Off",
                                    style = Typography.labelSmall,
                                    color = if (shieldActive) OrbitPrimary else OrbitPanicButton
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Section 4 - Feature Navigation Cards
            item {
                FeatureNavigationCard(
                    icon = Icons.Default.Eco,
                    iconBgColor = OrbitPrimaryLight,
                    iconTintColor = OrbitPrimary,
                    title = "Your Seed",
                    subtitle = "Seed · view your garden",
                    onClick = onNavigateToGarden
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                FeatureNavigationCard(
                    icon = Icons.Default.ChatBubble,
                    iconBgColor = OrbitPrimaryLight,
                    iconTintColor = OrbitPrimary,
                    title = "AI Coach",
                    subtitle = "A supportive check-in",
                    onClick = onNavigateToCoach
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                FeatureNavigationCard(
                    icon = Icons.Default.CalendarMonth,
                    iconBgColor = Color(0xFFFEF3C7), // Amber 100
                    iconTintColor = OrbitAmber,
                    title = "Daily Check-in",
                    subtitle = "Reflect and reset",
                    onClick = onNavigateToDailyCheckin
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                FeatureNavigationCard(
                    icon = Icons.Default.MenuBook,
                    iconBgColor = OrbitSurfaceVariant,
                    iconTintColor = OrbitTextSecondary,
                    title = "Learn",
                    subtitle = "Dopamine & the Habit Loop",
                    onClick = onNavigateToLearn
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                FeatureNavigationCard(
                    icon = Icons.Default.Shield,
                    iconBgColor = OrbitPrimaryLight,
                    iconTintColor = OrbitPrimary,
                    title = "Screen Time & Safety",
                    subtitle = "App limits & content filter",
                    onClick = onNavigateToScreenTime
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                FeatureNavigationCard(
                    icon = Icons.Default.Person,
                    iconBgColor = OrbitSurfaceVariant,
                    iconTintColor = OrbitTextSecondary,
                    title = "Profile (Lvl 1)",
                    subtitle = "Settings and preferences",
                    onClick = onNavigateToProfile
                )
                
            }
        }
    }
}

@Composable
fun FeatureNavigationCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        color = OrbitSurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OrbitBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = Typography.titleLarge, color = OrbitTextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = Typography.bodyMedium, color = OrbitTextSecondary)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = OrbitTextMuted
            )
        }
    }
}
