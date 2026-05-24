package com.example.cleancityapp.presentation.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.RankResponse
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.data.remote.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeState(
    val currentUser: UserDto? = null,
    val userRank: RankResponse? = null,
    val userReports: List<ReportResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        refreshHome()
    }

    fun refreshHome(force: Boolean = false) {
        getMe(force)
        fetchRank(force)
        fetchUserReports(force)
    }

    private fun getMe(force: Boolean = false) {
        if (!force && _state.value.currentUser != null) return
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
                    _state.update { it.copy(currentUser = profile, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun fetchRank(force: Boolean = false) {
        if (!force && _state.value.userRank != null) return
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getUserRank("Bearer $token")
                }
                if (response.isSuccessful) {
                    _state.update { it.copy(userRank = response.body()) }
                }
            } catch (e: Exception) { }
        }
    }

    private fun fetchUserReports(force: Boolean = false) {
        if (!force && _state.value.userReports.isNotEmpty()) return
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.getMeReports("Bearer $token")
                }
                if (response.isSuccessful) {
                    _state.update { it.copy(userReports = response.body() ?: emptyList()) }
                }
            } catch (e: Exception) { }
        }
    }
}
