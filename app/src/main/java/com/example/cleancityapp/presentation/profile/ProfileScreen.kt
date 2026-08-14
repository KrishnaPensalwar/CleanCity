package com.example.cleancityapp.presentation.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.ThemeSelectorRow
import com.example.cleancityapp.presentation.home.sections.StatBubble
import com.example.cleancityapp.presentation.main.ThemeMode
import com.example.cleancityapp.presentation.profile.sections.ProfileSettingRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    user: UserDto?,
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var osPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        osPermissionGranted = isGranted
        onNotificationsToggle(isGranted)
        if (!isGranted) {
            // Open system settings if permanently denied / user wants to enable
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loadUser(force = true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                osPermissionGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    val profile = uiState.user ?: user
    val name = profile?.name ?: "User"
    val email = profile?.email ?: "user@example.com"
    val notificationStatus = if (notificationsEnabled && osPermissionGranted) "On" else "Off"
    val initials = try {
        name.split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
            .uppercase()
            .ifEmpty { "U" }
    } catch (_: Exception) {
        "U"
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            Text(name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(email, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            profile?.address?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBubble(
                    label = "Reports",
                    value = "${profile?.reportsFiled ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                StatBubble(
                    label = "Points",
                    value = "${profile?.rewardPoints ?: 0}",
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
                    onModeSelected = {
                        viewModel.setThemeMode(it)
                        onThemeSelected(it)
                    }
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
                    value = notificationStatus,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!osPermissionGranted) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onNotificationsToggle(!notificationsEnabled)
                            }
                        } else {
                            onNotificationsToggle(!notificationsEnabled)
                        }
                    }
                )
                ProfileSettingRow(
                    title = "Log out",
                    icon = "🚪",
                    titleColor = Color(0xFFD32F2F),
                    isLast = true,
                    onClick = onLogout,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        if (uiState.isLoading && uiState.user == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center).size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
