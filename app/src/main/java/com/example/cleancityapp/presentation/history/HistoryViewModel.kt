package com.example.cleancityapp.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.presentation.main.MainContract
import com.example.cleancityapp.presentation.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val mainViewModel: MainViewModel
) : ViewModel() {

    init {
        mainViewModel.processIntent(MainContract.Intent.FetchUserReports)
    }

    val historyList: StateFlow<List<UploadHistory>> = mainViewModel.uiState
        .map { state ->
            state.userReports.map {
                UploadHistory(
                    id = it.id,
                    title = "Upload #${it.id.take(4)}", // Mock title as it might be missing
                    date = it.createdAt,
                    status = getStatus(it.status),
                    description = it.description
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

fun getStatus(status: String): HistoryStatus {
    if (status.equals("pending", ignoreCase = true)) return HistoryStatus.PENDING
    else if (status.equals("approved", ignoreCase = true)) return HistoryStatus.APPROVED
    return HistoryStatus.REJECTED
}
