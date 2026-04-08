package com.example.cleancityapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopNavBar(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Clean City Smart City",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.alpha(0.8f)
        )
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(top = 2.dp)
                .alpha(0.7f)
        )
    }
}
