package com.example.cleancityapp.presentation.map

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
import com.example.cleancityapp.presentation.home.ReportItem
import com.example.cleancityapp.ui.theme.*

@Composable
fun MapScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "Heatmap",
            subtitle = "Hyderabad · live reports"
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFC8E6C9))
            ) {
                // High density area
                Box(modifier = Modifier.offset(x = 80.dp, y = 70.dp).size(40.dp).clip(CircleShape).background(Color(0x59E24B4A)))
                Box(modifier = Modifier.offset(x = 90.dp, y = 80.dp).size(14.dp).clip(CircleShape).background(Color(0xFFE24B4A)).border(2.dp, Color.White, CircleShape))
                
                // Medium density
                Box(modifier = Modifier.offset(x = 220.dp, y = 110.dp).size(30.dp).clip(CircleShape).background(Color(0x66EF9F27)))
                Box(modifier = Modifier.offset(x = 230.dp, y = 120.dp).size(10.dp).clip(CircleShape).background(Color(0xFFEF9F27)).border(2.dp, Color.White, CircleShape))
                
                // Low density
                Box(modifier = Modifier.offset(x = 220.dp, y = 50.dp).size(20.dp).clip(CircleShape).background(Color(0x33E24B4A)))
                Box(modifier = Modifier.offset(x = 225.dp, y = 55.dp).size(10.dp).clip(CircleShape).background(Color(0xFFE24B4A)).border(2.dp, Color.White, CircleShape))
                
                Box(modifier = Modifier.offset(x = 50.dp, y = 130.dp).size(10.dp).clip(CircleShape).background(Color(0xFFEF9F27)).border(2.dp, Color.White, CircleShape))
                
                Text(
                    text = "OSM · Hyderabad",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D5A2D),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color(0xD9FFFFFF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                LegendItem(color = Color(0xFFE24B4A), label = "High density")
                LegendItem(color = Color(0xFFEF9F27), label = "Medium")
                LegendItem(color = Color(0xFF639922), label = "Low")
            }
            
            // Nearby reports card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Nearby reports",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                ReportItem(icon = "🗑️", bg = Color(0xFFC0DD97), loc = "Jubilee Hills, Rd 36", time = "0.3 km away · 2 hrs ago", status = "Pending")
                ReportItem(icon = "🚯", bg = Color(0xFFFAC775), loc = "Greenlands colony", time = "0.8 km away · 5 hrs ago", status = "Resolved")
                ReportItem(icon = "♻️", bg = Color(0xFFF5C4B3), loc = "Film Nagar flyover", time = "1.2 km away · 1 day ago", status = "Pending", isLast = true)
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
