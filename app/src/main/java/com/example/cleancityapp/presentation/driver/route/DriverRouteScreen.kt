package com.example.cleancityapp.presentation.driver.route

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.components.CameraCapture
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.driver.tasks.sections.formatDriverTaskDate
import com.example.cleancityapp.ui.theme.BadgeDeclinedBg
import com.example.cleancityapp.ui.theme.BadgeDeclinedText
import org.koin.androidx.compose.koinViewModel

@Composable
fun DriverRouteScreen(
    viewModel: DriverViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val report = state.selectedReport
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(state.isActionSuccess) {
        if (state.isActionSuccess) {
            Toast.makeText(context, "Task marked as completed", Toast.LENGTH_SHORT).show()
            viewModel.resetActionStatus()
            capturedImageUri = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (report == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No task selected", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Map Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺️", fontSize = 40.sp)
                        Text("Task Location: ${report.latitude}, ${report.longitude}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        Text("OSM · Live", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Task Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(11.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Task details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailRow("Location", "Lat: ${report.latitude}, Lon: ${report.longitude}")
                        DetailRow("Issue type", report.description.substringBefore("]").removePrefix("[").ifEmpty { "General" })
                        DetailRow("Description", report.description.substringAfter("]").trim().ifEmpty { report.description })
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Status", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Badge(
                                containerColor = if (report.status == "Completed") Color(0xFFEAF3DE) else Color(0xFFFAEEDA),
                                contentColor = if (report.status == "Completed") Color(0xFF3B6D11) else Color(0xFF854F0B)
                            ) {
                                Text(report.status.uppercase())
                            }
                        }
                        DetailRow("Reported", formatDriverTaskDate(report.createdAt))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (report.status.lowercase() != "completed" && report.status.lowercase() != "resolved") {
                    Text(text = "After clean-up", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    CameraCapture(
                        onImageCaptured = { uri -> capturedImageUri = uri },
                        buttonText = "📸 Capture Completion Photo"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { 
                            capturedImageUri?.let { uri ->
                                viewModel.uploadCompletionPhoto(report.id, uri)
                            }
                        },
                        enabled = capturedImageUri != null && !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Mark as completed")
                        }
                    }
                } else {
                    Text(text = "Task Completed", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B6D11))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
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
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 16.dp))
    }
}
