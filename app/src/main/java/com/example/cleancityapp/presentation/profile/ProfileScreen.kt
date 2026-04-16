package com.example.cleancityapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.presentation.home.StatCard

@Composable
fun ProfileScreen(
    user: UserDto?,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val name = user?.name ?: "Priya Kumar"
    val email = user?.email ?: "priya.kumar@gmail.com"
    val initials = if (name.isNotBlank()) {
        name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").uppercase()
    } else "PK"
    
    val rewardPoints = user?.rewardPoints ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 10.dp, start = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            TopNavBar(
                title = "Profile",
                subtitle = "Your account details"
            )

        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9FE1CB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initials, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF085041))
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = email, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Hyderabad · Member since 2025", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(num = "14", lbl = "Total reports", modifier = Modifier.weight(1f))
                StatCard(num = rewardPoints.toString(), lbl = "Reward Pts", modifier = Modifier.weight(1f))
                StatCard(num = "3", lbl = "Pending", modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Settings
            Text(
                text = "Account Settings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp)
            ) {
                SettingRow(title = "Notifications", value = "On", valueColor = MaterialTheme.colorScheme.primary)
                SettingRow(title = "Privacy settings", value = "›", valueColor = MaterialTheme.colorScheme.onSurfaceVariant)
                SettingRow(
                    title = "Sign out",
                    value = "›",
                    valueColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    titleColor = Color(0xFFA32D2D),
                    isLast = true,
                    onClick = onLogout
                )
            }
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    value: String,
    valueColor: Color,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    isLast: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp, color = titleColor, fontWeight = if(title == "Sign out") FontWeight.Medium else FontWeight.Normal)
        Text(text = value, fontSize = 14.sp, fontWeight = if(value == "On") FontWeight.Medium else FontWeight.Normal, color = valueColor)
    }
    if (!isLast) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant))
    }
}
