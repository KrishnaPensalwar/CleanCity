package com.example.cleancityapp.presentation.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.ThemeSelectorRow
import com.example.cleancityapp.presentation.home.sections.StatBubble
import com.example.cleancityapp.presentation.main.ThemeMode
import com.example.cleancityapp.presentation.profile.sections.ProfileSettingRow
import com.example.cleancityapp.ui.theme.DangerRed
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    currentThemeMode: ThemeMode,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
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
        isLoading = uiState.isLoading,
        error = uiState.error,
        user = uiState.user,
        onRetry = { viewModel.loadUser(force = true) },
        onLogout = onLogout
    ) { profile ->
        ProfileContent(
            profile = profile,
            currentThemeMode = currentThemeMode,
            notificationsOn = notifications.isOn,
            onNotificationsCheckedChange = notifications.onCheckedChange,
            onThemeSelected = {
                viewModel.setThemeMode(it)
                onThemeSelected(it)
            },
            onEditProfile = onEditProfile,
            onPrivacyPolicy = onPrivacyPolicy,
            onLogout = onLogout
        )
    }
}

@Composable
private fun ProfileContent(
    profile: UserDto,
    currentThemeMode: ThemeMode,
    notificationsOn: Boolean,
    onNotificationsCheckedChange: (Boolean) -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onEditProfile: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onLogout: () -> Unit
) {
    val initials = profile.name
        .split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")
        .uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .shadow(10.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(profile.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(profile.email, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        profile.address?.takeIf { it.isNotBlank() }?.let {
            Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBubble(
                label = "Reports",
                value = "${profile.reportsFiled}",
                modifier = Modifier.weight(1f)
            )
            StatBubble(
                label = "Points",
                value = "${profile.rewardPoints}",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "PERSONALIZATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
        ) {
            ThemeSelectorRow(
                currentMode = currentThemeMode,
                onModeSelected = onThemeSelected
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "ACCOUNT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
        ) {
            ProfileSettingRow(
                title = "Edit profile",
                icon = "✏️",
                onClick = onEditProfile
            )
            ProfileSettingRow(
                title = "Privacy policy",
                icon = "🛡️",
                onClick = onPrivacyPolicy
            )
            ProfileSettingRow(
                title = "Notifications",
                icon = "🔔",
                trailing = {
                    Switch(
                        checked = notificationsOn,
                        onCheckedChange = onNotificationsCheckedChange
                    )
                }
            )
            ProfileSettingRow(
                title = "Log out",
                icon = "🚪",
                titleColor = DangerRed,
                isLast = true,
                onClick = onLogout,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
