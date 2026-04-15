package com.example.cleancityapp.presentation.history

data class UploadHistory(
    val id: String,
    val title: String = "",
    val date: String,
    val status: HistoryStatus,
    val description: String
)