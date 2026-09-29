package com.orbit.recovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.orbit.recovery.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orbit.recovery.ui.components.OrbitBottomNav
import com.orbit.recovery.ui.components.OrbitCard
import com.orbit.recovery.ui.components.OrbitTab
import com.orbit.recovery.ui.theme.*

@Composable
fun LearnScreen(
    onNavigateTab: (OrbitTab) -> Unit,
    onNavigateToLesson: (String) -> Unit,
    viewModel: LearnViewModel = viewModel()
) {
    val completedLessons by viewModel.completedLessons.collectAsState()

    Scaffold(
        bottomBar = {
            OrbitBottomNav(currentTab = OrbitTab.Learn, onTabSelected = onNavigateTab)
        },
        containerColor = OrbitBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("ORBIT / Learn", style = Typography.labelSmall, color = OrbitTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Recovery Library", style = Typography.headlineLarge, color = OrbitTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Build your understanding one lesson at a time.",
                        style = Typography.bodyLarge,
                        color = OrbitTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(libraryLessons) { lesson ->
                val isCompleted = completedLessons.contains(lesson.id)
                OrbitCard(modifier = Modifier.fillMaxWidth().clickable { onNavigateToLesson(lesson.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(OrbitSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(lesson.emoji, style = Typography.headlineMedium)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(lesson.title, style = Typography.titleLarge, color = OrbitTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(lesson.subtitle, style = Typography.bodyMedium, color = OrbitTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = OrbitPrimary)
                        } else {
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = OrbitTextMuted)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
