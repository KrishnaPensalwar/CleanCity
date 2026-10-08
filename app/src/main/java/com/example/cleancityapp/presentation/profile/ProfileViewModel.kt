package com.example.cleancityapp.presentation.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.MeResponse
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.data.remote.toAppErrorMessage
import com.example.cleancityapp.presentation.main.ThemeMode
import kotlinx.coroutines.CoroutineDispatcher
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
    private val sharedPreferences: SharedPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUser(force = true)
    }

    fun loadUser(force: Boolean = false) {
        if (!force && _state.value.user != null) return
        val token = sharedPreferences.getString("access_token", null)
        if (token.isNullOrBlank()) {
            _state.update {
                it.copy(
                    user = null,
                    isLoading = false,
                    error = "Unable to load your profile."
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = withContext(ioDispatcher) {
                    authApi.getMe("Bearer $token")
                }
                if (response.isSuccessful) {
                    val profile = response.body()?.profileOrNull()
                    if (profile != null) {
                        _state.update { it.copy(user = profile, isLoading = false, error = null) }
                    } else {
                        _state.update {
                            it.copy(
                                user = null,
                                isLoading = false,
                                error = "Unable to load your profile."
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            user = null,
                            isLoading = false,
                            error = response.toAppErrorMessage("Unable to load your profile.")
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        user = null,
                        isLoading = false,
                        error = e.toAppErrorMessage("Unable to load your profile.")
                    )
                }
            }
        }
    }

    fun updateProfile(name: String, phone: String, address: String) {
        val token = sharedPreferences.getString("access_token", null)
        if (token.isNullOrBlank()) {
            _state.update {
                it.copy(
                    isSaving = false,
                    isUpdateSuccess = false,
                    error = "Unable to update your profile."
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, isUpdateSuccess = false) }
            try {
                val response = withContext(ioDispatcher) {
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
                        it.copy(user = response.body(), isSaving = false, isUpdateSuccess = true, error = null)
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isUpdateSuccess = false,
                            error = response.toAppErrorMessage("Unable to update your profile.")
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        isUpdateSuccess = false,
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

    private fun MeResponse.profileOrNull(): UserDto? = userProfile ?: driverProfile
}
