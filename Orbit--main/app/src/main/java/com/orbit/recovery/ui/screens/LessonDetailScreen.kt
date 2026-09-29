package com.orbit.recovery.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.orbit.recovery.ui.components.OrbitPrimaryButton
import com.orbit.recovery.ui.theme.*

@Composable
fun LessonDetailScreen(
    lessonId: String,
    onNavigateBack: () -> Unit,
    viewModel: LearnViewModel = viewModel()
) {
    val lesson = libraryLessons.find { it.id == lessonId } ?: return
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    val completedLessons by viewModel.completedLessons.collectAsState()
    val isCompleted = completedLessons.contains(lessonId)

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
                    text = lesson.title,
                    style = Typography.labelSmall,
                    color = OrbitTextMuted,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(lesson.emoji, fontSize = 48.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(lesson.title, style = Typography.headlineLarge, color = OrbitTextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${lesson.readTime} min read", style = Typography.labelSmall, color = OrbitTextSecondary)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = isCompleted,
                                onCheckedChange = { 
                                    if(it) {
                                        viewModel.markLessonComplete(lessonId) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Lesson complete! 🌿")
                                                delay(1000)
                                                onNavigateBack()
                                            }
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark complete", style = Typography.labelSmall, color = OrbitTextSecondary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                items(lesson.sections) { section ->
                    Text(section.heading, style = Typography.titleLarge, color = OrbitPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = section.body,
                        style = Typography.bodyLarge,
                        color = OrbitTextPrimary,
                        lineHeight = 28.sp 
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            
            // Bottom Sticky Button
            if (!isCompleted) {
                Surface(
                    color = OrbitSurface,
                    border = BorderStroke(1.dp, OrbitBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        OrbitPrimaryButton(
                            text = "Mark as Complete",
                            onClick = {
                                viewModel.markLessonComplete(lessonId) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Lesson complete! 🌿")
                                        onNavigateBack()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
