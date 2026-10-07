package com.example.cleancityapp.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.components.InputField
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current
    val user = uiState.user

    var name by remember(user?.id) { mutableStateOf(user?.name.orEmpty()) }
    var phone by remember(user?.id) { mutableStateOf(user?.phone.orEmpty()) }
    var address by remember(user?.id) { mutableStateOf(user?.address.orEmpty()) }

    LaunchedEffect(Unit) {
        viewModel.loadUser(force = true)
    }

    LaunchedEffect(uiState.user) {
        uiState.user?.let {
            name = it.name
            phone = it.phone.orEmpty()
            address = it.address.orEmpty()
        }
    }

    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateStatus()
            onSaved()
            onBack()
        }
    }

    ProfileLoadGate(
        isLoading = uiState.isLoading,
        error = uiState.error,
        user = uiState.user,
        onRetry = { viewModel.loadUser(force = true) },
        onLogout = onLogout
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Edit profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Update your personal details",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
            }

            InputField(
                value = name,
                onValueChange = { name = it },
                label = "Full name",
                enabled = !uiState.isSaving
            )
            Spacer(Modifier.height(16.dp))
            InputField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone",
                enabled = !uiState.isSaving
            )
            Spacer(Modifier.height(16.dp))
            InputField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                enabled = !uiState.isSaving,
                singleLine = false
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.updateProfile(name, phone, address) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isSaving && name.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
