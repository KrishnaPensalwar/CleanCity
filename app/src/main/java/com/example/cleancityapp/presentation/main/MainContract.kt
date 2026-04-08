package com.example.cleancityapp.presentation.main

class MainContract {
    data class State(
        val currentScreen: Screen = Screen.Home
    )
    
    sealed class Intent {
        data class NavigateTo(val screen: Screen) : Intent()
    }
}
