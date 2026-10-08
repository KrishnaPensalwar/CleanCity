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

    // Driver Screens
    object DriverDashboard : Screen("driver_dashboard")
    object DriverTasks : Screen("driver_tasks")
    object DriverRoute : Screen("driver_route")
    object DriverProfile : Screen("driver_profile")
}

enum class UserRole {
    USER, DRIVER
}
