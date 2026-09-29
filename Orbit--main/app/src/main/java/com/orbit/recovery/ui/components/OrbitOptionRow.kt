package com.orbit.recovery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.orbit.recovery.ui.theme.OrbitBorder
import com.orbit.recovery.ui.theme.OrbitSurface
import com.orbit.recovery.ui.theme.OrbitTextPrimary
import com.orbit.recovery.ui.theme.Typography

@Composable
fun OrbitOptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        color = OrbitSurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OrbitBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = Typography.bodyLarge,
                color = OrbitTextPrimary
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = OrbitTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(1.5.dp, OrbitBorder, CircleShape)
                )
            }
        }
    }
}
