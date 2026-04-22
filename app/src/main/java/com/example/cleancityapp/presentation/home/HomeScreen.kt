package com.example.cleancityapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.ui.theme.*
import java.util.*

@Composable
fun HomeScreen(
    user: UserDto?,
    onNavigateToReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    val greeting = remember {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }
    
    val displayName = user?.name ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "$greeting, $displayName!",
            subtitle = "Hyderabad · ${user?.rewardPoints ?: 0} pts total",
            onProfileClick = onNavigateToProfile
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(num = "14", lbl = "Reports filed", modifier = Modifier.weight(1f))
                StatCard(num = (user?.rewardPoints ?: 0).toString(), lbl = "Total points", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(num = "9", lbl = "Resolved", modifier = Modifier.weight(1f))
                StatCard(num = "#12", lbl = "City rank", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD4E9D4)),
                contentAlignment = Alignment.Center
            ) {
                // Map Dots
                MapDot(Modifier.align(Alignment.CenterStart).offset(x = 60.dp, y = (-10).dp))
                MapDot(Modifier.align(Alignment.BottomEnd).offset(x = (-80).dp, y = (-40).dp))
                MapDot(Modifier.align(Alignment.TopEnd).offset(x = (-60).dp, y = 30.dp))
                MapDot(Modifier.align(Alignment.BottomStart).offset(x = 40.dp, y = (-20).dp))
                
                Text(
                    text = "4 active reports nearby",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D5A2D),
                    modifier = Modifier.offset(y = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            Button(
                onClick = onNavigateToReport,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "+ Report an issue", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recent Activity Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Recent activity",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ReportItem(icon = "🗑️", bg = Color(0xFFC0DD97), loc = "Jubilee Hills, Rd 36", time = "2 hours ago", status = "Pending")
                ReportItem(icon = "♻️", bg = GreenLight, loc = "Banjara Hills, Rd 12", time = "Yesterday", status = "Approved")
                ReportItem(icon = "🚯", bg = Color(0xFFFAC775), loc = "Madhapur Main Rd", time = "3 days ago", status = "Approved", isLast = true)
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            // Weekly Challenge Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Weekly challenge",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Report 5 issues this week to earn 50 bonus points",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "3 / 5 completed",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(num: String, lbl: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp)
    ) {
        Text(text = num, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        Text(text = lbl, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun MapDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(Color(0xFFE24B4A))
            .border(2.dp, Color.White, CircleShape)
    )
}

@Composable
fun ReportItem(icon: String, bg: Color, loc: String, time: String, status: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = loc,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = time,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        val badgeBg = if (status == "Pending") BadgePendingBg else if(status == "Approved" || status == "Resolved") BadgeApprovedBg else BadgeDeclinedBg
        val badgeText = if (status == "Pending") BadgePendingText else if(status == "Approved" || status == "Resolved") BadgeApprovedText else BadgeDeclinedText
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeBg)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = badgeText
            )
        }
    }
    if (!isLast) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant))
    }
}
