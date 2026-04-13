package com.example.cleancityapp.presentation.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainContract.State())
    val uiState: StateFlow<MainContract.State> = _uiState.asStateFlow()

    fun processIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.NavigateTo -> {
                _uiState.update { it.copy(currentScreen = intent.screen) }
            }
            is MainContract.Intent.SetRole -> {
                val initialScreen = when (intent.role) {
                    UserRole.USER -> Screen.Home
                    UserRole.DRIVER -> Screen.DriverDashboard
                }
                _uiState.update { it.copy(userRole = intent.role, currentScreen = initialScreen) }
            }
        }
    }
}
