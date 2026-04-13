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

@Composable
fun DriverDashboardScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToRoute: () -> Unit
) {
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
                text = "Good morning, Arjun!",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Vehicle KA-05-HB-4521 · On duty",
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), "6", "Tasks today")
                StatCard(modifier = Modifier.weight(1f), "4", "Completed")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), "2", "Pending")
                StatCard(modifier = Modifier.weight(1f), "38", "km driven")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Next Task Card
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
                            Text(text = "Jubilee Hills, Rd 36", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Garbage pile · 0.8 km away", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "Reported 2 hrs ago · HIGH priority", fontSize = 11.sp, color = Color(0xFF185FA5))
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
                    Text(text = "4 of 6 tasks completed", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = 0.67f,
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
                        QuickActionItem(modifier = Modifier.weight(1f), "🚨", "Report issue", Color(0xFFFCEBEB), Color(0xFFA32D2D))
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
