package com.example.cleancityapp.presentation.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import com.example.cleancityapp.presentation.main.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProfileState(
    val user: UserDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val authApi: AuthApi,
    private val deviceRepository: DeviceRegistrationRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        if (_state.value.user != null) return
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val meData = response.body()!!
                    val profile = meData.userProfile ?: meData.driverProfile
                    _state.update { it.copy(user = profile, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        sharedPreferences.edit().putString("theme_mode", mode.name).apply()
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        val token = sharedPreferences.getString("access_token", null)
        viewModelScope.launch {
            if (token != null) {
                try {
                    deviceRepository.unregisterDevice(token)
                    deviceRepository.clearCachedToken()
                } catch (e: Exception) { }
            }
            sharedPreferences.edit().clear().apply()
            onLogoutSuccess()
        }
    }
}
