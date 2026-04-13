package com.example.cleancityapp.presentation.driver.profile

import androidx.compose.foundation.background
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
import com.example.cleancityapp.presentation.driver.route.DetailRow
import com.example.cleancityapp.presentation.driver.dashboard.StatCard

@Composable
fun DriverProfileScreen() {
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
                text = "My profile",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Driver account",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB5D4F4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "AK", color = Color(0xFF0C447C), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Arjun Kumar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Driver ID: DRV-2041", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Hyderabad Zone B · Active", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), "142", "Total tasks")
                StatCard(modifier = Modifier.weight(1f), "96%", "Completion")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), "4.8", "Rating")
                StatCard(modifier = Modifier.weight(1f), "28d", "Streak")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Vehicle info", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Reg. number", "KA-05-HB-4521")
                    DetailRow("Type", "Compactor truck")
                    DetailRow("Zone", "Hyderabad Zone B")
                    DetailRow("Shift", "6:00 AM – 2:00 PM")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    SettingRow("Notifications", "On")
                    SettingRow("Location sharing", "On")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sign out", fontSize = 13.sp, color = Color(0xFFA32D2D), fontWeight = FontWeight.Medium)
                        Text(text = "›", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp)
        Text(text = value, fontSize = 13.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
    }
}
