package com.example.cleancityapp.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.presentation.components.ErrorState
import com.example.cleancityapp.presentation.components.HistoryItemSkeleton
import com.example.cleancityapp.presentation.history.sections.HistoryEmptyState
import com.example.cleancityapp.presentation.history.sections.HistoryFilterBar
import com.example.cleancityapp.presentation.history.sections.ReportHistoryCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    isDriver: Boolean,
    onReportClick: (ReportResponse) -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Approved", "Rejected")
    val lifecycleOwner = LocalLifecycleOwner.current

    val filteredReports = remember(uiState.reports, selectedFilter) {
        if (selectedFilter == "All") uiState.reports
        else uiState.reports.filter { normalizeReportStatus(it.status) == selectedFilter }
    }

    LaunchedEffect(isDriver, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.fetchReports(isDriver, force = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        HistoryFilterBar(
            filters = filters,
            selectedFilter = selectedFilter,
            onSelect = { selectedFilter = it },
        )

        when {
            uiState.isLoading && uiState.reports.isEmpty() ->
                Column(modifier = Modifier.padding(24.dp)) {
                    repeat(4) { HistoryItemSkeleton() }
                }

            uiState.error != null ->
                ErrorState(message = uiState.error!!, onRetry = { viewModel.fetchReports(isDriver, force = true) })

            filteredReports.isEmpty() ->
                HistoryEmptyState()

            else ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(filteredReports, key = { it.id }) { report ->
                        ReportHistoryCard(
                            report = report,
                            onClick = { onReportClick(report) },
                        )
                    }
                }
        }
    }
}
