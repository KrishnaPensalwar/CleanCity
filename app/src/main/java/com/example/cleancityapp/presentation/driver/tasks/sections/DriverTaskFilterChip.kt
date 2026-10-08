package com.example.cleancityapp.presentation.driver.tasks.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.ui.theme.ChipUnselectedContainer
import com.example.cleancityapp.ui.theme.PrimaryBlue

@Composable
fun DriverTaskFilterChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        color = if (isSelected) PrimaryBlue else ChipUnselectedContainer,
        contentColor = if (isSelected) Color.White else Color.Gray,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable { onClick() },
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
