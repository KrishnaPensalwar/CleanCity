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
import androidx.compose.ui.draw.shadow
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
    val name = user?.name ?: "User"
    val email = user?.email ?: ""
    val initials = if (name.isNotBlank()) {
        name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").uppercase()
    } else "U"
    
    val rewardPoints = user?.rewardPoints ?: 0
    val reportsFiled = user?.reportsFiled ?: 0
    val reportsResolved = user?.reportsResolved ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "Profile",
            subtitle = "Your account details",
            onBackClick = onBack
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(modifier = Modifier.padding(start = 20.dp)) {
                        Text(
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = email,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Hyderabad · Member since 2025",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(num = reportsFiled.toString(), lbl = "Reports", modifier = Modifier.weight(1f))
                StatCard(num = rewardPoints.toString(), lbl = "Reward Pts", modifier = Modifier.weight(1f))
                StatCard(num = reportsResolved.toString(), lbl = "Resolved", modifier = Modifier.weight(1f))
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
