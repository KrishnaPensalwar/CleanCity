package com.example.cleancityapp.presentation.driver.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.driver.dashboard.sections.DriverDashboardNextTaskSection
import com.example.cleancityapp.presentation.driver.dashboard.sections.DriverDashboardProgressCard
import com.example.cleancityapp.presentation.driver.dashboard.sections.DriverDashboardQuickActions
import com.example.cleancityapp.presentation.driver.dashboard.sections.DriverDashboardStatsSection
import com.example.cleancityapp.presentation.driver.dashboard.sections.DriverDutyStatusRow
import com.example.cleancityapp.presentation.main.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DriverDashboardScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToRoute: () -> Unit,
    driverViewModel: DriverViewModel = koinViewModel(),
) {
    val state by driverViewModel.state.collectAsState()
    var onDuty by remember { mutableStateOf(true) }

    val pendingReports = state.assignedReports.filter {
        it.status.lowercase() == "assigned" || it.status.lowercase() == "pending"
    }
    val completedReports = state.assignedReports.filter {
        it.status.lowercase() == "completed" || it.status.lowercase() == "resolved"
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DriverDutyStatusRow(isActive = onDuty, onActiveChanged = { onDuty = it })

            Spacer(modifier = Modifier.height(12.dp))

            DriverDashboardStatsSection(
                isLoading = state.isLoading,
                reportsEmpty = state.assignedReports.isEmpty(),
                totalTasks = state.assignedReports.size,
                completedCount = completedReports.size,
                pendingCount = pendingReports.size.toString(),
                error = state.error,
                onRetry = { driverViewModel.fetchAssignedReports() },
            )

            Spacer(modifier = Modifier.height(12.dp))

            DriverDashboardNextTaskSection(
                isLoadingSkeleton = state.isLoading && state.assignedReports.isEmpty(),
                nextTask = pendingReports.firstOrNull(),
                error = state.error,
                onNavigate = {
                    pendingReports.firstOrNull()?.let { report ->
                        driverViewModel.setSelectedReport(report)
                        val gmmIntentUri = Uri.parse("google.navigation:q=${report.latitude},${report.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${report.latitude},${report.longitude}"))
                            context.startActivity(browserIntent)
                        }
                    }
                    onNavigateToRoute()
                },
            )

            Spacer(modifier = Modifier.height(12.dp))

            DriverDashboardProgressCard(
                completed = completedReports.size,
                total = state.assignedReports.size,
            )

            Spacer(modifier = Modifier.height(12.dp))

            DriverDashboardQuickActions(
                onViewTasks = onNavigateToTasks,
                onRefresh = { driverViewModel.fetchAssignedReports() },
            )
        }
    }
}
