package com.example.cleancityapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.home.sections.StatBubble
import com.example.cleancityapp.presentation.profile.sections.ProfileSettingRow

@Composable
fun ProfileScreen(
    user: UserDto?,
    onLogout: () -> Unit,
    onBack: () -> Unit,
) {
    val name = user?.name ?: "User"
    val email = user?.email ?: "user@example.com"
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").uppercase()

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
            ProfileSettingRow(title = "Notifications", icon = "🔔", value = "On")
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
