package com.orbit.recovery.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.orbit.recovery.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.orbit.recovery.AppBlockerService
import com.orbit.recovery.OrbitVpnService
import com.orbit.recovery.ui.components.*
import com.orbit.recovery.ui.theme.*

enum class BreathPhase(val label: String, val targetScale: Float) {
    INHALE("INHALE", 1.3f),
    HOLD_IN("HOLD", 1.3f),
    EXHALE("EXHALE", 1.0f),
    HOLD_OUT("HOLD", 1.0f)
}

@Composable
fun PanicScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCoach: () -> Unit
) {
    val context = LocalContext.current
    var shieldActive by remember { mutableStateOf(false) }
    var isBreathing by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var cyclesCompleted by remember { mutableStateOf(0) }

    val scale by animateFloatAsState(
        targetValue = currentPhase.targetScale,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
        label = "breathScale"
    )

    val vpnLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val vpnIntent = Intent(context, OrbitVpnService::class.java)
                val blockerIntent = Intent(context, AppBlockerService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    context.startForegroundService(vpnIntent)
                    context.startForegroundService(blockerIntent)
                } else {
                    context.startService(vpnIntent)
                    context.startService(blockerIntent)
                }
                shieldActive = true
            }
            isBreathing = true
        }

    LaunchedEffect(isBreathing) {
        if (isBreathing) {
            while (true) {
                currentPhase = BreathPhase.INHALE
                delay(4000)
                currentPhase = BreathPhase.HOLD_IN
                delay(4000)
                currentPhase = BreathPhase.EXHALE
                delay(4000)
                currentPhase = BreathPhase.HOLD_OUT
                delay(4000)
                cyclesCompleted++
            }
        }
    }

    Surface(
        modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            color = OrbitPanicBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.orbit_logo),
                        contentDescription = "Orbit Logo",
                        modifier = Modifier.height(24.dp).wrapContentWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "← Back",
                        style = Typography.labelSmall,
                        color = OrbitTextPrimary,
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                }

                if (shieldActive) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = OrbitPrimary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shield ON", style = Typography.labelSmall, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "A calmer next step",
                            style = Typography.headlineLarge,
                            color = OrbitTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You don't have to push through alone.",
                            style = Typography.bodyLarge,
                            color = OrbitTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (!isBreathing && cyclesCompleted == 0) {
                            Button(
                                onClick = {
                                    val intent = VpnService.prepare(context)
                                    if (intent != null) {
                                        vpnLauncher.launch(intent)
                                    } else {
                                        val vpnIntent = Intent(context, OrbitVpnService::class.java)
                                        val blockerIntent =
                                            Intent(context, AppBlockerService::class.java)
                                        if (android.os.Build.VERSION.SDK_INT >= 26) {
                                            context.startForegroundService(vpnIntent)
                                            context.startForegroundService(blockerIntent)
                                        } else {
                                            context.startService(vpnIntent)
                                            context.startService(blockerIntent)
                                        }
                                        shieldActive = true
                                        isBreathing = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrbitPanicButton)
                            ) {
                                Text(
                                    text = "🛡 Activate Panic Shield",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Starts breathing guide · Enables DNS content filter · Activates app limits",
                                style = Typography.bodyMedium,
                                color = OrbitTextMuted,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Breathing UI
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 32.dp)
                            ) {
                                // Outer
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .scale(scale)
                                        .border(2.dp, OrbitPanicButton, CircleShape)
                                )
                                // Inner
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .border(1.5.dp, OrbitPanicButton, CircleShape)
                                )
                                Text(
                                    text = currentPhase.label,
                                    color = OrbitPanicButton,
                                    style = Typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                    )
                                )
                            }

                            Text(
                                text = "Cycle ${cyclesCompleted + 1} of 4",
                                style = Typography.labelSmall,
                                color = OrbitTextMuted
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = cyclesCompleted >= 2,
                    enter = fadeIn(animationSpec = tween(1000)) + expandVertically()
                ) {
                    var selectedGrounding by remember { mutableStateOf(setOf<Int>()) }
                    val items = listOf(
                        "👁 5 things you can SEE",
                        "✋ 4 things you can TOUCH",
                        "👂 3 things you can HEAR",
                        "👃 2 things you can SMELL",
                        "👅 1 thing you can TASTE"
                    )

                    Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                        Text(
                            text = "5-4-3-2-1 GROUNDING",
                            style = Typography.labelSmall,
                            color = OrbitTextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        items.forEachIndexed { index, item ->
                            val isSelected = selectedGrounding.contains(index)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) OrbitPrimary else OrbitBorder
                                ),
                                color = if (isSelected) OrbitPrimaryLight else OrbitSurface,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedGrounding =
                                        if (isSelected) selectedGrounding - index else selectedGrounding + index
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item,
                                        style = Typography.bodyLarge.copy(
                                            textDecoration = if (isSelected) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        color = if (isSelected) OrbitTextMuted else OrbitTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = OrbitPrimary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (selectedGrounding.size == 5) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "You made it through. Well done. 💛",
                                style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = OrbitPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Talk to Coach →",
                    style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = OrbitPrimary,
                    modifier = Modifier.clickable { onNavigateToCoach() }.padding(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "← Back to Home",
                    style = Typography.bodyMedium,
                    color = OrbitTextMuted,
                    modifier = Modifier.clickable { onNavigateBack() }.padding(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }