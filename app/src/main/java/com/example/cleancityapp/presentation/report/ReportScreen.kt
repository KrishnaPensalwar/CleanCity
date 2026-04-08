package com.example.cleancityapp.presentation.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

@Composable
fun ReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "File a report",
            subtitle = "Upload image & details"
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Upload Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0FAF4))
                    // Using basic dotted border simulation with simple border
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .padding(vertical = 24.dp, horizontal = 16.dp)
            ) {
                Text(text = "📸", fontSize = 32.sp, modifier = Modifier.padding(bottom = 6.dp))
                Row {
                    Text(text = "Tap to capture ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                    Text(text = "or upload from gallery", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Text(
                    text = "JPG, PNG up to 10MB",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Fallback for tertiary
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Issue Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Issue details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                FormField(label = "Category", value = "Garbage / Litter ▾")
                FormField(label = "Description", value = "Describe the issue briefly…")
                
                Text(text = "Location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp, top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(text = "Jubilee Hills, Hyderabad", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEAF3DE))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(text = "📍 GPS", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3B6D11))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            // Earn Points Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(text = "🎁", fontSize = 22.sp, modifier = Modifier.padding(end = 10.dp))
                Column {
                    Text(text = "Earn 25 points", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Upon admin approval of your report", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit report", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancel", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun FormField(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp)
        ) {
            Text(text = value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        DropdownMenu(false, onDismissRequest = {}){

        }
    }
}
