package com.example.cleancityapp.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cleancityapp.presentation.map.sections.LegendItem
import com.example.cleancityapp.presentation.map.sections.MapActivityItem
import com.example.cleancityapp.presentation.map.sections.MapHeatmapCard

@Composable
fun MapScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            MapHeatmapCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Map legend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LegendItem(Color(0xFFE24B4A), "High density")
                LegendItem(Color(0xFFEF9F27), "Medium")
                LegendItem(Color(0xFF4CAF50), "Low")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nearby activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            MapActivityItem(icon = "🗑️", title = "Illegal dumping", area = "Rd No. 36", status = "Pending")
            MapActivityItem(icon = "🚰", title = "Water leakage", area = "Banjara Hills", status = "Resolved")
            MapActivityItem(icon = "💡", title = "Street light out", area = "Madhapur", status = "Pending")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
