package com.example.cleancityapp.presentation.main

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            fetchInitialData()
        }
    }

    private fun fetchInitialData() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val meData = response.body()!!
                    val profile = meData.userProfile ?: meData.driverProfile
                    _uiState.update { it.copy(currentUser = profile) }
                    registerFCMToken(token)
                    navigateToDashboard()
                } else {
                    _uiState.update { it.copy(currentScreen = Screen.Login) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentScreen = Screen.Login) }
            }
        }
    }

    private fun registerFCMToken(accessToken: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                viewModelScope.launch {
                    try {
                        deviceRepository.registerDevice(accessToken, token)
                    } catch (e: Exception) {
                        Log.e("FCM", "Failed to register token", e)
                    }
                }
            }
        }
    }

    fun processIntent(intent: MainContract.Intent) {
        when (intent) {
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
                val roleString = if (intent.role == UserRole.DRIVER) "ROLE_DRIVER" else "ROLE_USER"
                sharedPreferences.edit().putString("user_role", roleString).apply()
                _uiState.update { it.copy(userRole = intent.role) }
                navigateToDashboard()
            }
            is MainContract.Intent.Logout -> {
                logout()
            }
            is MainContract.Intent.LoginSuccess -> {
                fetchInitialData()
            }
            is MainContract.Intent.ViewReportDetails -> {
                _uiState.update { 
                    it.copy(
                        selectedReport = intent.report,
                        currentScreen = Screen.ReportDetails
                    ) 
                }
            }
            is MainContract.Intent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            else -> {}
        }
    }

    private fun logout() {
        val token = sharedPreferences.getString("access_token", null)
        viewModelScope.launch {
            if (token != null) {
                try {
                    deviceRepository.unregisterDevice(token)
                    deviceRepository.clearCachedToken()
                } catch (e: Exception) { }
            }
            sharedPreferences.edit { clear() }
            _uiState.update {
                MainContract.State(
                    currentScreen = Screen.Login,
                    themeMode = it.themeMode
                )
            }
        }
    }

    private fun navigateToDashboard() {
        val roleString = sharedPreferences.getString("user_role", null)
        val currentUser = _uiState.value.currentUser
        val roles = currentUser?.roles ?: emptyList()
        
        if (roleString == null && roles.size > 1) {
            _uiState.update { it.copy(currentScreen = Screen.RoleSelection) }
            return
        }

        val role = when {
            roleString == "ROLE_DRIVER" -> UserRole.DRIVER
            roleString == "ROLE_USER" -> UserRole.USER
            roles.contains("DRIVER") -> UserRole.DRIVER
            else -> UserRole.USER
        }

        val initialScreen = if (role == UserRole.DRIVER) Screen.DriverDashboard else Screen.Home
        _uiState.update { it.copy(userRole = role, currentScreen = initialScreen) }
    }
}
