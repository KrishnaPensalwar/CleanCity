package com.example.cleancityapp.presentation.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.repository.ComplaintDetailsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComplaintDetailsViewModel(
    private val repository: ComplaintDetailsRepository,
    private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ComplaintDetailsContract.State())
    val state = _state.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun processIntent(intent: ComplaintDetailsContract.Intent) {
        when (intent) {
            is ComplaintDetailsContract.Intent.LoadComplaint -> loadComplaint(intent.complaintId)
            is ComplaintDetailsContract.Intent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun loadComplaint(complaintId: String) {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getComplaintDetails(token, complaintId)
                .onSuccess { complaint ->
                    _state.update { it.copy(isLoading = false, complaint = complaint) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.localizedMessage) }
                }
        }
    }
}
