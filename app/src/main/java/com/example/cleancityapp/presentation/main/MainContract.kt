package com.example.cleancityapp.presentation.main

import com.example.cleancityapp.data.remote.UserDto

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Login,
        val userRole: UserRole = UserRole.DRIVER,
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUser: UserDto? = null
    )
    
    sealed class Intent {
        data class NavigateTo(val screen: Screen) : Intent()
        data class SetRole(val role: UserRole) : Intent()
        
        data class Login(val email: String, val password: String) : Intent()
        data class SignUp(
            val name: String,
            val mobile: String,
            val email: String,
            val password: String
        ) : Intent()
        
        object GetMe : Intent()
        object ClearError : Intent()
        object LoginSuccess : Intent()
    }
}
