package com.example.cleancityapp.presentation.rewards

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.RankResponse
import com.example.cleancityapp.data.remote.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RewardsState(
    val userRank: RankResponse? = null,
    val currentUser: UserDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RewardsViewModel(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(RewardsState())
    val state: StateFlow<RewardsState> = _state.asStateFlow()

    init {
        fetchRewardsData()
    }

    fun fetchRewardsData() {
        if (_state.value.userRank != null && _state.value.currentUser != null) return
        
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Fetch User Rank
                val rankResponse = withContext(Dispatchers.IO) {
                    authApi.getUserRank("Bearer $token")
                }
                
                // Fetch Profile for latest points
                val profileResponse = withContext(Dispatchers.IO) {
                    authApi.getMe("Bearer $token")
                }

                if (rankResponse.isSuccessful && profileResponse.isSuccessful) {
                    val profile = profileResponse.body()?.userProfile ?: profileResponse.body()?.driverProfile
                    _state.update { 
                        it.copy(
                            userRank = rankResponse.body(),
                            currentUser = profile,
                            isLoading = false
                        ) 
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to load rewards data") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
}
