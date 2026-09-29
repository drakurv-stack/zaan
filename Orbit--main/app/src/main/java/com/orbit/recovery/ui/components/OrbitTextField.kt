package com.orbit.recovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.orbit.recovery.ui.theme.OrbitSurfaceVariant
import com.orbit.recovery.ui.theme.OrbitTextMuted
import com.orbit.recovery.ui.theme.OrbitTextPrimary
import com.orbit.recovery.ui.theme.Typography

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment

@Composable
fun OrbitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OrbitSurfaceVariant)
            .padding(14.dp),
        textStyle = Typography.bodyLarge.copy(color = OrbitTextPrimary),
        cursorBrush = SolidColor(OrbitTextPrimary),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = Typography.bodyLarge,
                            color = OrbitTextMuted
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
