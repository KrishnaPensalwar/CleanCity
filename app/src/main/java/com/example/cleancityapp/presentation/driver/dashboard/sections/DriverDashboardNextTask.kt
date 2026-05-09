package com.example.cleancityapp.presentation.driver.dashboard.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.presentation.components.SkeletonBox

@Composable
fun DriverDashboardNextTaskSection(
    isLoadingSkeleton: Boolean,
    nextTask: ReportResponse?,
    error: String?,
    onNavigate: () -> Unit,
) {
    when {
        isLoadingSkeleton ->
            SkeletonBox(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(11.dp),
            )

        nextTask != null ->
            NextTaskFilledCard(report = nextTask, onNavigate = onNavigate)

        error == null ->
            EmptyTaskCard()
    }
}

@Composable
private fun NextTaskFilledCard(report: ReportResponse, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Next task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🗑️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Task #${report.id.takeLast(4).uppercase()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Lat: ${report.latitude}, Lon: ${report.longitude}", fontSize = 11.sp, color = Color.Gray)
                    Text(text = report.description, fontSize = 11.sp, color = Color(0xFF185FA5), maxLines = 1)
                }
                Badge(containerColor = Color(0xFFFAEEDA), contentColor = Color(0xFF854F0B)) {
                    Text("Pending")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(9.dp),
            ) {
                Text("Navigate")
            }
        }
    }
}

@Composable
private fun EmptyTaskCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = "No pending tasks", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}
