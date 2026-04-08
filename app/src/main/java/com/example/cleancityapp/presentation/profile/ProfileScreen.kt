package com.example.cleancityapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.presentation.home.StatCard

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "Profile",
            subtitle = "Your account & history"
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9FE1CB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "PK", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF085041))
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = "Priya Kumar", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "priya.kumar@gmail.com", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Hyderabad · Member since Jan 2025", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(num = "14", lbl = "Total reports", modifier = Modifier.weight(1f))
                StatCard(num = "9", lbl = "Resolved", modifier = Modifier.weight(1f))
                StatCard(num = "3", lbl = "Pending", modifier = Modifier.weight(1f))
                StatCard(num = "2", lbl = "Declined", modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Submission History
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Submission history",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                HistoryItem(title = "Jubilee Hills — Garbage pile", time = "Today, 9:14 AM · ", status = "Pending", dotColor = MaterialTheme.colorScheme.primary, hasLine = true)
                HistoryItem(title = "Banjara Hills — Open drain", time = "Yesterday · ", status = "Approved +25 pts", dotColor = Color(0xFF3B6D11), hasLine = true)
                HistoryItem(title = "Madhapur — Illegal dumping", time = "3 days ago · ", status = "Approved +25 pts", dotColor = Color(0xFF3B6D11), hasLine = false)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Settings
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                SettingRow(title = "Notifications", value = "On", valueColor = MaterialTheme.colorScheme.primary)
                SettingRow(title = "Privacy settings", value = "›", valueColor = MaterialTheme.colorScheme.onSurfaceVariant)
                SettingRow(title = "Sign out", value = "›", valueColor = MaterialTheme.colorScheme.onSurfaceVariant, titleColor = Color(0xFFA32D2D), isLast = true)
            }
        }
    }
}

@Composable
fun HistoryItem(title: String, time: String, status: String, dotColor: Color, hasLine: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Adjusted to fit the line
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(16.dp).padding(top = 4.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
            if (hasLine) {
                Box(modifier = Modifier.width(1.dp).height(28.dp).padding(vertical = 2.dp).background(MaterialTheme.colorScheme.outlineVariant))
            }
        }
        
        Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Row(modifier = Modifier.padding(top = 2.dp)) {
                Text(text = time, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = status, fontSize = 11.sp, color = if(status == "Pending") Color(0xFF854F0B) else Color(0xFF3B6D11))
            }
        }
    }
}

@Composable
fun SettingRow(title: String, value: String, valueColor: Color, titleColor: Color = MaterialTheme.colorScheme.onSurface, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = titleColor)
        Text(text = value, fontSize = 12.sp, fontWeight = if(value == "On") FontWeight.Medium else FontWeight.Normal, color = valueColor)
    }
    if (!isLast) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant))
    }
}
