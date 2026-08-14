package com.example.cleancityapp.presentation.history

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.DriverApi
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.data.remote.toAppErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryState(
    val reports: List<ReportResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HistoryViewModel(
    private val authApi: AuthApi,
    private val driverApi: DriverApi,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    fun fetchReports(isDriver: Boolean, force: Boolean = true) {
        if (!force && _state.value.reports.isNotEmpty()) return

        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val reports = if (isDriver) {
                    driverApi.getAssignedReports(token)
                } else {
                    val response = authApi.getMeReports("Bearer $token")
                    if (response.isSuccessful) response.body() ?: emptyList()
                    else throw IllegalStateException(
                        response.toAppErrorMessage("Unable to load report history.")
                    )
                }
                _state.update { it.copy(reports = reports, isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.toAppErrorMessage("Unable to load report history.")
                    )
                }
            }
        }
    }

    fun invalidate() {
        _state.update { it.copy(reports = emptyList()) }
    }
}

/** Normalize backend status values for filter chips. */
fun normalizeReportStatus(status: String): String {
    return when (status.trim().uppercase()) {
        "APPROVED", "RESOLVED", "COMPLETED", "ACCEPTED" -> "Approved"
        "REJECTED", "DECLINED", "DENIED" -> "Rejected"
        "PENDING", "SUBMITTED", "IN_REVIEW", "IN REVIEW", "OPEN" -> "Pending"
        else -> status.replaceFirstChar { it.uppercase() }
    }
}
