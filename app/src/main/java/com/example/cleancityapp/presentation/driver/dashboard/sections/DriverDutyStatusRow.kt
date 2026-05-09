package com.example.cleancityapp.presentation.driver.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DriverDutyStatusRow(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    onActiveChanged: ((Boolean) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEAF3DE))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxAccent()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Status: ${if (isActive) "Active & available" else "Off duty"}",
            color = Color(0xFF27500A),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isActive,
            onCheckedChange = onActiveChanged ?: { },
        )
    }
}

@Composable
private fun BoxAccent() {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A6B3A)),
    )
}
