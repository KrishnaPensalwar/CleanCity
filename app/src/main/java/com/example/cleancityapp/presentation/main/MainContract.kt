package com.example.cleancityapp.presentation.main

import android.net.Uri
import com.example.cleancityapp.data.remote.CityDto
import com.example.cleancityapp.data.remote.ReportResponse
import com.example.cleancityapp.data.remote.UserDto

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Login,
        val userRole: UserRole = UserRole.DRIVER,
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUser: UserDto? = null,
        val cities: List<CityDto> = emptyList(),
        val isReportSuccess: Boolean = false,
        val userReports: List<ReportResponse> = emptyList(),
        val selectedReport: ReportResponse? = null
    )
    
    sealed class Intent {
        data class NavigateTo(val screen: Screen) : Intent()
        data class SetRole(val role: UserRole) : Intent()
        
        data class Login(val email: String, val password: String) : Intent()
        data class SignUp(
            val name: String,
            val mobile: String,
            val email: String,
            val password: String,
            val city: String
        ) : Intent()
        
        data class SubmitReport(
            val imageUri: Uri,
            val description: String,
            val latitude: Double,
            val longitude: Double
        ) : Intent()
        
        data class ViewReportDetails(val report: ReportResponse) : Intent()
        
        object ResetReportStatus : Intent()
        object FetchCities : Intent()
        object FetchUserReports : Intent()
        object GetMe : Intent()
        object ClearError : Intent()
        object LoginSuccess : Intent()
        object Logout : Intent()
    }
}
