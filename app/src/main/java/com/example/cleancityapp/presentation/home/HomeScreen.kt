package com.example.cleancityapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.home.sections.ActivityItem
import com.example.cleancityapp.presentation.home.sections.HeaderSection
import com.example.cleancityapp.presentation.home.sections.MapCard
import com.example.cleancityapp.presentation.home.sections.StatBubble
import com.example.cleancityapp.presentation.main.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    user: UserDto?,
    onNavigateToReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val displayName = user?.name ?: state.currentUser?.name ?: "Citizen"

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToReport,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text("Report issue", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            HeaderSection(displayName, user?.rewardPoints ?: state.currentUser?.rewardPoints ?: 0, onNavigateToProfile)

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Your impact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatBubble(
                        label = "Rank",
                        value = state.userRank?.currentUser?.rank?.toString() ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                    StatBubble(
                        label = "Resolved",
                        value = (state.currentUser?.reportsResolved ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Nearby activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Map tab",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(enabled = false) { },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                MapCard(activeReports = 4)

                Spacer(modifier = Modifier.height(24.dp))

                Text("Recent activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                state.userReports.take(3).forEach { report ->
                    ActivityItem(report.description, "Recently updated", report.status)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.userReports.isEmpty()) {
                    ActivityItem("No reports yet", "Tap + to file your first report", "Clean")
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
