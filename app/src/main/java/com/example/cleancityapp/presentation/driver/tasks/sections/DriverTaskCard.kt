package com.example.cleancityapp.presentation.driver.tasks.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DriverTaskCard(
    id: String,
    location: String,
    description: String,
    date: String,
    distance: String? = null,
    status: String,
    isHighlight: Boolean = false,
    onNavigate: (() -> Unit)? = null,
    onMarkDone: (() -> Unit)? = null,
) {
    val accent = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isHighlight) Modifier.border(1.5.dp, accent, RoundedCornerShape(11.dp))
                else Modifier,
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = id,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlight) Color(0xFF185FA5) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Badge(
                    containerColor = if (status == "Completed") Color(0xFFEAF3DE) else Color(0xFFFAEEDA),
                    contentColor = if (status == "Completed") Color(0xFF3B6D11) else Color(0xFF854F0B),
                ) {
                    Text(status)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = location, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Reported on: $date", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (distance != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = distance, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (onNavigate != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onNavigate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                        shape = RoundedCornerShape(9.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                    ) {
                        Text("Navigate", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { onMarkDone?.invoke() },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFF3B6D11)),
                        shape = RoundedCornerShape(9.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3B6D11)),
                    ) {
                        Text("Mark done", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
