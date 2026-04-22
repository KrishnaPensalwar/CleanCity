package com.example.cleancityapp.presentation.main

import android.net.Uri
import com.example.cleancityapp.data.remote.CityDto
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.data.remote.UserDto

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Login,
        val userRole: UserRole = UserRole.USER,
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUser: UserDto? = null,
        val selectedReport: ReportResponse? = null
    )
    
    sealed class Intent {
        data class NavigateTo(val screen: Screen) : Intent()
        data class SetRole(val role: UserRole) : Intent()
        
        data class ViewReportDetails(val report: ReportResponse) : Intent()
        
        object GetMe : Intent()
        object ClearError : Intent()
        object LoginSuccess : Intent()
        object Logout : Intent()
    }
}
