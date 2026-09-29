package com.orbit.recovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orbit.recovery.ui.theme.OrbitDivider
import com.orbit.recovery.ui.theme.OrbitPrimary
import com.orbit.recovery.ui.theme.OrbitSurface
import com.orbit.recovery.ui.theme.OrbitTextMuted
import com.orbit.recovery.ui.theme.Typography

enum class OrbitTab(val label: String) {
    Home("Home"),
    Garden("Garden"),
    Coach("Coach"),
    Learn("Learn"),
    ScreenTime("ScreenTime")
}

@Composable
fun OrbitBottomNav(
    currentTab: OrbitTab,
    onTabSelected: (OrbitTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .drawBehind {
                drawLine(
                    color = OrbitDivider,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
        containerColor = OrbitSurface,
        tonalElevation = 0.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrbitTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                val icon = when (tab) {
                    OrbitTab.Home -> Icons.Default.Home
                    OrbitTab.Garden -> Icons.Default.Eco
                    OrbitTab.Coach -> Icons.Default.ChatBubble
                    OrbitTab.Learn -> Icons.Default.MenuBook
                    OrbitTab.ScreenTime -> Icons.Default.Schedule
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onTabSelected(tab) }
                        .background(if (isSelected) OrbitPrimary else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) Color.White else OrbitTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        if (isSelected) {
                            Text(
                                text = tab.label,
                                style = Typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
