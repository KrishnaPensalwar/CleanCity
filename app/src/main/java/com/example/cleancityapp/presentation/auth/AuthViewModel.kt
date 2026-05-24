package com.example.cleancityapp.presentation.auth

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.CityDto
import com.example.cleancityapp.data.remote.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun fetchCities() {
        if (_state.value.cities.isNotEmpty()) return
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getCities()
                }
                if (response.isSuccessful) {
                    _state.update { it.copy(cities = response.body() ?: emptyList()) }
                }
            } catch (e: Exception) { }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.login(mapOf("email" to email, "password" to pass))
                }
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
                val response = withContext(Dispatchers.IO) {
                    authApi.signUp(
                        mapOf(
                            "name" to name,
                            "mobile" to mobile,
                            "email" to email,
                            "password" to pass,
                            "city" to city
                        )
                    )
                }
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
        sharedPreferences.edit().apply {
            putString("access_token", loginData.token)
            putString("refresh_token", loginData.refreshToken)
            putString("user_id", loginData.profile.id)
            
            if (loginData.roles.size == 1) {
                val role = if (loginData.roles.contains("DRIVER")) "ROLE_DRIVER" else "ROLE_USER"
                putString("user_role", role)
            } else {
                remove("user_role")
            }
            apply()
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
