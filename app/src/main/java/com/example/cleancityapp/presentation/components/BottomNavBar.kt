package com.example.cleancityapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(36.dp)),
            color = Color(0xFF1A1A1A), // Dark background as per image
            shape = RoundedCornerShape(36.dp)
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
                        Triple("🗺️", "Map", Screen.Map),
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
                    val isSelected = currentScreen == screen
                    
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 56.dp else 48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { onNavigate(screen) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = icon,
                                fontSize = if (isSelected) 24.sp else 20.sp,
                                modifier = Modifier.graphicsLayer(
                                    scaleX = if (isSelected) 1.1f else 1f,
                                    scaleY = if (isSelected) 1.1f else 1f
                                )
                            )
                            if (!isSelected) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.6f),
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
