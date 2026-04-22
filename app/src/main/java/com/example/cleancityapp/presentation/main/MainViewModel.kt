package com.example.cleancityapp.presentation.main

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.ReportResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

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
        val roleString = sharedPreferences.getString("user_role", null)
        if (!savedToken.isNullOrEmpty()) {
            val role = if (roleString == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
            _uiState.update { it.copy(userRole = role) }
            getMe() // Fetch user profile
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
            is MainContract.Intent.Logout -> {
                logout()
            }
            is MainContract.Intent.LoginSuccess -> {
                getMe()
                navigateToDashboard()
            }
            else -> {
                // Other intents should be handled by specific viewmodels
            }
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
            } catch (e: Exception) {
                // Silently fail if getMe fails during background check
            }
        }
    }

    private fun logout() {
        sharedPreferences.edit().clear().apply()
        _uiState.update { 
            MainContract.State(currentScreen = Screen.Login)
        }
    }

    private fun navigateToDashboard() {
        val initialScreen = when (_uiState.value.userRole) {
            UserRole.USER -> Screen.Home
            UserRole.DRIVER -> Screen.DriverDashboard
        }
        _uiState.update { it.copy(currentScreen = initialScreen) }
    }
}
