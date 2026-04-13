package com.example.cleancityapp.presentation.main

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Home,
        val userRole: UserRole = UserRole.DRIVER // Defaulting to DRIVER for now to see changes
    )
    
    sealed class Intent {
        data class NavigateTo(val screen: Screen) : Intent()
        data class SetRole(val role: UserRole) : Intent()
    }
}
