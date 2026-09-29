package com.orbit.recovery.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.ui.components.*
import com.orbit.recovery.ui.theme.*

@Composable
fun PersonalizationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResults: () -> Unit,
    onSkip: () -> Unit,
    viewModel: PersonalizationViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(1) }
    
    // States for answers
    var selectedReasons by remember { mutableStateOf(setOf<String>()) }
    var targetHabit by remember { mutableStateOf("") }
    var routineDuration by remember { mutableStateOf("") }
    var usageIncreased by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    val totalSteps = 5
    val percentage = (currentStep.toFloat() / totalSteps * 100).toInt()
    
    Surface(modifier = Modifier.fillMaxSize(), color = OrbitBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo on the far left
                Image(
                    painter = painterResource(id = R.drawable.orbit_logo),
                    contentDescription = "Orbit",
                    modifier = Modifier.height(24.dp).wrapContentWidth(),
                    contentScale = ContentScale.Fit
                )
                // Back button in center
                Surface(
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, OrbitBorder),
                    color = Color.Transparent,
                    onClick = { if (currentStep > 1) currentStep-- else onNavigateBack() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back",
                            modifier = Modifier.size(16.dp), tint = OrbitTextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", style = Typography.labelSmall, color = OrbitTextPrimary)
                    }
                }
                // Skip on the far right
                Text(
                    text = "Skip",
                    style = Typography.labelSmall,
                    color = OrbitTextSecondary,
                    modifier = Modifier.clickable { onSkip() }.padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                OrbitCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        // Question Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question $currentStep of $totalSteps",
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
                        
                        // Dynamic Question Content
                        AnimatedContent(targetState = currentStep, label = "QuestionContent") { step ->
                            Column {
                                val questionTitle = getQuestionTitle(step)
                                val subtitle = getQuestionSubtitle(step)
                                
                                Text(
                                    text = questionTitle,
                                    style = Typography.headlineLarge,
                                    color = OrbitTextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = subtitle,
                                    style = Typography.bodyLarge,
                                    color = OrbitTextSecondary
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Options
                                when (step) {
                                    1 -> {
                                        val options = listOf("Regain control over my time", "Improve focus and energy", "Feel more confident", "Strengthen relationships", "Reduce shame and spirals", "Build better habits")
                                        options.forEach { option ->
                                            OrbitOptionRow(
                                                text = option,
                                                isSelected = selectedReasons.contains(option),
                                                onClick = {
                                                    selectedReasons = if (selectedReasons.contains(option)) {
                                                        selectedReasons - option
                                                    } else {
                                                        selectedReasons + option
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                    2 -> {
                                        val options = listOf("Pornography / adult content", "Social media & scrolling", "Gaming", "Gambling", "Alcohol or substances", "Other compulsive behavior")
                                        options.forEach { option ->
                                            OrbitOptionRow(
                                                text = option,
                                                isSelected = targetHabit == option,
                                                onClick = { targetHabit = option }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                    3 -> {
                                        val options = listOf("Less than 6 months", "6 months – 2 years", "2–5 years", "5–10 years", "10+ years")
                                        options.forEach { option ->
                                            OrbitOptionRow(
                                                text = option,
                                                isSelected = routineDuration == option,
                                                onClick = { routineDuration = option }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                    4 -> {
                                        val options = listOf("Yes", "No", "Not sure")
                                        options.forEach { option ->
                                            OrbitOptionRow(
                                                text = option,
                                                isSelected = usageIncreased == option,
                                                onClick = { usageIncreased = option }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                    5 -> {
                                        OrbitTextField(
                                            value = name,
                                            onValueChange = { name = it },
                                            hint = "Alex",
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = OrbitTextMuted,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        val isContinueEnabled = when (currentStep) {
                            1 -> selectedReasons.isNotEmpty()
                            2 -> targetHabit.isNotEmpty()
                            3 -> routineDuration.isNotEmpty()
                            4 -> usageIncreased.isNotEmpty()
                            5 -> name.isNotBlank()
                            else -> true
                        }
                        
                        val buttonText = if (currentStep == totalSteps) "Build my plan →" else "Continue"
                        
                        OrbitPrimaryButton(
                            text = buttonText,
                            enabled = isContinueEnabled,
                            onClick = {
                                if (currentStep < totalSteps) {
                                    currentStep++
                                } else {
                                    viewModel.saveQuizResults(
                                        name, selectedReasons, targetHabit, "",
                                        routineDuration, usageIncreased, "", "", ""
                                    )
                                    onNavigateToResults()
                                }
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your answers are stored on this device (local storage) for this prototype. You can reset from Profile anytime.",
                    style = Typography.bodyMedium,
                    color = OrbitTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

fun getQuestionTitle(step: Int) = when(step) {
    1 -> "What brings you here?"
    2 -> "What are you trying to change?"
    3 -> "How long has this been part of your routine?"
    4 -> "Has your usage increased over time?"
    5 -> "What should we call you?"
    else -> ""
}

fun getQuestionSubtitle(step: Int) = when(step) {
    1 -> "Choose as many as you like. We'll tailor your plan around what matters most."
    2 -> "No judgment here. This is just for context."
    3 -> "Pick what feels closest. You can always update later."
    4 -> "No judgment — this is common, and awareness is progress."
    5 -> "A first name or nickname is perfect."
    else -> ""
}
