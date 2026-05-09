package com.example.cleancityapp.presentation.driver.dashboard.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cleancityapp.presentation.components.ErrorState
import com.example.cleancityapp.presentation.components.SkeletonBox

@Composable
fun DriverDashboardStatsSection(
    isLoading: Boolean,
    reportsEmpty: Boolean,
    totalTasks: Int,
    completedCount: Int,
    pendingCount: String,
    error: String?,
    onRetry: () -> Unit,
) {
    Column {
        when {
            isLoading && reportsEmpty -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                }
            }
            error != null -> ErrorState(message = error, onRetry = onRetry)
            else -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(modifier = Modifier.weight(1f), value = totalTasks.toString(), label = "Tasks today")
                    StatCard(modifier = Modifier.weight(1f), value = completedCount.toString(), label = "Completed")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(modifier = Modifier.weight(1f), value = pendingCount, label = "Pending")
                    StatCard(modifier = Modifier.weight(1f), value = "38", label = "km driven")
                }
            }
        }
    }
}
