package com.example.cleancityapp.presentation.history

import android.content.Context
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
import retrofit2.Response

data class HistoryState(
    val reports: List<ReportResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HistoryViewModel(
    private val authApi: AuthApi,
    private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun fetchReports(isDriver: Boolean) {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = retryApiCall(3) {
                if (isDriver) {
                    authApi.getAssignedReports("Bearer $token")
                } else {
                    authApi.getMeReports("Bearer $token")
                }
            }
            result?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    _state.update { it.copy(reports = response.body()!!, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to fetch history") }
                }
            } ?: run {
                _state.update { it.copy(isLoading = false, error = "Connection failed") }
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
}
