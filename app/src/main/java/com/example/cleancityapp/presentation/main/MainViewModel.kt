package com.example.cleancityapp.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

class MainViewModel(
    private val authApi: AuthApi,
    private val deviceRepository: DeviceRegistrationRepository,
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainContract.State())
    val uiState: StateFlow<MainContract.State> = _uiState.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        loadTheme()
        checkLoggedIn()
    }

    private fun loadTheme() {
        val themeStr = sharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name)
        val mode = try {
            ThemeMode.valueOf(themeStr ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
        _uiState.update { it.copy(themeMode = mode) }
    }

    private fun checkLoggedIn() {
        val savedToken = sharedPreferences.getString("access_token", null)
        if (!savedToken.isNullOrEmpty()) {
            getMe()
            fetchRank()
            registerFCMToken(savedToken)
            navigateToDashboard()
        }
    }

    private fun registerFCMToken(accessToken: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                viewModelScope.launch {
                    try {
                        deviceRepository.registerDevice(accessToken, token)
                        Log.d("FCM", "Registered token on startup")
                    } catch (e: Exception) {
                        Log.e("FCM", "Failed to register token on startup", e)
                    }
                }
            }
        }
    }

    fun processIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.NavigateTo -> {
                _uiState.update { it.copy(currentScreen = intent.screen, error = null) }
            }
            is MainContract.Intent.HandleDeepLink -> {
                _uiState.update { it.copy(deepLinkComplaintId = intent.complaintId) }
            }
            is MainContract.Intent.ClearDeepLink -> {
                _uiState.update { it.copy(deepLinkComplaintId = null) }
            }
            is MainContract.Intent.SetThemeMode -> {
                sharedPreferences.edit { putString("theme_mode", intent.mode.name) }
                _uiState.update { it.copy(themeMode = intent.mode) }
            }
            is MainContract.Intent.SetRole -> {
                _uiState.update { it.copy(userRole = intent.role) }
            }
            is MainContract.Intent.GetMe -> {
                getMe()
            }
            is MainContract.Intent.FetchUserReports -> {
                fetchUserReports()
            }
            is MainContract.Intent.FetchRank -> {
                fetchRank()
            }
            is MainContract.Intent.Logout -> {
                logout()
            }
            is MainContract.Intent.LoginSuccess -> {
                val token = sharedPreferences.getString("access_token", null)
                if (token != null) registerFCMToken(token)
                getMe()
                fetchRank()
                fetchUserReports()
                navigateToDashboard()
            }
            is MainContract.Intent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            is MainContract.Intent.ViewReportDetails -> {
                _uiState.update { 
                    it.copy(
                        selectedReport = intent.report,
                        currentScreen = Screen.ReportDetails
                    ) 
                }
            }
            else -> {}
        }
    }

    private fun fetchUserReports() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMeReports("Bearer $token")
                }
                if (response.isSuccessful) {
                    _uiState.update { it.copy(userReports = response.body() ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch reports") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun getMe() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val role = if (user.role == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
                    _uiState.update { 
                        it.copy(
                            currentUser = user,
                            userRole = role,
                            isLoading = false
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch user data") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun fetchRank() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getUserRank("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(userRank = response.body(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch rank") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun logout() {
        val token = sharedPreferences.getString("access_token", null)
        if (token != null) {
            viewModelScope.launch {
                try {
                    deviceRepository.unregisterDevice(token)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to unregister device on logout", e)
                }
            }
        }

        sharedPreferences.edit {
            remove("access_token")
            remove("user_role")
        }

        _uiState.update {
            it.copy(
                currentScreen = Screen.Login,
                currentUser = null,
                userReports = emptyList(),
                selectedReport = null,
                userRank = null
            )
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
