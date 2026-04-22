package com.example.cleancityapp.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.CityDto
import com.example.cleancityapp.data.remote.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cities: List<CityDto> = emptyList(),
    val isLoginSuccess: Boolean = false,
    val isSignUpSuccess: Boolean = false,
    val loginData: LoginResponse? = null
)

class AuthViewModel(
    private val authApi: AuthApi,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun fetchCities() {
        if (_state.value.cities.isNotEmpty()) return
        viewModelScope.launch {
            try {
                val response = authApi.getCities()
                if (response.isSuccessful) {
                    _state.update { it.copy(cities = response.body() ?: emptyList()) }
                }
            } catch (e: Exception) {
                // Background fetch, ignore error for now
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = authApi.login(mapOf("email" to email, "password" to pass))
                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    saveAuthData(loginData)
                    _state.update { it.copy(isLoading = false, isLoginSuccess = true, loginData = loginData) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Login failed: ${response.message()}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun signUp(name: String, mobile: String, email: String, pass: String, city: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = authApi.signUp(
                    mapOf(
                        "name" to name,
                        "mobile" to mobile,
                        "email" to email,
                        "password" to pass,
                        "city" to city
                    )
                )
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _state.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                } else {
                    _state.update { it.copy(isLoading = false, error = response.body()?.message ?: "Sign up failed") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun saveAuthData(loginData: LoginResponse) {
        sharedPreferences.edit()
            .putString("access_token", loginData.accessToken)
            .putString("refresh_token", loginData.refreshToken)
            .putString("user_role", loginData.user.role)
            .apply()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
