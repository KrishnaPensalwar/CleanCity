package com.example.cleancityapp.presentation.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.ui.theme.*

@Composable
fun RewardsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopNavBar(
            title = "Rewards",
            subtitle = "Your points & leaderboard"
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Balance Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(vertical = 20.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = "YOUR BALANCE",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp
                )
                Text(
                    text = "340",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(text = "points", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEAF3DE))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Redeem", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3B6D11))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE6F1FB))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Share", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF185FA5))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // How to earn points
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "How to earn points",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                RewardRow(text = "Valid report uploaded", pts = "+25 pts")
                RewardRow(text = "Issue resolved & closed", pts = "+15 pts")
                RewardRow(text = "Weekly challenge bonus", pts = "+50 pts")
                RewardRow(text = "Referral (new user)", pts = "+30 pts", isLast = true)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // City Leaderboard
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "City leaderboard",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                LeaderboardRow(rank = "1", name = "Ravi K.", pts = "580 pts", badgeBg = Color(0xFFFAEEDA), badgeColor = Color(0xFF854F0B))
                LeaderboardRow(rank = "2", name = "Sneha M.", pts = "490 pts", badgeBg = Color(0xFFD3D1C7), badgeColor = Color(0xFF444441))
                
                // Current user highlighting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0FAF4))
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9FE1CB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "12", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF085041))
                        }
                        Text(text = "You", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(text = "340 pts", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun RewardRow(text: String, pts: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            Text(text = pts, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
    if (!isLast) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant))
    }
}

@Composable
fun LeaderboardRow(rank: String, name: String, pts: String, badgeBg: Color, badgeColor: Color, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = rank, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = badgeColor)
            }
            Text(text = name, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(text = pts, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
    }
    if (!isLast) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant))
    }
}
