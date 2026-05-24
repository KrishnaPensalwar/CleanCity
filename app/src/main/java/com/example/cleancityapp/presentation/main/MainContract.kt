package com.example.cleancityapp.presentation.main

import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.data.remote.UserDto

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Login,
        val userRole: UserRole = UserRole.USER,
        val currentUser: UserDto? = null,
        val selectedReport: ReportResponse? = null,
        val themeMode: ThemeMode = ThemeMode.SYSTEM,
        val deepLinkComplaintId: String? = null
    )
    
    sealed class Intent {
        data class HandleDeepLink(val complaintId: String) : Intent()
        object ClearDeepLink : Intent()
        data class SetThemeMode(val mode: ThemeMode) : Intent()
        data class NavigateTo(val screen: Screen) : Intent()
        data class SetRole(val role: UserRole) : Intent()
        data class SyncScreenState(val screen: Screen) : Intent()
        data class ViewReportDetails(val report: ReportResponse) : Intent()
        
        object GetMe : Intent()
        object LoginSuccess : Intent()
        object Logout : Intent()
    }
}
