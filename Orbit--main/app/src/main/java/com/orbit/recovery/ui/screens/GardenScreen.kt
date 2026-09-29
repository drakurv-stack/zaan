package com.orbit.recovery.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.orbit.recovery.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.ui.components.OrbitBottomNav
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.components.OrbitPrimaryButton
import com.orbit.recovery.ui.components.OrbitTab
import com.orbit.recovery.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(
    onNavigateTab: (OrbitTab) -> Unit,
    onNavigateToCheckin: () -> Unit,
    viewModel: GardenViewModel = viewModel()
) {
    val streak by viewModel.streak.collectAsState()

    val stageName = when {
        streak < 7 -> "Origin Seed"
        streak < 30 -> "Tiny Sprout"
        streak < 60 -> "Growing Plant"
        streak < 90 -> "Thriving Plant"
        else -> "Rooted Tree"
    }

    Scaffold(
        bottomBar = { OrbitBottomNav(currentTab = OrbitTab.Garden, onTabSelected = onNavigateTab) },
        containerColor = OrbitBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text("ORBIT / Your garden", style = Typography.labelSmall, color = OrbitTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stageName, style = Typography.headlineMedium, color = OrbitTextPrimary)
                }
                Spacer(modifier = Modifier.height(24.dp))

                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Canvas(modifier = Modifier.size(240.dp)) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val groundY = center.y + 80.dp.toPx()
                            
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(OrbitPrimaryLight, OrbitBackground),
                                    center = center,
                                    radius = size.width / 2
                                ),
                                radius = size.width / 2,
                                center = center
                            )
                            
                            when {
                                streak < 7 -> {
                                    drawOval(
                                        color = Color(0xFF8B5A2B),
                                        topLeft = Offset(center.x - 10.dp.toPx(), groundY - 20.dp.toPx()),
                                        size = Size(20.dp.toPx(), 15.dp.toPx())
                                    )
                                }
                                streak < 30 -> {
                                    drawLine(
                                        color = OrbitPrimary,
                                        start = Offset(center.x, groundY),
                                        end = Offset(center.x, groundY - 40.dp.toPx()),
                                        strokeWidth = 4.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                    drawOval(color = OrbitPrimary, topLeft = Offset(center.x, groundY - 30.dp.toPx()), size = Size(15.dp.toPx(), 8.dp.toPx()))
                                    drawOval(color = OrbitPrimary, topLeft = Offset(center.x - 15.dp.toPx(), groundY - 20.dp.toPx()), size = Size(15.dp.toPx(), 8.dp.toPx()))
                                }
                                streak < 60 -> {
                                    drawLine(
                                        color = OrbitPrimary,
                                        start = Offset(center.x, groundY),
                                        end = Offset(center.x, groundY - 80.dp.toPx()),
                                        strokeWidth = 6.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                    drawOval(color = OrbitPrimary, topLeft = Offset(center.x, groundY - 60.dp.toPx()), size = Size(20.dp.toPx(), 10.dp.toPx()))
                                    drawOval(color = OrbitPrimary, topLeft = Offset(center.x - 20.dp.toPx(), groundY - 45.dp.toPx()), size = Size(20.dp.toPx(), 10.dp.toPx()))
                                    drawOval(color = OrbitPrimary, topLeft = Offset(center.x, groundY - 30.dp.toPx()), size = Size(25.dp.toPx(), 12.dp.toPx()))
                                    drawOval(color = OrbitPrimary, topLeft = Offset(center.x - 25.dp.toPx(), groundY - 15.dp.toPx()), size = Size(25.dp.toPx(), 12.dp.toPx()))
                                }
                                streak < 90 -> {
                                    drawLine(
                                        color = OrbitPrimary,
                                        start = Offset(center.x, groundY),
                                        end = Offset(center.x, groundY - 120.dp.toPx()),
                                        strokeWidth = 8.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                    for (i in 1..4) {
                                        val yOffset = groundY - (i * 25).dp.toPx()
                                        drawOval(color = OrbitPrimary, topLeft = Offset(center.x, yOffset), size = Size(30.dp.toPx(), 15.dp.toPx()))
                                        drawOval(color = OrbitPrimary, topLeft = Offset(center.x - 30.dp.toPx(), yOffset + 10.dp.toPx()), size = Size(30.dp.toPx(), 15.dp.toPx()))
                                    }
                                }
                                else -> {
                                    drawLine(
                                        color = Color(0xFF6B4423),
                                        start = Offset(center.x, groundY),
                                        end = Offset(center.x, groundY - 120.dp.toPx()),
                                        strokeWidth = 16.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                    drawCircle(color = OrbitPrimary, center = Offset(center.x, groundY - 120.dp.toPx()), radius = 80.dp.toPx())
                                    drawCircle(color = OrbitPrimary.copy(alpha = 0.8f), center = Offset(center.x - 40.dp.toPx(), groundY - 90.dp.toPx()), radius = 50.dp.toPx())
                                    drawCircle(color = OrbitPrimary.copy(alpha = 0.9f), center = Offset(center.x + 40.dp.toPx(), groundY - 100.dp.toPx()), radius = 60.dp.toPx())
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("$streak days of growth", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("STAGE", style = Typography.labelSmall, color = OrbitTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stageName, style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Every day you don't act on the urge, your roots grow deeper.", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        OrbitPrimaryButton(
                            text = "Mark Today Complete",
                            onClick = onNavigateToCheckin
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text("Milestones ahead", style = Typography.titleMedium, color = OrbitTextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            val milestones = listOf(
                Pair(7, "First week free"),
                Pair(30, "A solid month"),
                Pair(60, "Two months of clarity"),
                Pair(90, "The 90-day reset"),
                Pair(180, "Half a year strong")
            )
            
            items(milestones.size) { index ->
                val (day, text) = milestones[index]
                val isReached = streak >= day
                OrbitCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(if (isReached) OrbitPrimaryLight else OrbitSurfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isReached) "✓" else "🔒",
                                color = if (isReached) OrbitPrimary else OrbitTextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Day $day", style = Typography.bodyMedium, color = if (isReached) OrbitTextPrimary else OrbitTextSecondary, fontWeight = FontWeight.SemiBold)
                            Text(text, style = Typography.labelSmall, color = OrbitTextSecondary)
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
