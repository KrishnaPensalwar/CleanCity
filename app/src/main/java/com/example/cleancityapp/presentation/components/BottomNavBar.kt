package com.example.cleancityapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.main.Screen
import com.example.cleancityapp.presentation.main.UserRole

@Composable
fun BottomNavBar(
    currentRoute: String?,
    userRole: UserRole,
    onNavigate: (Screen) -> Unit
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val barColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onBackground
    val selectedBg = if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val labelColor = if (isDark) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp + navBarPadding),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(32.dp)),
            color = barColor,
            shape = RoundedCornerShape(32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val screens = if (userRole == UserRole.USER) {
                    listOf(
                        Triple("🏠", "Home", Screen.Home),
                        Triple("📷", "Report", Screen.Report),
                        Triple("🏆", "Rewards", Screen.Rewards),
                        Triple("📜", "History", Screen.History)
                    )
                } else {
                    listOf(
                        Triple("🏠", "Home", Screen.DriverDashboard),
                        Triple("📋", "Tasks", Screen.DriverTasks),
                        Triple("🗺️", "Route", Screen.DriverRoute),
                        Triple("📜", "History", Screen.History)
                    )
                }

                screens.forEach { (icon, label, screen) ->
                    val isSelected = currentRoute == screen.route
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 52.dp else 46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) selectedBg
                                else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onNavigate(screen) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = icon,
                                fontSize = if (isSelected) 22.sp else 18.sp,
                                modifier = Modifier.graphicsLayer(
                                    scaleX = if (isSelected) 1.05f else 1f,
                                    scaleY = if (isSelected) 1.05f else 1f
                                )
                            )
                            if (!isSelected) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    color = labelColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
