package com.example.cleancityapp.presentation.driver.tasks

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cleancityapp.presentation.components.HistoryItemSkeleton
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.driver.tasks.sections.DriverTaskCard
import com.example.cleancityapp.presentation.driver.tasks.sections.DriverTaskFilterChip
import com.example.cleancityapp.presentation.driver.tasks.sections.createTempPictureUri
import com.example.cleancityapp.presentation.driver.tasks.sections.formatDriverTaskDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun DriverTasksScreen(
    onNavigateToRoute: () -> Unit,
    viewModel: DriverViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val context = LocalContext.current
    var currentReportIdForPhoto by remember { mutableStateOf<String?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

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
        },
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
        },
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
            "Pending" -> state.assignedReports.filter {
                it.status.lowercase() == "assigned" || it.status.lowercase() == "pending"
            }
            "Done" -> state.assignedReports.filter {
                it.status.lowercase() == "completed" || it.status.lowercase() == "resolved"
            }
            else -> state.assignedReports
        }
    }

    val pendingCount = state.assignedReports.count {
        it.status.lowercase() == "assigned" || it.status.lowercase() == "pending"
    }
    val doneCount = state.assignedReports.count {
        it.status.lowercase() == "completed" || it.status.lowercase() == "resolved"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
        ) {
            Text(
                text = "Clean City · Driver",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
            )
            Text(text = "My tasks", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "${state.assignedReports.size} total · $doneCount done · $pendingCount pending",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp),
            ) {
                item {
                    DriverTaskFilterChip(
                        label = "All (${state.assignedReports.size})",
                        isSelected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                    )
                }
                item {
                    DriverTaskFilterChip(
                        label = "Pending ($pendingCount)",
                        isSelected = selectedFilter == "Pending",
                        onClick = { selectedFilter = "Pending" },
                    )
                }
                item {
                    DriverTaskFilterChip(
                        label = "Done ($doneCount)",
                        isSelected = selectedFilter == "Done",
                        onClick = { selectedFilter = "Done" },
                    )
                }
            }

            when {
                state.isLoading && state.assignedReports.isEmpty() ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        items(5) {
                            HistoryItemSkeleton()
                        }
                    }

                filteredReports.isEmpty() ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No tasks found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                else ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        items(filteredReports) { report ->
                            val isPending = report.status.lowercase() == "assigned" ||
                                report.status.lowercase() == "pending"
                            DriverTaskCard(
                                id = "TASK #${report.id.takeLast(4).uppercase()}",
                                location = "Lat: ${report.latitude}, Lon: ${report.longitude}",
                                description = report.description,
                                date = formatDriverTaskDate(report.createdAt),
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
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                                        PackageManager.PERMISSION_GRANTED
                                    ) {
                                        val uri = createTempPictureUri(context)
                                        tempImageUri = uri
                                        cameraLauncher.launch(uri)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                            )
                        }
                    }
            }
        }
    }
}
