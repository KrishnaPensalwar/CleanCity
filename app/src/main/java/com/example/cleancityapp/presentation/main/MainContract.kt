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
        val deepLinkComplaintId: String? = null,
        /** True while validating an existing session on cold start. */
        val isSessionChecking: Boolean = false,
        val notificationsEnabled: Boolean = true,
        val shouldRequestNotificationPermission: Boolean = false
    )
    
    sealed class Intent {
        data class HandleDeepLink(val complaintId: String) : Intent()
        object ClearDeepLink : Intent()
        data class SetThemeMode(val mode: ThemeMode) : Intent()
        data class SetRole(val role: UserRole) : Intent()
        data class SyncScreenState(val screen: Screen) : Intent()
        data class ViewReportDetails(val report: ReportResponse) : Intent()
        data class SetNotificationsEnabled(val enabled: Boolean) : Intent()
        object NotificationPermissionHandled : Intent()
        object RefreshCurrentUser : Intent()
        object LoginSuccess : Intent()
        object Logout : Intent()
    }
}
