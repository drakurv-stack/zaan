package com.orbit.recovery.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.orbit.recovery.ui.components.*
import com.orbit.recovery.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckinScreen(
    onNavigateBack: () -> Unit,
    viewModel: DailyCheckinViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    var mood by remember { mutableStateOf<Int?>(null) }
    var urgeLevel by remember { mutableStateOf(0f) }
    var triggers by remember { mutableStateOf(setOf<String>()) }
    var winText by remember { mutableStateOf("") }
    var stayedStrong by remember { mutableStateOf<Boolean?>(null) }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OrbitBackground
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                    text = "Daily Check-In",
                    style = Typography.titleLarge,
                    color = OrbitTextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(72.dp)) 
            }
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                // Section 1 - MOOD
                item {
                    OrbitCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("MOOD", style = Typography.labelSmall, color = OrbitTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("How are you feeling today?", style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val emojis = listOf("😔", "😐", "🙂", "😊", "😄")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                emojis.forEachIndexed { index, emoji ->
                                    val number = index + 1
                                    val isSelected = mood == number
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) OrbitPrimaryLight else OrbitSurfaceVariant)
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) OrbitPrimary else OrbitBorder,
                                                    shape = CircleShape
                                                )
                                                .clickable { mood = number },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(emoji, style = Typography.headlineMedium)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("$number", style = Typography.labelSmall, color = OrbitTextMuted)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Section 2 - URGE LEVEL
                item {
                    OrbitCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("URGE LEVEL", style = Typography.labelSmall, color = OrbitTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Urge level right now?", style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Surface(
                                    color = OrbitSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, OrbitBorder)
                                ) {
                                    Text(
                                        text = "${urgeLevel.toInt()}",
                                        style = Typography.titleMedium,
                                        color = OrbitTextPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            
                            Slider(
                                value = urgeLevel,
                                onValueChange = { urgeLevel = it },
                                valueRange = 0f..10f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = OrbitPrimary,
                                    inactiveTrackColor = OrbitBorder
                                ),
                                thumb = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(2.dp, OrbitPrimary, CircleShape)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )
                            
                            val (contextText, contextColor) = when (urgeLevel.toInt()) {
                                in 0..2 -> "Feeling clear 🌿" to OrbitPrimary
                                in 3..5 -> "Manageable 🌊" to OrbitTextSecondary
                                in 6..8 -> "Stay strong 💪" to OrbitAmber
                                else -> "Reach out now" to OrbitPanicButton
                            }
                            
                            Text(
                                text = contextText,
                                style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = contextColor,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Section 3 - TRIGGERS
                item {
                    OrbitCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text("TRIGGERS", style = Typography.labelSmall, color = OrbitTextSecondary)
                                Text("Select all that apply", style = Typography.bodyMedium, color = OrbitTextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("What triggered you today?", style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val triggerOptions = listOf(
                                "Boredom", "Stress", "Loneliness", "Social media", 
                                "Substance nearby", "Relationship issues", "Work pressure", "Nothing today"
                            )
                            triggerOptions.forEach { trigger ->
                                OrbitOptionRow(
                                    text = trigger,
                                    isSelected = triggers.contains(trigger),
                                    onClick = {
                                        triggers = if (triggers.contains(trigger)) {
                                            triggers - trigger
                                        } else {
                                            triggers + trigger
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Section 4 - YOUR WIN
                item {
                    OrbitCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("YOUR WIN", style = Typography.labelSmall, color = OrbitTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Any wins today?", style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OrbitTextField(
                                value = winText,
                                onValueChange = { winText = it },
                                hint = "Something you're proud of, however small...",
                                modifier = Modifier.fillMaxWidth().height(100.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Section 5 - HONEST CHECK
                item {
                    OrbitCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("HONEST CHECK", style = Typography.labelSmall, color = OrbitTextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val isStayedStrong = stayedStrong == true
                                Surface(
                                    color = if (isStayedStrong) OrbitPrimaryLight else OrbitSurfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(width = if (isStayedStrong) 2.dp else 1.dp, color = if (isStayedStrong) OrbitPrimary else OrbitBorder),
                                    modifier = Modifier.weight(1f).clickable { stayedStrong = true }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("✅", style = Typography.headlineMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Stayed strong", style = Typography.bodyMedium, color = OrbitTextPrimary, textAlign = TextAlign.Center)
                                    }
                                }
                                
                                val isSetback = stayedStrong == false
                                Surface(
                                    color = if (isSetback) Color(0xFFFFF3E0) else OrbitSurfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(width = if (isSetback) 2.dp else 1.dp, color = if (isSetback) OrbitAmber else OrbitBorder),
                                    modifier = Modifier.weight(1f).clickable { stayedStrong = false }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("💛", style = Typography.headlineMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Had a setback", style = Typography.bodyMedium, color = OrbitTextPrimary, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                            
                            if (stayedStrong == false) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "That takes courage. Let's reset together.",
                                    style = Typography.bodyMedium,
                                    color = OrbitPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            
            // Bottom Sticky Button
            Surface(
                color = OrbitSurface,
                border = BorderStroke(1.dp, OrbitBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    val isEnabled = mood != null && stayedStrong != null
                    OrbitPrimaryButton(
                        text = "Save My Check-In",
                        enabled = isEnabled,
                        onClick = {
                            viewModel.saveCheckin(
                                mood = mood!!,
                                urgeLevel = urgeLevel.toInt(),
                                triggers = triggers.toList(),
                                win = winText,
                                stayedStrong = stayedStrong!!,
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Check-in saved 🌿")
                                        onNavigateBack()
                                    }
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
