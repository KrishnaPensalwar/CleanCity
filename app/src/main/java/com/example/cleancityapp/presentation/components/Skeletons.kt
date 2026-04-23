package com.example.cleancityapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp)
    ) {
        SkeletonBox(modifier = Modifier.size(40.dp, 28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        SkeletonBox(modifier = Modifier.size(60.dp, 12.dp))
    }
}

@Composable
fun ReportItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonBox(modifier = Modifier.size(42.dp), shape = RoundedCornerShape(8.dp))
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            SkeletonBox(modifier = Modifier.size(120.dp, 14.dp))
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonBox(modifier = Modifier.size(80.dp, 10.dp))
        }
        
        SkeletonBox(modifier = Modifier.size(60.dp, 20.dp), shape = RoundedCornerShape(6.dp))
    }
}

@Composable
fun HistoryItemSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(8.dp))
            
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonBox(modifier = Modifier.size(150.dp, 16.dp))
                SkeletonBox(modifier = Modifier.size(100.dp, 12.dp))
                SkeletonBox(modifier = Modifier.size(70.dp, 18.dp), shape = RoundedCornerShape(4.dp))
            }
        }
    }
}

@Composable
fun LeaderboardRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SkeletonCircle(modifier = Modifier.size(24.dp))
            SkeletonBox(modifier = Modifier.size(100.dp, 14.dp))
        }
        SkeletonBox(modifier = Modifier.size(50.dp, 14.dp))
    }
}
