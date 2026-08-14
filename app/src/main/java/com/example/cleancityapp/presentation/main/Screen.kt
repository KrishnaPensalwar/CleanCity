package com.example.cleancityapp.presentation.main

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object RoleSelection : Screen("role_selection")
    object TestAccounts : Screen("test_accounts")
    
    // User Screens
    object Home : Screen("home")
    object Report : Screen("report")
    object Rewards : Screen("rewards")
    object History : Screen("history")
    object ReportDetails : Screen("report_details")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object PrivacyPolicy : Screen("privacy_policy")
    data class ComplaintDetails(val id: String) : Screen("complaint_details/{id}") {
        fun createRoute(id: String) = "complaint_details/$id"
    }

    // Driver Screens
    object DriverDashboard : Screen("driver_dashboard")
    object DriverTasks : Screen("driver_tasks")
    object DriverRoute : Screen("driver_route")
    object DriverProfile : Screen("driver_profile")

    // Kept for deep-link / legacy route safety; Map feature is disabled in nav.
    object Map : Screen("map")
}

enum class UserRole {
    USER, DRIVER
}
