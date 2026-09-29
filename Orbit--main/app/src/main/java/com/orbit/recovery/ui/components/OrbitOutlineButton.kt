package com.orbit.recovery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.recovery.ui.theme.OrbitBorder
import com.orbit.recovery.ui.theme.OrbitTextPrimary

@Composable
fun OrbitOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.5.dp, OrbitBorder)
    ) {
        Text(
            text = text,
            color = OrbitTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
