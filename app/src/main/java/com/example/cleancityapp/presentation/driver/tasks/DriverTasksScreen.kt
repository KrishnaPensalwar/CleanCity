package com.example.cleancityapp.presentation.driver.tasks

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.example.cleancityapp.presentation.components.HistoryItemSkeleton
import com.example.cleancityapp.presentation.components.SkeletonBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.presentation.driver.DriverViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriverTasksScreen(
    onNavigateToRoute: () -> Unit,
    viewModel: DriverViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val context = LocalContext.current
    var currentReportIdForPhoto by remember { mutableStateOf<String?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // ✅ Refresh tasks on every screen launch
    LaunchedEffect(Unit) {
        viewModel.fetchAssignedReports()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let { uri ->
                    currentReportIdForPhoto?.let { id ->
                        viewModel.uploadCompletionPhoto(id, uri)
                    }
                }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = createTempPictureUri(context)
                tempImageUri = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(state.isActionSuccess) {
        if (state.isActionSuccess) {
            Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetActionStatus()
        }
    }
    
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val filteredReports = remember(state.assignedReports, selectedFilter) {
        when (selectedFilter) {
            "Pending" -> state.assignedReports.filter { it.status.lowercase() == "assigned" || it.status.lowercase() == "pending" }
            "Done" -> state.assignedReports.filter { it.status.lowercase() == "completed" || it.status.lowercase() == "resolved" }
            else -> state.assignedReports
        }
    }

    val pendingCount = state.assignedReports.count { it.status.lowercase() == "assigned" || it.status.lowercase() == "pending" }
    val doneCount = state.assignedReports.count { it.status.lowercase() == "completed" || it.status.lowercase() == "resolved" }

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
                text = "${state.assignedReports.size} total · $doneCount done · $pendingCount pending",
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
                item { 
                    FilterChip(
                        label = "All (${state.assignedReports.size})", 
                        isSelected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" }
                    ) 
                }
                item { 
                    FilterChip(
                        label = "Pending ($pendingCount)", 
                        isSelected = selectedFilter == "Pending",
                        onClick = { selectedFilter = "Pending" }
                    ) 
                }
                item { 
                    FilterChip(
                        label = "Done ($doneCount)", 
                        isSelected = selectedFilter == "Done",
                        onClick = { selectedFilter = "Done" }
                    ) 
                }
            }

            if (state.isLoading && state.assignedReports.isEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(5) {
                        HistoryItemSkeleton()
                    }
                }
            } else if (filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No tasks found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredReports) { report ->
                        val isPending = report.status.lowercase() == "assigned" || report.status.lowercase() == "pending"
                        TaskCard(
                            id = "TASK #${report.id.takeLast(4).uppercase()}",
                            location = "Lat: ${report.latitude}, Lon: ${report.longitude}",
                            description = report.description,
                            date = formatDate(report.createdAt),
                            status = if (isPending) "Pending" else "Completed",
                            isHighlight = isPending,
                            onNavigate = if (isPending) {
                                {
                                    viewModel.setSelectedReport(report)
                                    onNavigateToRoute()
                                }
                            } else null,
                            onMarkDone = {
                                currentReportIdForPhoto = report.id
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    val uri = createTempPictureUri(context)
                                    tempImageUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun createTempPictureUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

private fun formatDate(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Surface(
        color = if (isSelected) Color(0xFF1565C0) else Color(0xFFEEEEEE),
        contentColor = if (isSelected) Color.White else Color.Gray,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { onClick() }
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
    date: String,
    distance: String? = null,
    status: String,
    isHighlight: Boolean = false,
    onNavigate: (() -> Unit)? = null,
    onMarkDone: (() -> Unit)? = null
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
            Text(text = "Reported on: $date", fontSize = 11.sp, color = Color.Gray)
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
                        onClick = { onMarkDone?.invoke() },
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
