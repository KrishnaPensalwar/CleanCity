package com.example.cleancityapp.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.presentation.history.sections.StatusBadge
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ComplaintDetailsScreen(
    complaintId: String,
    onBack: () -> Unit,
    viewModel: ComplaintDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(complaintId) {
        viewModel.processIntent(ComplaintDetailsContract.Intent.LoadComplaint(complaintId))
    }

    Scaffold(
        topBar = {
            TopNavBar(
                title = "Complaint Details",
                subtitle = "ID: $complaintId",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                state.complaint != null -> {
                    val complaint = state.complaint!!
                    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(Date(complaint.createdAt))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Images
                        if (complaint.imageUrls.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                AsyncImage(
                                    model = complaint.imageUrls.first(),
                                    contentDescription = "Complaint Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.Gray
                                    )
                                    StatusBadge(status = complaint.status)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                                ComplaintDetailInfoRow(label = "Category", value = complaint.category)
                                ComplaintDetailInfoRow(label = "Title", value = complaint.title)
                                ComplaintDetailInfoRow(label = "Description", value = complaint.description)
                                ComplaintDetailInfoRow(label = "Location", value = complaint.locationName)
                                ComplaintDetailInfoRow(label = "Submitted On", value = date)
                                
                                if (complaint.assignedWorkerName != null) {
                                    ComplaintDetailInfoRow(label = "Assigned Worker", value = complaint.assignedWorkerName)
                                }
                                if (complaint.adminRemarks != null) {
                                    ComplaintDetailInfoRow(label = "Admin Remarks", value = complaint.adminRemarks)
                                }
                                if (complaint.resolutionDetails != null) {
                                    ComplaintDetailInfoRow(label = "Resolution Details", value = complaint.resolutionDetails)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplaintDetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
