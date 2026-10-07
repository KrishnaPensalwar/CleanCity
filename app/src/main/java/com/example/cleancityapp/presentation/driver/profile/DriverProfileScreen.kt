package com.example.cleancityapp.presentation.driver.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.ThemeSelectorRow
import com.example.cleancityapp.presentation.driver.dashboard.sections.StatCard
import com.example.cleancityapp.presentation.main.ThemeMode
import com.example.cleancityapp.presentation.profile.ProfileLoadGate
import com.example.cleancityapp.presentation.profile.ProfileViewModel
import com.example.cleancityapp.presentation.profile.rememberNotificationPermissionUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun DriverProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    currentThemeMode: ThemeMode,
    notificationsEnabled: Boolean = true,
    onNotificationsToggle: (Boolean) -> Unit = {},
    onPrivacyPolicy: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val notifications = rememberNotificationPermissionUi(
        notificationsEnabled = notificationsEnabled,
        onNotificationsToggle = onNotificationsToggle
    )

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loadUser(force = true)
        }
    }

    ProfileLoadGate(
        isLoading = state.isLoading,
        error = state.error,
        user = state.user,
        onRetry = { viewModel.loadUser(force = true) },
        onLogout = onLogout
    ) { profile ->
        DriverProfileContent(
            profile = profile,
            currentThemeMode = currentThemeMode,
            notificationsOn = notifications.isOn,
            onNotificationsCheckedChange = notifications.onCheckedChange,
            onThemeSelected = {
                viewModel.setThemeMode(it)
                onThemeSelected(it)
            },
            onPrivacyPolicy = onPrivacyPolicy,
            onLogout = onLogout
        )
    }
}

@Composable
private fun DriverProfileContent(
    profile: UserDto,
    currentThemeMode: ThemeMode,
    notificationsOn: Boolean,
    onNotificationsCheckedChange: (Boolean) -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onPrivacyPolicy: () -> Unit,
    onLogout: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val initials = profile.name
        .split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")
        .uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(profile.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Text(profile.email, fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
                        profile.phone?.takeIf { it.isNotBlank() }?.let {
                            Text(it, fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
                        }
                        profile.address?.takeIf { it.isNotBlank() }?.let {
                            Text(it, fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), "${profile.reportsFiled}", "Reports")
                StatCard(modifier = Modifier.weight(1f), "${profile.rewardPoints}", "Points")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                ThemeSelectorRow(
                    currentMode = currentThemeMode,
                    onModeSelected = onThemeSelected
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Notifications", fontSize = 13.sp, color = colorScheme.onSurface)
                        Switch(
                            checked = notificationsOn,
                            onCheckedChange = onNotificationsCheckedChange
                        )
                    }

                    SettingRow(label = "Privacy policy", value = "View", onClick = onPrivacyPolicy)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLogout() }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sign out",
                            fontSize = 13.sp,
                            color = colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = "›", color = colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = colorScheme.onSurface)
        Text(text = value, fontSize = 13.sp, color = colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}
