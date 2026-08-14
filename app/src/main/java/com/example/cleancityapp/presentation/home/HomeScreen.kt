package com.example.cleancityapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.home.sections.ActivityItem
import com.example.cleancityapp.presentation.home.sections.StatBubble
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    user: UserDto?,
    onNavigateToReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.refreshHome(force = true)
        }
    }

    val displayUser = state.currentUser ?: user

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Your impact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatBubble(
                        label = "Rank",
                        value = "${state.userRank?.currentUser?.rank ?: "—"}",
                        modifier = Modifier.weight(1f),
                    )
                    StatBubble(
                        label = "Reports",
                        value = "${displayUser?.reportsFiled ?: state.userReports.size}",
                        modifier = Modifier.weight(1f),
                    )
                    StatBubble(
                        label = "Resolved",
                        value = "${displayUser?.reportsResolved ?: 0}",
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Recent activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                state.userReports.take(5).forEach { report ->
                    ActivityItem(report.description, "Recently updated", report.status)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.userReports.isEmpty() && !state.isLoading) {
                    ActivityItem("No reports yet", "Tap + to file your first report", "Clean")
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (state.isLoading && state.userReports.isEmpty() && state.currentUser == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
