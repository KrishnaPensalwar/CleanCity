package com.example.cleancityapp.presentation.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.data.remote.toAppErrorMessage
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
    val isSaving: Boolean = false,
    val error: String? = null,
    val isUpdateSuccess: Boolean = false
)

class ProfileViewModel(
    private val authApi: AuthApi,
    private val deviceRepository: DeviceRegistrationRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUser(force = true)
    }

    fun loadUser(force: Boolean = false) {
        if (!force && _state.value.user != null) return
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful && response.body() != null) {
                    val meData = response.body()!!
                    val profile = meData.userProfile ?: meData.driverProfile
                    _state.update { it.copy(user = profile, isLoading = false) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.toAppErrorMessage("Unable to load your profile.")
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.toAppErrorMessage("Unable to load your profile.")
                    )
                }
            }
        }
    }

    fun updateProfile(name: String, phone: String, address: String) {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, isUpdateSuccess = false) }
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.updateProfile(
                        "Bearer $token",
                        mapOf(
                            "name" to name.trim(),
                            "phone" to phone.trim(),
                            "address" to address.trim()
                        )
                    )
                }
                if (response.isSuccessful && response.body() != null) {
                    _state.update {
                        it.copy(user = response.body(), isSaving = false, isUpdateSuccess = true)
                    }
                } else {
                    // Fallback: refresh from /me and keep local edits if API unsupported
                    val me = withContext(Dispatchers.IO) { authApi.getMe("Bearer $token") }
                    val profile = me.body()?.userProfile ?: me.body()?.driverProfile
                    if (profile != null) {
                        val updated = profile.copy(
                            name = name.trim().ifBlank { profile.name },
                            phone = phone.trim().ifBlank { profile.phone },
                            address = address.trim().ifBlank { profile.address }
                        )
                        _state.update {
                            it.copy(
                                user = updated,
                                isSaving = false,
                                isUpdateSuccess = response.isSuccessful || response.code() == 404,
                                error = if (!response.isSuccessful && response.code() != 404) {
                                    response.toAppErrorMessage("Unable to update your profile.")
                                } else {
                                    null
                                }
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                error = response.toAppErrorMessage("Unable to update your profile.")
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "updateProfile failed", e)
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.toAppErrorMessage("Unable to update your profile.")
                    )
                }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        sharedPreferences.edit().putString("theme_mode", mode.name).apply()
    }

    fun resetUpdateStatus() {
        _state.update { it.copy(isUpdateSuccess = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
