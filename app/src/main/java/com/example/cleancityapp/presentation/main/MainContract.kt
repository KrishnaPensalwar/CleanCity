package com.example.cleancityapp.presentation.main

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Login,
        val userRole: UserRole = UserRole.DRIVER
    )
    
    sealed class Intent {
        data class NavigateTo(val screen: Screen) : Intent()
        data class SetRole(val role: UserRole) : Intent()
        object LoginSuccess : Intent()
    }
}
