package com.example.cleancityapp.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.components.ErrorState

@Composable
fun ProfileLoadGate(
    isLoading: Boolean,
    error: String?,
    user: UserDto?,
    onRetry: () -> Unit,
    onLogout: (() -> Unit)? = null,
    content: @Composable (UserDto) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        user != null -> content(user)
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorState(
                    message = error ?: "Unable to load your profile.",
                    onRetry = onRetry,
                    onLogout = onLogout,
                )
            }
        }
    }
}
