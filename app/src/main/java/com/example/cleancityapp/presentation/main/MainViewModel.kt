package com.example.cleancityapp.presentation.main

import android.content.SharedPreferences
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
import kotlinx.coroutines.withTimeoutOrNull

class MainViewModel(
    private val authApi: AuthApi,
    private val deviceRepository: DeviceRegistrationRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<MainContract.State> = _uiState.asStateFlow()

    init {
        if (_uiState.value.isSessionChecking) {
            fetchInitialData()
        }
    }

    private fun buildInitialState(): MainContract.State {
        val themeStr = sharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name)
        val mode = try {
            ThemeMode.valueOf(themeStr ?: ThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        val prompted = sharedPreferences.getBoolean("notification_prompted", false)
        val hasToken = !sharedPreferences.getString("access_token", null).isNullOrEmpty()

        return MainContract.State(
            currentScreen = if (hasToken) Screen.Splash else Screen.Login,
            themeMode = mode,
            notificationsEnabled = notificationsEnabled,
            shouldRequestNotificationPermission = notificationsEnabled && !prompted,
            isSessionChecking = hasToken
        )
    }

    private fun fetchInitialData() {
        val token = sharedPreferences.getString("access_token", null) ?: run {
            _uiState.update { it.copy(isSessionChecking = false, currentScreen = Screen.Login) }
            return
        }
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val meData = response.body()!!
                    val profile = meData.userProfile ?: meData.driverProfile
                    _uiState.update {
                        it.copy(currentUser = profile, isSessionChecking = false)
                    }
                    registerFCMToken(token)
                    navigateToDashboard()
                } else {
                    clearAuthTokens()
                    _uiState.update {
                        it.copy(isSessionChecking = false, currentScreen = Screen.Login, currentUser = null)
                    }
                }
            } catch (e: Exception) {
                // Keep user in-app if they have a token but network failed briefly
                val roleString = sharedPreferences.getString("user_role", null)
                if (roleString != null) {
                    val role = if (roleString == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
                    val screen = if (role == UserRole.DRIVER) Screen.DriverDashboard else Screen.Home
                    _uiState.update {
                        it.copy(
                            isSessionChecking = false,
                            userRole = role,
                            currentScreen = screen
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isSessionChecking = false, currentScreen = Screen.Login)
                    }
                }
                Log.e("MainViewModel", "fetchInitialData failed", e)
            }
        }
    }

    private fun registerFCMToken(accessToken: String) {
        if (!_uiState.value.notificationsEnabled) return
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
                sharedPreferences.edit { putString("user_role", roleString) }
                _uiState.update { it.copy(userRole = intent.role) }
                navigateToDashboard()
            }
            is MainContract.Intent.Logout -> logout()
            is MainContract.Intent.LoginSuccess -> {
                _uiState.update { it.copy(isSessionChecking = true) }
                fetchInitialData()
            }
            is MainContract.Intent.SyncScreenState -> {
                if (_uiState.value.currentScreen != intent.screen) {
                    _uiState.update { it.copy(currentScreen = intent.screen) }
                }
            }
            is MainContract.Intent.ViewReportDetails -> {
                _uiState.update {
                    it.copy(
                        selectedReport = intent.report,
                        currentScreen = Screen.ReportDetails
                    )
                }
            }
            is MainContract.Intent.SetNotificationsEnabled -> {
                sharedPreferences.edit {
                    putBoolean("notifications_enabled", intent.enabled)
                    putBoolean("notification_prompted", true)
                }
                _uiState.update {
                    it.copy(
                        notificationsEnabled = intent.enabled,
                        shouldRequestNotificationPermission = intent.enabled
                    )
                }
                if (intent.enabled) {
                    val token = sharedPreferences.getString("access_token", null)
                    if (token != null) registerFCMToken(token)
                } else {
                    viewModelScope.launch {
                        val token = sharedPreferences.getString("access_token", null)
                        if (token != null) {
                            withTimeoutOrNull(3_000) {
                                try {
                                    deviceRepository.unregisterDevice(token)
                                } catch (_: Exception) { }
                            }
                        }
                        deviceRepository.clearCachedToken()
                    }
                }
            }
            is MainContract.Intent.NotificationPermissionHandled -> {
                sharedPreferences.edit { putBoolean("notification_prompted", true) }
                _uiState.update { it.copy(shouldRequestNotificationPermission = false) }
            }
            is MainContract.Intent.RefreshCurrentUser -> refreshCurrentUser()
        }
    }

    private fun refreshCurrentUser() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!.userProfile ?: response.body()!!.driverProfile
                    _uiState.update { it.copy(currentUser = profile) }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "refreshCurrentUser failed", e)
            }
        }
    }

    private fun logout() {
        val token = sharedPreferences.getString("access_token", null)
        val themeMode = _uiState.value.themeMode
        val notificationsEnabled = _uiState.value.notificationsEnabled

        // Clear auth immediately so UI responds; unregister in background
        clearAuthTokens()
        sharedPreferences.edit {
            putString("theme_mode", themeMode.name)
            putBoolean("notifications_enabled", notificationsEnabled)
            putBoolean("notification_prompted", true)
        }
        _uiState.update {
            MainContract.State(
                currentScreen = Screen.Login,
                themeMode = themeMode,
                notificationsEnabled = notificationsEnabled,
                isSessionChecking = false
            )
        }

        viewModelScope.launch {
            if (token != null) {
                withTimeoutOrNull(3_000) {
                    try {
                        deviceRepository.unregisterDevice(token)
                    } catch (_: Exception) { }
                }
            }
            deviceRepository.clearCachedToken()
        }
    }

    private fun clearAuthTokens() {
        sharedPreferences.edit {
            remove("access_token")
            remove("refresh_token")
            remove("user_id")
            remove("user_role")
            remove("last_registered_fcm_token")
        }
    }

    private fun navigateToDashboard() {
        val roleString = sharedPreferences.getString("user_role", null)
        val currentUser = _uiState.value.currentUser
        val roles = currentUser?.roles ?: emptyList()

        if (roleString == null && roles.size > 1) {
            _uiState.update { it.copy(currentScreen = Screen.RoleSelection, isSessionChecking = false) }
            return
        }

        val role = when {
            roleString == "ROLE_DRIVER" -> UserRole.DRIVER
            roleString == "ROLE_USER" -> UserRole.USER
            roles.contains("DRIVER") -> UserRole.DRIVER
            else -> UserRole.USER
        }

        val initialScreen = if (role == UserRole.DRIVER) Screen.DriverDashboard else Screen.Home
        _uiState.update {
            it.copy(userRole = role, currentScreen = initialScreen, isSessionChecking = false)
        }
    }
}
