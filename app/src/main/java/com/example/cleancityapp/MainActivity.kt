package com.example.cleancityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import com.example.cleancityapp.presentation.components.BottomNavBar
import com.example.cleancityapp.presentation.home.HomeScreen
import com.example.cleancityapp.presentation.main.MainContract
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.main.Screen
import com.example.cleancityapp.presentation.map.MapScreen
import com.example.cleancityapp.presentation.profile.ProfileScreen
import com.example.cleancityapp.presentation.report.ReportScreen
import com.example.cleancityapp.presentation.rewards.RewardsScreen
import com.example.cleancityapp.ui.theme.CleanCityAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CleanCityAppTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                currentScreen = uiState.currentScreen,
                onNavigate = { screen ->
                    viewModel.processIntent(MainContract.Intent.NavigateTo(screen))
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (uiState.currentScreen) {
                is Screen.Home -> HomeScreen(
                    onNavigateToReport = { viewModel.processIntent(MainContract.Intent.NavigateTo(Screen.Report)) }
                )
                is Screen.Report -> ReportScreen()
                is Screen.Map -> MapScreen()
                is Screen.Rewards -> RewardsScreen()
                is Screen.Profile -> ProfileScreen()
            }
        }
    }
}