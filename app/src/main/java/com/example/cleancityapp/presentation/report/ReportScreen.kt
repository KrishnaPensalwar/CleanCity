package com.example.cleancityapp.presentation.report

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.presentation.components.CameraCapture
import com.example.cleancityapp.presentation.main.MainContract
import com.example.cleancityapp.presentation.main.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReportScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var category by remember { mutableStateOf("Garbage / Litter") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Jubilee Hills, Hyderabad") }

    val categories = listOf("Garbage / Litter", "Open Drain", "Dead Animal", "Illegal Dumping", "Overflowing Bin")

    LaunchedEffect(uiState.isReportSuccess) {
        if (uiState.isReportSuccess) {
            Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_LONG).show()
            capturedImageUri = null
            description = ""
            viewModel.processIntent(MainContract.Intent.ResetReportStatus)
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel.processIntent(MainContract.Intent.ClearError)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "File a report",
            subtitle = "Capture image & details"
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Camera Capture Section
            CameraCapture(
                onImageCaptured = { uri -> capturedImageUri = uri },
                buttonText = "📸 Capture Waste Photo"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                
                CategoryDropdown(
                    label = "Category",
                    selectedCategory = category,
                    categories = categories,
                    onCategorySelected = { category = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Description", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe the issue briefly…", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(text = location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEAF3DE))
                            .clickable { /* Update location logic */ }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(text = "📍 GPS", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3B6D11))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    capturedImageUri?.let { uri ->
                        viewModel.processIntent(
                            MainContract.Intent.SubmitReport(
                                imageUri = uri,
                                description = "$category: $description",
                                latitude = 37.7749, // Hardcoded for sample
                                longitude = -122.4194 // Hardcoded for sample
                            )
                        )
                    }
                },
                enabled = capturedImageUri != null && description.isNotBlank() && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Submit report", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = {
                    capturedImageUri = null
                    description = ""
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(text = "Cancel", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    label: String,
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedCategory, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
