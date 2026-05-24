package com.example.cleancityapp.presentation.report

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.components.CameraCapture
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.report.sections.ReportCategorySelector
import com.example.cleancityapp.presentation.user.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReportScreen(
    userViewModel: UserViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by userViewModel.state.collectAsState()
    val context = LocalContext.current

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var category by remember { mutableStateOf("Garbage") }
    var description by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isReportSuccess) {
        if (uiState.isReportSuccess) {
            Toast.makeText(context, "Report submitted. Thank you.", Toast.LENGTH_LONG).show()
            capturedImageUri = null
            description = ""
            userViewModel.resetReportStatus()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            CameraCapture(
                capturedImageUri = capturedImageUri,
                onImageCaptured = { uri -> capturedImageUri = uri },
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("🎁", fontSize = 24.sp)
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Impact reward", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text(
                            "Earn ~25 pts for validated reports",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Category", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            ReportCategorySelector(selected = category, onSelect = { category = it })

            if (category.equals("other", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(24.dp))
                TextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    placeholder = { Text("Enter custom category") },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Describe the issue", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("What needs attention?") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("Pinned location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Jubilee Hills, Hyderabad", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    capturedImageUri?.let { uri ->
                        userViewModel.submitReport(
                            imageUri = uri,
                            description = "[$category] $description",
                            lat = 17.4065,
                            lon = 78.4772
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = capturedImageUri != null && description.length > 5 && !uiState.isLoading,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Text("Submit report", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
