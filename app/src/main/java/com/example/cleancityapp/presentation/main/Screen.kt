package com.example.cleancityapp.presentation.main

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Report : Screen("report")
    object Map : Screen("map")
    object Rewards : Screen("rewards")
    object Profile : Screen("profile")
}
