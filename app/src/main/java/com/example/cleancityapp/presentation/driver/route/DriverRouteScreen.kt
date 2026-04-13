package com.example.cleancityapp.presentation.driver.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun DriverRouteScreen() {
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
                text = "Navigation",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Task #T-042 · Jubilee Hills",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFDCE8F5)),
                contentAlignment = Alignment.Center
            ) {
                // Simplified Map UI representation
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗺️", fontSize = 40.sp)
                    Text("Interactive Map View", fontSize = 12.sp, color = Color(0xFF0C447C), fontWeight = FontWeight.Bold)
                    Text("OSM · Live", fontSize = 10.sp, color = Color(0xFF0C447C))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Distance Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🗺️", fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "0.8 km · 12 min", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Via Jubilee Hills Rd, turns right at signal", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Task details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Location", "Jubilee Hills, Rd 36")
                    DetailRow("Issue type", "Garbage pile")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Priority", fontSize = 12.sp, color = Color.Gray)
                        Badge(containerColor = Color(0xFFFCEBEB), contentColor = Color(0xFFA32D2D)) {
                            Text("HIGH")
                        }
                    }
                    DetailRow("Reported by", "Priya Kumar")
                    DetailRow("Reported", "2 hrs ago")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "After clean-up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A6B3A)),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text("📸 Upload completion photo")
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA32D2D)),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text("Mark as completed")
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
