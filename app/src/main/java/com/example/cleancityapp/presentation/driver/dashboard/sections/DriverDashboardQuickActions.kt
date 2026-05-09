package com.example.cleancityapp.presentation.driver.dashboard.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DriverDashboardQuickActions(
    onViewTasks: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Quick actions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionItem(
                    modifier = Modifier.weight(1f),
                    icon = "📋",
                    label = "View tasks",
                    bgColor = Color(0xFFE6F1FB),
                    textColor = Color(0xFF185FA5),
                    onClick = onViewTasks,
                )
                QuickActionItem(
                    modifier = Modifier.weight(1f),
                    icon = "📸",
                    label = "Upload photo",
                    bgColor = Color(0xFFEAF3DE),
                    textColor = Color(0xFF3B6D11),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionItem(modifier = Modifier.weight(1f), icon = "🗺️", label = "Open map", bgColor = Color(0xFFFAEEDA), textColor = Color(0xFF854F0B))
                QuickActionItem(
                    modifier = Modifier.weight(1f),
                    icon = "🚨",
                    label = "Refresh",
                    bgColor = Color(0xFFFCEBEB),
                    textColor = Color(0xFFA32D2D),
                    onClick = onRefresh,
                )
            }
        }
    }
}
