package com.example.cleancityapp.presentation.driver.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import com.example.cleancityapp.presentation.components.*
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.main.MainViewModel

@Composable
fun DriverDashboardScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToRoute: () -> Unit,
    driverViewModel: DriverViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel()
) {
    val state by driverViewModel.state.collectAsState()
    val mainState by mainViewModel.uiState.collectAsState()
    
    val pendingReports = state.assignedReports.filter { it.status.lowercase() == "assigned" || it.status.lowercase() == "pending" }
    val completedReports = state.assignedReports.filter { it.status.lowercase() == "completed" || it.status.lowercase() == "resolved" }
    
    val nextTask = pendingReports.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(16.dp)
        ) {
            Text(
                text = "Clean City · Driver App",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
            Text(
                text = "Good morning, ${mainState.currentUser?.name ?: "Arjun"}!",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "On duty · ${state.assignedReports.size} tasks assigned",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Status Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFFEAF3DE))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A6B3A))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Status: Active & available",
                    color = Color(0xFF27500A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = {})
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Grid
            if (state.isLoading && state.assignedReports.isEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                    SkeletonBox(modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(9.dp))
                }
            } else if (state.error != null) {
                ErrorState(
                    message = state.error ?: "Error loading dashboard",
                    onRetry = { driverViewModel.fetchAssignedReports() }
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(modifier = Modifier.weight(1f), state.assignedReports.size.toString(), "Tasks today")
                    StatCard(modifier = Modifier.weight(1f), completedReports.size.toString(), "Completed")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(modifier = Modifier.weight(1f), pendingReports.size.toString(), "Pending")
                    StatCard(modifier = Modifier.weight(1f), "38", "km driven")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Next Task Card
            if (state.isLoading && state.assignedReports.isEmpty()) {
                SkeletonBox(modifier = Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(11.dp))
            } else if (nextTask != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(11.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Next task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🗑️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Task #${nextTask.id.takeLast(4).uppercase()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Lat: ${nextTask.latitude}, Lon: ${nextTask.longitude}", fontSize = 11.sp, color = Color.Gray)
                                Text(text = nextTask.description, fontSize = 11.sp, color = Color(0xFF185FA5), maxLines = 1)
                            }
                            Badge(containerColor = Color(0xFFFAEEDA), contentColor = Color(0xFF854F0B)) {
                                Text("Pending")
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToRoute,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(9.dp)
                        ) {
                            Text("Navigate")
                        }
                    }
                }
            } else if (state.error == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(11.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No pending tasks", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Today's progress", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${completedReports.size} of ${state.assignedReports.size} tasks completed", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    val progress = if (state.assignedReports.isNotEmpty()) completedReports.size.toFloat() / state.assignedReports.size else 0f
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape),
                        color = Color(0xFF1565C0),
                        trackColor = Color(0xFFEEEEEE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Quick actions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickActionItem(modifier = Modifier.weight(1f), "📋", "View tasks", Color(0xFFE6F1FB), Color(0xFF185FA5), onClick = onNavigateToTasks)
                        QuickActionItem(modifier = Modifier.weight(1f), "📸", "Upload photo", Color(0xFFEAF3DE), Color(0xFF3B6D11))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickActionItem(modifier = Modifier.weight(1f), "🗺️", "Open map", Color(0xFFFAEEDA), Color(0xFF854F0B))
                        QuickActionItem(modifier = Modifier.weight(1f), "🚨", "Refresh", Color(0xFFFCEBEB), Color(0xFFA32D2D), onClick = { driverViewModel.fetchAssignedReports() })
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
        shape = RoundedCornerShape(9.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 18.sp)
            Text(text = label, fontSize = 11.sp, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}
