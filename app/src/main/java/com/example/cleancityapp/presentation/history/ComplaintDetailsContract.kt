package com.example.cleancityapp.presentation.history

import com.example.cleancityapp.data.remote.ComplaintDetailsDto

class ComplaintDetailsContract {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val complaint: ComplaintDetailsDto? = null
    )

    sealed class Intent {
        data class LoadComplaint(val complaintId: String) : Intent()
        object ClearError : Intent()
    }
}
