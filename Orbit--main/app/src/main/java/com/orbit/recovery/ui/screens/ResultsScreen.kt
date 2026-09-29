package com.orbit.recovery.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.components.OrbitPrimaryButton
import com.orbit.recovery.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultsScreen(
    userName: String = "Alex", // could be fetched from datastore
    onNavigateToHome: () -> Unit,
    viewModel: ResultsViewModel = viewModel()
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = OrbitBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 1 - Header & Timeline
            OrbitCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = OrbitPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RESULTS",
                            style = Typography.labelSmall,
                            color = OrbitPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your next 90 days, $userName",
                        style = Typography.headlineLarge,
                        color = OrbitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Here's a gentle roadmap with milestones people often report. You can go at your own pace.",
                        style = Typography.bodyLarge,
                        color = OrbitTextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Surface(
                        color = OrbitSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "RECOVERY TIMELINE",
                                style = Typography.labelSmall,
                                color = OrbitTextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val textMeasurer = rememberTextMeasurer()
                            
                            Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                                val width = size.width
                                val yCenter = 30.dp.toPx()
                                
                                drawLine(
                                    color = OrbitBorder,
                                    start = Offset(0f, yCenter),
                                    end = Offset(width, yCenter),
                                    strokeWidth = 2.dp.toPx()
                                )
                                
                                val points = listOf(0f, 0.33f, 0.66f, 1f)
                                val labels = listOf("Day 0", "Day 30", "Day 60", "Day 90")
                                
                                points.forEachIndexed { index, percent ->
                                    val x = percent * (width - 40.dp.toPx()) + 20.dp.toPx()
                                    
                                    if (index == 0) {
                                        drawCircle(
                                            color = OrbitPrimary,
                                            radius = 6.dp.toPx(),
                                            center = Offset(x, yCenter)
                                        )
                                    } else {
                                        drawCircle(
                                            color = OrbitPrimary,
                                            radius = 6.dp.toPx(),
                                            center = Offset(x, yCenter),
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                        drawCircle(
                                            color = OrbitSurfaceVariant,
                                            radius = 4.dp.toPx(),
                                            center = Offset(x, yCenter)
                                        )
                                    }
                                    
                                    val textLayoutResult = textMeasurer.measure(labels[index])
                                    drawText(
                                        textLayoutResult = textLayoutResult,
                                        color = OrbitTextSecondary,
                                        topLeft = Offset(x - textLayoutResult.size.width / 2, yCenter + 12.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val milestones = listOf(
                        "Day 30" to "Often reported: steadier mood",
                        "Day 60" to "Can improve: focus & energy",
                        "Day 90" to "Many report: more confidence"
                    )
                    
                    milestones.forEach { (day, text) ->
                        Surface(
                            color = OrbitSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = OrbitPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = day, style = Typography.labelSmall, color = OrbitPrimary)
                                    Text(text = text, style = Typography.bodyMedium, color = OrbitTextPrimary)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cautious guidance only. Improvements vary; no medical guarantees.",
                        style = Typography.labelSmall,
                        color = OrbitTextMuted
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 2 - Benefits
            OrbitCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("BENEFITS (OFTEN REPORTED)", style = Typography.labelSmall, color = OrbitTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("What may improve over time", style = Typography.titleLarge, color = OrbitTextPrimary)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val benefits = listOf("Energy", "Focus", "Motivation", "Relationships", "Confidence")
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        benefits.forEach { benefit ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, OrbitBorder),
                                color = OrbitSurface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome, 
                                        contentDescription = null, 
                                        tint = OrbitAmber, 
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = benefit,
                                        style = Typography.bodyMedium,
                                        color = OrbitTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 3 - Privacy card
            Surface(
                color = OrbitSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = OrbitTextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PRIVACY", style = Typography.labelSmall, color = OrbitTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This prototype stores your choices on-device (local storage). There's no account, no server, and no sharing.",
                        style = Typography.bodyMedium,
                        color = OrbitTextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OrbitPrimaryButton(
                text = "Begin my recovery journey →",
                onClick = {
                    viewModel.completeOnboarding {
                        onNavigateToHome()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
