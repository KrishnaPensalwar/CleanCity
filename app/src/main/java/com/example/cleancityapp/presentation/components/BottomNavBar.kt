package com.example.cleancityapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.main.Screen
import com.example.cleancityapp.presentation.main.UserRole

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    userRole: UserRole,
    onNavigate: (Screen) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userRole == UserRole.USER) {
            NavItem(
                icon = "🏠",
                label = "Home",
                isSelected = currentScreen == Screen.Home,
                onClick = { onNavigate(Screen.Home) }
            )
            NavItem(
                icon = "📷",
                label = "Report",
                isSelected = currentScreen == Screen.Report,
                onClick = { onNavigate(Screen.Report) }
            )
            NavItem(
                icon = "🗺️",
                label = "Map",
                isSelected = currentScreen == Screen.Map,
                onClick = { onNavigate(Screen.Map) }
            )
            NavItem(
                icon = "🏆",
                label = "Rewards",
                isSelected = currentScreen == Screen.Rewards,
                onClick = { onNavigate(Screen.Rewards) }
            )
            NavItem(
                icon = "📜",
                label = "History",
                isSelected = currentScreen == Screen.History,
                onClick = { onNavigate(Screen.History) }
            )
        } else {
            NavItem(
                icon = "🏠",
                label = "Home",
                isSelected = currentScreen == Screen.DriverDashboard,
                onClick = { onNavigate(Screen.DriverDashboard) }
            )
            NavItem(
                icon = "📋",
                label = "Tasks",
                isSelected = currentScreen == Screen.DriverTasks,
                onClick = { onNavigate(Screen.DriverTasks) }
            )
            NavItem(
                icon = "🗺️",
                label = "Route",
                isSelected = currentScreen == Screen.DriverRoute,
                onClick = { onNavigate(Screen.DriverRoute) }
            )
            NavItem(
                icon = "📜",
                label = "History",
                isSelected = currentScreen == Screen.History,
                onClick = { onNavigate(Screen.History) }
            )
        }
    }
}

@Composable
fun NavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = fontWeight,
            color = contentColor,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}
