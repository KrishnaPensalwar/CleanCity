package com.example.cleancityapp.presentation.components

import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cleancityapp.ui.theme.StatusSuccessContainer
import com.example.cleancityapp.ui.theme.StatusSuccessContent
import com.example.cleancityapp.ui.theme.StatusWarningContainer
import com.example.cleancityapp.ui.theme.StatusWarningContent

/**
 * Badge for driver task status. "Completed" renders as success, anything else
 * (e.g. "Pending") renders as a warning. Colors come from the central palette.
 */
@Composable
fun TaskStatusBadge(status: String, modifier: Modifier = Modifier) {
    val completed = status.equals("Completed", ignoreCase = true)
    Badge(
        modifier = modifier,
        containerColor = if (completed) StatusSuccessContainer else StatusWarningContainer,
        contentColor = if (completed) StatusSuccessContent else StatusWarningContent,
    ) {
        Text(status)
    }
}
