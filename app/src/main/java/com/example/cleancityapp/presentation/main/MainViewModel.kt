package com.example.cleancityapp.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val authApi: AuthApi,
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainContract.State())
    val uiState: StateFlow<MainContract.State> = _uiState.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        val savedToken = sharedPreferences.getString("access_token", null)
        if (!savedToken.isNullOrEmpty()) {
            getMe()
            fetchRank()
            navigateToDashboard()
        }
    }

    fun processIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.NavigateTo -> {
                _uiState.update { it.copy(currentScreen = intent.screen, error = null) }
            }
            is MainContract.Intent.SetRole -> {
                _uiState.update { it.copy(userRole = intent.role) }
            }
            is MainContract.Intent.GetMe -> {
                getMe()
            }
            is MainContract.Intent.FetchRank -> {
                fetchRank()
            }
            is MainContract.Intent.Logout -> {
                logout()
            }
            is MainContract.Intent.LoginSuccess -> {
                getMe()
                fetchRank()
                navigateToDashboard()
            }
            else -> {}
        }
    }

    private fun getMe() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            try {
                val response = authApi.getMe("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val role = if (user.role == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
                    _uiState.update { 
                        it.copy(
                            currentUser = user,
                            userRole = role
                        ) 
                    }
                }
            } catch (e: Exception) {}
        }
    }

    private fun fetchRank() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            try {
                val response = authApi.getUserRank("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(userRank = response.body()) }
                }
            } catch (e: Exception) {}
        }
    }

    private fun logout() {
        sharedPreferences.edit().clear().apply()
        _uiState.update { 
            MainContract.State(currentScreen = Screen.Login)
        }
    }

    private fun navigateToDashboard() {
        val roleString = sharedPreferences.getString("user_role", null)
        val role = if (roleString == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
        val initialScreen = when (role) {
            UserRole.USER -> Screen.Home
            UserRole.DRIVER -> Screen.DriverDashboard
        }
        _uiState.update { it.copy(userRole = role, currentScreen = initialScreen) }
    }
}
