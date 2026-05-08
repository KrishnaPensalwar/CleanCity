package com.example.cleancityapp.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.*
import com.example.cleancityapp.presentation.main.MainContract
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    user: UserDto?,
    onNavigateToReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
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

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    val displayName = user?.name ?: state.currentUser?.name ?: "User"

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.processIntent(MainContract.Intent.FetchUserReports)
        viewModel.processIntent(MainContract.Intent.FetchRank)
    }

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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.isLoading && state.currentUser == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCardSkeleton(modifier = Modifier.weight(1f))
                        StatCardSkeleton(modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCardSkeleton(modifier = Modifier.weight(1f))
                        StatCardSkeleton(modifier = Modifier.weight(1f))
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(num = (state.currentUser?.reportsFiled ?: 0).toString(), lbl = "Reports filed", modifier = Modifier.weight(1f))
                        StatCard(num = (state.currentUser?.rewardPoints ?: 0).toString(), lbl = "Total points", modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(num = (state.currentUser?.reportsResolved ?: 0).toString(), lbl = "Resolved", modifier = Modifier.weight(1f))
                        StatCard(num = state.userRank?.currentUser?.rank?.toString() ?: "N/A", lbl = "City rank", modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // Map Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MapDot(Modifier.align(Alignment.CenterStart).offset(x = 60.dp, y = (-10).dp))
                    MapDot(Modifier.align(Alignment.BottomEnd).offset(x = (-80).dp, y = (-40).dp))
                    MapDot(Modifier.align(Alignment.TopEnd).offset(x = (-60).dp, y = 30.dp))
                    MapDot(Modifier.align(Alignment.BottomStart).offset(x = 40.dp, y = (-20).dp))
                    
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "4 active reports nearby",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            
            Button(
                onClick = onNavigateToReport,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = "+ Report an issue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // Recent Activity Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recent activity",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    if (state.isLoading && state.userReports.isEmpty()) {
                        repeat(3) { ReportItemSkeleton() }
                    } else if (state.userReports.isEmpty()) {
                        Text(
                            text = "No recent activity",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        state.userReports.take(3).forEachIndexed { index, report ->
                            val date = remember(report.timestamp) {
                                val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                                sdf.format(Date(report.timestamp))
                            }
                            ReportItem(
                                icon = if (report.description.contains("garbage", true)) "🗑️" else "📍",
                                bg = if (report.status.uppercase() == "APPROVED") GreenLight else Color(0xFFFAEEDA),
                                loc = report.description,
                                time = date,
                                status = report.status,
                                isLast = index == state.userReports.take(3).size - 1
                            )
                        }
                    }
                }
            }

            // Weekly Challenge Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly challenge",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Report 5 issues this week to earn 50 bonus points",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "3 / 5 completed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "60%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

    }
}

@Composable
fun StatCard(num: String, lbl: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = num,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = lbl,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MapDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(
        modifier = modifier
            .size(16.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(Color(0xFFE24B4A))
            .border(2.dp, Color.White, CircleShape)
    )
}

@Composable
fun ReportItem(icon: String, bg: Color, loc: String, time: String, status: String, isLast: Boolean = false) {
    val animatedProgress by animateFloatAsState(targetValue = 1f, animationSpec = tween(500), label = "itemEntry")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = animatedProgress, translationX = (1f - animatedProgress) * 50f)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = bg
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 20.sp)
            }
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = loc,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = time,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        val badgeBg = if (status == "Pending") BadgePendingBg else if(status.equals("Approved", ignoreCase = true) || status.equals("Resolved",ignoreCase = true)) BadgeApprovedBg else BadgeDeclinedBg
        val badgeText = if (status == "Pending") BadgePendingText else if(status.equals("Approved", ignoreCase = true) || status.equals("Resolved",ignoreCase = true)) BadgeApprovedText else BadgeDeclinedText
        
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = badgeBg
        ) {
            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = badgeText,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
    if (!isLast) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

