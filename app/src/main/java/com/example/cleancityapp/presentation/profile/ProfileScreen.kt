package com.example.cleancityapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.home.sections.StatBubble
import com.example.cleancityapp.presentation.main.MainContract
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.main.ThemeMode
import com.example.cleancityapp.presentation.components.ThemeSelectorRow
import com.example.cleancityapp.presentation.profile.sections.ProfileSettingRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    user: UserDto?,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    currentThemeMode: ThemeMode,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle result if needed
    }

    val notificationStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) "On" else "Off"
    } else {
        "On"
    }

    val name = "${uiState.user?.name ?: user?.name ?: "User"}"
    val email = "${uiState.user?.email ?: user?.email ?: "user@example.com"}"
    val initials = try {
        name.split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
            .uppercase()
            .ifEmpty { "U" }
    } catch (e: Exception) {
        "U"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
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
        Text(name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(email, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBubble(label = "Reports", value = "${user?.reportsFiled ?: 0}", modifier = Modifier.weight(1f))
            StatBubble(
                label = "Points",
                value = "${user?.rewardPoints ?: 0}",
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
                onModeSelected = { onThemeSelected(it) }
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
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
        ) {
            ProfileSettingRow(title = "Privacy policy", icon = "🛡️")
            ProfileSettingRow(
                title = "Notifications",
                icon = "🔔",
                value = notificationStatus,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
    }
}
