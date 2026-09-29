package com.orbit.recovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.orbit.recovery.ui.theme.OrbitBorder
import com.orbit.recovery.ui.theme.OrbitPrimary
import com.orbit.recovery.ui.theme.OrbitTextPrimary
import com.orbit.recovery.ui.theme.Typography

@Composable
fun OrbitProgressBar(
    step: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSteps > 0) step.toFloat() / totalSteps else 0f
    val percentage = (progress * 100).toInt()
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step $step of $totalSteps",
                style = Typography.bodyMedium,
                color = OrbitTextPrimary
            )
            Text(
                text = "$percentage%",
                style = Typography.bodyMedium,
                color = OrbitTextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = OrbitPrimary,
            trackColor = OrbitBorder,
            strokeCap = StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..totalSteps) {
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (i <= step) OrbitPrimary else OrbitBorder)
                )
            }
        }
    }
}
