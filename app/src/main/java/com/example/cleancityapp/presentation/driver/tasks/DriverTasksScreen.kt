package com.example.cleancityapp.presentation.driver.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun DriverTasksScreen(
    onNavigateToRoute: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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
                text = "My tasks",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "6 assigned · 4 done · 2 pending",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                item { FilterChip("All (6)", true) }
                item { FilterChip("Pending (2)", false) }
                item { FilterChip("Done (4)", false) }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    TaskCard(
                        id = "TASK #T-042 · HIGH",
                        location = "Jubilee Hills, Rd 36",
                        description = "Garbage pile · Reported by Priya K. · 2 hrs ago",
                        distance = "📍 0.8 km away · Est. 12 min",
                        status = "Pending",
                        isHighlight = true,
                        onNavigate = onNavigateToRoute
                    )
                }
                item {
                    TaskCard(
                        id = "TASK #T-041 · MED",
                        location = "Greenlands Colony",
                        description = "Open drain debris · 5 hrs ago",
                        distance = "📍 2.1 km away · Est. 28 min",
                        status = "Pending"
                    )
                }
                item {
                    TaskCard(
                        id = "TASK #T-040",
                        location = "Banjara Hills, Rd 12",
                        description = "Garbage cleared · 10:45 AM · Photo uploaded",
                        status = "Completed"
                    )
                }
                item {
                    TaskCard(
                        id = "TASK #T-039",
                        location = "Madhapur Main Rd",
                        description = "Litter cleared · 9:20 AM · Photo uploaded",
                        status = "Completed"
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF1565C0) else Color(0xFFEEEEEE),
        contentColor = if (isSelected) Color.White else Color.Gray,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TaskCard(
    id: String,
    location: String,
    description: String,
    distance: String? = null,
    status: String,
    isHighlight: Boolean = false,
    onNavigate: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isHighlight) Modifier.border(1.5.dp, Color(0xFF1565C0), RoundedCornerShape(11.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(11.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = id,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlight) Color(0xFF185FA5) else Color.Gray
                )
                Badge(
                    containerColor = if (status == "Completed") Color(0xFFEAF3DE) else Color(0xFFFAEEDA),
                    contentColor = if (status == "Completed") Color(0xFF3B6D11) else Color(0xFF854F0B)
                ) {
                    Text(status)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = location, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = description, fontSize = 11.sp, color = Color.Gray)
            if (distance != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = distance, fontSize = 11.sp, color = Color.Gray)
            }
            
            if (onNavigate != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onNavigate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(9.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Navigate", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFF3B6D11)),
                        shape = RoundedCornerShape(9.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3B6D11))
                    ) {
                        Text("Mark done", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
