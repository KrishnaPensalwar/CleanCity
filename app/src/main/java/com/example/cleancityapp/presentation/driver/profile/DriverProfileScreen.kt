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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.driver.dashboard.sections.StatCard
import com.example.cleancityapp.presentation.driver.route.DetailRow
import com.example.cleancityapp.presentation.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DriverProfileScreen(
    user: UserDto?,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val name = "${state.user?.name ?: user?.name ?: "Driver"}"
    val email = "${state.user?.email ?: user?.email ?: "driver@example.com"}"

    val initials = try {
        name.split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
            .uppercase()
            .ifEmpty { "D" }
    } catch (e: Exception) {
        "D"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(11.dp),
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

                        Text(
                            text = name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        Text(
                            text = "Email: $email",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Hyderabad Zone B · Active",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), "142", "Total tasks")
                StatCard(modifier = Modifier.weight(1f), "96%", "Completion")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), "4.8", "Rating")
                StatCard(modifier = Modifier.weight(1f), "28d", "Streak")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {

                Column(modifier = Modifier.padding(12.dp)) {

                    Text(
                        text = "Vehicle info",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("Reg. number", "KA-05-HB-4521")
                    DetailRow("Type", "Compactor truck")
                    DetailRow("Zone", "Hyderabad Zone B")
                    DetailRow("Shift", "6:00 AM – 2:00 PM")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(11.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {

                Column(modifier = Modifier.padding(12.dp)) {

                    SettingRow("Notifications", "On")

                    SettingRow("Location sharing", "On")

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

                        Text(
                            text = "›",
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(label: String, value: String) {

    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = colorScheme.onSurface
        )

        Text(
            text = value,
            fontSize = 13.sp,
            color = colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}