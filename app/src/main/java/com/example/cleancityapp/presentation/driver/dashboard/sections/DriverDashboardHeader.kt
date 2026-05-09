package com.example.cleancityapp.presentation.driver.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DriverDashboardHeader(
    greetingName: String,
    assignmentSummary: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
    ) {
        Text(
            text = "Clean City · Driver",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 12.sp,
        )
        Text(
            text = "Good morning, $greetingName!",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = assignmentSummary,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
        )
    }
}
