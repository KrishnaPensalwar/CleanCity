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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.ui.theme.Radius
import com.example.cleancityapp.ui.theme.Spacing
import com.example.cleancityapp.ui.theme.StatusErrorContainer
import com.example.cleancityapp.ui.theme.StatusErrorContent
import com.example.cleancityapp.ui.theme.StatusInfoContainer
import com.example.cleancityapp.ui.theme.StatusInfoContent
import com.example.cleancityapp.ui.theme.StatusSuccessContainer
import com.example.cleancityapp.ui.theme.StatusSuccessContent
import com.example.cleancityapp.ui.theme.StatusWarningContainer
import com.example.cleancityapp.ui.theme.StatusWarningContent

@Composable
fun DriverDashboardQuickActions(
    onViewTasks: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Radius.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(text = "Quick actions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                QuickActionItem(
                    modifier = Modifier.weight(1f),
                    icon = "📋",
                    label = "View tasks",
                    bgColor = StatusInfoContainer,
                    textColor = StatusInfoContent,
                    onClick = onViewTasks,
                )
                QuickActionItem(
                    modifier = Modifier.weight(1f),
                    icon = "📸",
                    label = "Upload photo",
                    bgColor = StatusSuccessContainer,
                    textColor = StatusSuccessContent,
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                QuickActionItem(modifier = Modifier.weight(1f), icon = "🗺️", label = "Open map", bgColor = StatusWarningContainer, textColor = StatusWarningContent)
                QuickActionItem(
                    modifier = Modifier.weight(1f),
                    icon = "🚨",
                    label = "Refresh",
                    bgColor = StatusErrorContainer,
                    textColor = StatusErrorContent,
                    onClick = onRefresh,
                )
            }
        }
    }
}
