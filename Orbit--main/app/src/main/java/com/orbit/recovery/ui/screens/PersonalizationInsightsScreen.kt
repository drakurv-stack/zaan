package com.orbit.recovery.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.orbit.recovery.ui.components.*
import com.orbit.recovery.ui.theme.*

@Composable
fun PersonalizationInsightsScreen(
    userName: String = "Alex",
    onNavigateBack: () -> Unit,
    onNavigateToResults: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 6
    val percentage = (currentStep.toFloat() / totalSteps * 100).toInt()
    
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    
    Surface(modifier = Modifier.fillMaxSize(), color = OrbitBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, OrbitBorder),
                    color = Color.Transparent,
                    onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onNavigateBack()
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                
                Text(
                    text = "Skip",
                    style = Typography.labelSmall,
                    color = OrbitTextSecondary,
                    modifier = Modifier.clickable { onSkip() }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Personalization $currentStep of $totalSteps",
                                style = Typography.labelSmall,
                                color = OrbitTextMuted
                            )
                            Text(
                                text = "$percentage%",
                                style = Typography.labelSmall,
                                color = OrbitPrimary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { currentStep.toFloat() / totalSteps },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = OrbitPrimary,
                            trackColor = OrbitBorder
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        AnimatedContent(targetState = currentStep, label = "InsightContent") { step ->
                            Column {
                                val title = getInsightTitle(step, userName)
                                val subtitle = getInsightSubtitle(step)
                                val icon = getInsightIcon(step)
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = title,
                                        style = Typography.headlineLarge,
                                        color = OrbitTextPrimary,
                                        modifier = Modifier.weight(1f).padding(end = 16.dp)
                                    )
                                    
                                    if (icon != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(OrbitSurfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = OrbitPrimary
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = subtitle,
                                    style = Typography.bodyLarge,
                                    color = OrbitTextSecondary
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                when (step) {
                                    1 -> InsightStep1()
                                    2 -> InsightStep2()
                                    3 -> InsightStep3()
                                    4 -> {
                                        val options = listOf("Low motivation", "Brain fog", "Anxiety or restlessness", "Sleep issues", "Lower confidence", "Trouble focusing", "Social withdrawal")
                                        options.forEach { option ->
                                            OrbitOptionRow(
                                                text = option,
                                                isSelected = selectedSymptoms.contains(option),
                                                onClick = {
                                                    selectedSymptoms = if (selectedSymptoms.contains(option)) {
                                                        selectedSymptoms - option
                                                    } else {
                                                        selectedSymptoms + option
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                    5 -> InsightStep5()
                                    6 -> InsightStep6()
                                }
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                val btnText = if (step == 6) "✏ I commit to myself" else "Continue"
                                OrbitPrimaryButton(
                                    text = btnText,
                                    onClick = {
                                        if (currentStep < totalSteps) {
                                            currentStep++
                                        } else {
                                            onNavigateToResults()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                if (currentStep in listOf(1, 2, 3, 5)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    PrivacyDisclaimerCard()
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InsightStep1() {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("COMMUNITY", style = Typography.labelSmall, color = OrbitTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("This week's momentum", style = Typography.titleLarge, color = OrbitTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = OrbitSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Folded (reset)", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("38%", style = Typography.headlineMedium, color = OrbitTextPrimary)
                    }
                }
                Surface(
                    color = OrbitSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Still going strong", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("62%", style = Typography.headlineMedium, color = OrbitPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Example stats for prototype only.", style = Typography.bodyMedium, color = OrbitTextMuted)
        }
    }
}

@Composable
fun InsightStep2() {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ESTIMATE", style = Typography.labelSmall, color = OrbitTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Annual minutes", style = Typography.bodyMedium, color = OrbitTextSecondary)
            Text("4,380", style = Typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = OrbitTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = OrbitSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("That's about", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("73 hours", style = Typography.titleLarge, color = OrbitTextPrimary)
                    }
                }
                Surface(
                    color = OrbitSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Roughly", style = Typography.bodyMedium, color = OrbitTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("1.8 work weeks", style = Typography.titleLarge, color = OrbitTextPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Example calculation for prototype only. Adjust later.", style = Typography.bodyMedium, color = OrbitTextMuted)
        }
    }
}

@Composable
fun InsightStep3() {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("COMPARISON / You vs Average", style = Typography.labelSmall, color = OrbitTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("You", style = Typography.titleLarge, color = OrbitTextPrimary)
                Text("7/10", style = Typography.titleLarge, color = OrbitTextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OrbitBorder)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Average", style = Typography.bodyLarge, color = OrbitTextSecondary)
                Text("4/10", style = Typography.bodyLarge, color = OrbitTextSecondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Informational only, not a medical diagnosis.", style = Typography.bodyMedium, color = OrbitTextMuted)
        }
    }
}

@Composable
fun InsightStep5() {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("HOPE", style = Typography.labelSmall, color = OrbitTextSecondary)
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = OrbitAmber, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your nervous system can recover", style = Typography.titleLarge, color = OrbitTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Many people report better energy, focus, and mood after a period of consistency. Your path is unique — no pressure, no perfection.", style = Typography.bodyLarge, color = OrbitTextSecondary)
        }
    }
}

@Composable
fun InsightStep6() {
    var path by remember { mutableStateOf(Path()) }
    var lastX by remember { mutableStateOf(0f) }
    var lastY by remember { mutableStateOf(0f) }

    Column {
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, OrbitBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("COMMITMENT", style = Typography.labelSmall, color = OrbitTextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                
                val commitments = listOf(
                    "I will protect my sleep",
                    "I will practice a 60-second reset when urges spike",
                    "I will ask for support instead of isolating"
                )
                commitments.forEach { text ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = OrbitPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text, style = Typography.bodyLarge, color = OrbitTextPrimary)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            color = OrbitSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SIGNATURE", style = Typography.labelSmall, color = OrbitTextSecondary)
                    Text(
                        "Clear", 
                        style = Typography.labelSmall, 
                        color = OrbitPrimary,
                        modifier = Modifier.clickable { path = Path() }.padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OrbitSurfaceVariant, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        path = Path().also { 
                                            it.addPath(path)
                                            it.moveTo(offset.x, offset.y) 
                                        }
                                        lastX = offset.x
                                        lastY = offset.y
                                    },
                                    onDrag = { change, _ ->
                                        val x = change.position.x
                                        val y = change.position.y
                                        path = Path().apply {
                                            addPath(path)
                                            quadraticBezierTo(lastX, lastY, (lastX + x) / 2f, (lastY + y) / 2f)
                                        }
                                        lastX = x
                                        lastY = y
                                        change.consume()
                                    }
                                )
                            }
                    ) {
                        drawPath(path = path, color = OrbitTextPrimary, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Visual-only signature for the prototype. Not legally binding.", style = Typography.bodyMedium, color = OrbitTextMuted)
            }
        }
    }
}

@Composable
fun PrivacyDisclaimerCard() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = OrbitTextMuted, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text("PRIVACY", style = Typography.labelSmall, color = OrbitTextMuted)
            Text("Your answers are stored on this device (local storage) for this prototype. You can reset from Profile anytime.", style = Typography.bodyMedium, color = OrbitTextMuted)
        }
    }
}

fun getInsightTitle(step: Int, name: String) = when(step) {
    1 -> "You are not alone"
    2 -> "Time has a hidden cost"
    3 -> "A quick dependency snapshot"
    4 -> "What symptoms show up for you?"
    5 -> "Good news."
    6 -> "A small ritual, $name"
    else -> ""
}

fun getInsightSubtitle(step: Int) = when(step) {
    1 -> "Many people are rebuilding their relationship with their habits. You're joining a real movement of change."
    2 -> "If you're watching ~12 minutes/day, that's about 4,380 minutes per year."
    3 -> "This is informational only — not a medical diagnosis. It can help you track patterns over time."
    4 -> "Select any that feel familiar. You can skip this."
    5 -> "These symptoms can often improve as your brain rewires and you build new habits. We'll take it one day at a time."
    6 -> "Make a gentle commitment. It's okay to stumble — we focus on returning."
    else -> ""
}

fun getInsightIcon(step: Int) = when(step) {
    1 -> Icons.Default.Group
    2 -> Icons.Default.Timer
    3 -> Icons.Default.BarChart
    5 -> Icons.Default.AutoAwesome
    6 -> Icons.Default.Article
    else -> null
}
