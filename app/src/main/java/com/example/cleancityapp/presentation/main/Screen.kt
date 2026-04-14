package com.example.cleancityapp.presentation.main

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    
    // User Screens
    object Home : Screen("home")
    object Report : Screen("report")
    object Map : Screen("map")
    object Rewards : Screen("rewards")
    object Profile : Screen("profile")

    // Driver Screens
    object DriverDashboard : Screen("driver_dashboard")
    object DriverTasks : Screen("driver_tasks")
    object DriverRoute : Screen("driver_route")
    object DriverProfile : Screen("driver_profile")
}

enum class UserRole {
    USER, DRIVER
}
