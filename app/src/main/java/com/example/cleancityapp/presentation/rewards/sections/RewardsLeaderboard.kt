package com.example.cleancityapp.presentation.rewards.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.cleancityapp.data.remote.UserRankDto

@Composable
fun RewardsLeaderboardSection(topUsers: List<UserRankDto>, currentUserRank: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "City leaderboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Hyderabad",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.padding(top = 16.dp))

        topUsers.forEachIndexed { index, user ->
            val rank = index + 1
            LeaderboardRow(
                rank = rank,
                name = user.name,
                points = user.rewardPoints,
                isCurrentUser = rank == currentUserRank,
            )
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    name: String,
    points: Int,
    isCurrentUser: Boolean = false,
) {
    val accent = MaterialTheme.colorScheme.primary
    val containerColor = if (isCurrentUser) accent else MaterialTheme.colorScheme.surface
    val contentColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val rankCircleColor = if (isCurrentUser) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(if (isCurrentUser) 8.dp else 0.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(rankCircleColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = rank.toString(),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = contentColor,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isCurrentUser) "You" else name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                )
                if (isCurrentUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text("Top", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
            Text(
                text = "Community member",
                fontSize = 12.sp,
                color = contentColor.copy(alpha = 0.65f),
            )
        }

        Text(
            text = "$points pts",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else accent,
        )
    }
}
