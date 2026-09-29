package com.orbit.recovery.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.orbit.recovery.ui.components.OrbitTextField
import com.orbit.recovery.ui.theme.*

@Composable
fun CoachScreen(
    onNavigateBack: () -> Unit,
    viewModel: CoachViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            val targetIndex = if (isLoading) messages.size else messages.size - 1
            listState.animateScrollToItem(targetIndex)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = OrbitBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.orbit_logo),
                        contentDescription = "Orbit",
                        modifier = Modifier.height(26.dp).wrapContentWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Text("Your AI companion", style = Typography.labelSmall, color = OrbitTextMuted)
                }
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(72.dp)) 
            }

            // Privacy Disclaimer
            Text(
                text = "Orbit Coach is an AI-powered support tool. It does not provide medical advice. If you are in crisis, please contact local emergency services.",
                style = Typography.bodyMedium,
                color = OrbitTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                if (isLoading) {
                    item {
                        TypingIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Quick Actions
            if (messages.size < 4 && !isLoading) {
                val suggestions = listOf("I feel an urge", "I'm stressed", "I relapsed", "I'm proud of myself")
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestions) { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, OrbitBorder),
                            color = Color.Transparent,
                            onClick = { inputText = suggestion },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = suggestion,
                                    style = Typography.labelSmall,
                                    color = OrbitTextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = OrbitSurface,
                border = BorderStroke(width = 1.dp, color = OrbitBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OrbitTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            hint = "Talk to your coach...",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank() && !isLoading) OrbitPrimary else OrbitTextMuted.copy(alpha = 0.5f))
                            .clickable(enabled = inputText.isNotBlank() && !isLoading) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val backgroundColor = if (isUser) OrbitPrimary else OrbitSurface
    val textColor = if (isUser) Color.White else OrbitTextPrimary
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = backgroundColor,
            shape = shape,
            border = if (!isUser) BorderStroke(1.dp, OrbitBorder) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                style = Typography.bodyLarge,
                color = textColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = OrbitSurface,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 12.dp, bottomEnd = 18.dp),
            border = BorderStroke(1.dp, OrbitBorder),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BouncingDot(delay = 0)
                BouncingDot(delay = 150)
                BouncingDot(delay = 300)
            }
        }
    }
}

@Composable
fun BouncingDot(delay: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_$delay"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(OrbitTextMuted.copy(alpha = alpha))
    )
}
