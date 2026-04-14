package com.example.cleancityapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val authApi: AuthApi) : ViewModel() {
    private val _uiState = MutableStateFlow(MainContract.State())
    val uiState: StateFlow<MainContract.State> = _uiState.asStateFlow()

    private var accessToken: String? = null

    fun processIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.NavigateTo -> {
                _uiState.update { it.copy(currentScreen = intent.screen, error = null) }
            }
            is MainContract.Intent.SetRole -> {
                _uiState.update { it.copy(userRole = intent.role) }
            }
            is MainContract.Intent.Login -> {
                login(intent.email, intent.password)
            }
            is MainContract.Intent.SignUp -> {
                signUp(intent.name, intent.mobile, intent.email, intent.password)
            }
            is MainContract.Intent.GetMe -> {
                getMe()
            }
            is MainContract.Intent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            is MainContract.Intent.LoginSuccess -> {
                navigateToDashboard()
            }
        }
    }

    private fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = retryApiCall(3) {
                authApi.login(mapOf("email" to email, "password" to pass))
            }

            result?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    accessToken = loginData.accessToken
                    val role = if (loginData.user.role == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
                    _uiState.update { 
                        it.copy(
                            userRole = role, 
                            isLoading = false,
                            currentUser = loginData.user
                        ) 
                    }
                    navigateToDashboard()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Login failed: ${response.message()}") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed after retries") }
            }
        }
    }

    private fun getMe() {
        val token = accessToken ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = retryApiCall(3) {
                authApi.getMe("Bearer $token")
            }
            
            result?.let { response ->
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
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch profile") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed") }
            }
        }
    }

    private fun signUp(name: String, mobile: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = retryApiCall(3) {
                authApi.signUp(
                    mapOf(
                        "name" to name,
                        "mobile" to mobile,
                        "email" to email,
                        "password" to pass
                    )
                )
            }

            result?.let { response ->
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.update { it.copy(isLoading = false, currentScreen = Screen.Login) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.body()?.message ?: "Sign up failed") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed after retries") }
            }
        }
    }

    private suspend fun <T> retryApiCall(
        times: Int,
        initialDelay: Long = 1000,
        block: suspend () -> Response<T>
    ): Response<T>? {
        var currentDelay = initialDelay
        repeat(times) { attempt ->
            try {
                val response = block()
                if (response.isSuccessful) return response
                if (response.code() in 400..499) return response
            } catch (e: Exception) {
                if (attempt == times - 1) return null
            }
            delay(currentDelay)
            currentDelay *= 2
        }
        return null
    }

    private fun navigateToDashboard() {
        val initialScreen = when (_uiState.value.userRole) {
            UserRole.USER -> Screen.Home
            UserRole.DRIVER -> Screen.DriverDashboard
        }
        _uiState.update { it.copy(currentScreen = initialScreen) }
    }
}
