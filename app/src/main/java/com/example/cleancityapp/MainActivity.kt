package com.example.cleancityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.cleancityapp.presentation.history.HistoryScreen
import com.example.cleancityapp.presentation.driver.dashboard.DriverDashboardScreen
import com.example.cleancityapp.presentation.driver.tasks.DriverTasksScreen
import com.example.cleancityapp.presentation.driver.route.DriverRouteScreen
import com.example.cleancityapp.presentation.driver.profile.DriverProfileScreen
import com.example.cleancityapp.presentation.auth.LoginScreen
import com.example.cleancityapp.presentation.auth.SignUpScreen
import com.example.cleancityapp.presentation.history.ReportDetailsScreen
import com.example.cleancityapp.presentation.main.MainContract.Intent.*
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
    val isAuthScreen = uiState.currentScreen == Screen.Login || uiState.currentScreen == Screen.SignUp
    val isFullScreen = isAuthScreen || uiState.currentScreen == Screen.ReportDetails

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isFullScreen) {
                BottomNavBar(
                    currentScreen = uiState.currentScreen,
                    userRole = uiState.userRole,
                    onNavigate = { screen ->
                        viewModel.processIntent(NavigateTo(screen))
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(if (isFullScreen) PaddingValues(0.dp) else innerPadding)) {
            when (uiState.currentScreen) {
                is Screen.Login -> LoginScreen(
                    onLoginSuccess = { viewModel.processIntent(MainContract.Intent.LoginSuccess) },
                    onNavigateToSignUp = { viewModel.processIntent(NavigateTo(Screen.SignUp)) }
                )

                is Screen.SignUp -> SignUpScreen(
                    onSignUpSuccess = { viewModel.processIntent(NavigateTo(Screen.Login)) },
                    onNavigateToLogin = { viewModel.processIntent(NavigateTo(Screen.Login)) }
                )

                // User Screens
                is Screen.Home -> HomeScreen(
                    onNavigateToReport = { viewModel.processIntent(NavigateTo(Screen.Report)) },
                    onNavigateToProfile = {
                        viewModel.processIntent(GetMe)
                        viewModel.processIntent(NavigateTo(Screen.Profile))
                    },
                    user = uiState.currentUser
                )
                is Screen.Report -> ReportScreen()
                is Screen.Map -> MapScreen()
                is Screen.Rewards -> RewardsScreen()
                is Screen.History -> HistoryScreen()
                is Screen.ReportDetails -> ReportDetailsScreen(
                    report = uiState.selectedReport,
                    onBack = { viewModel.processIntent(NavigateTo(Screen.History)) }
                )
                is Screen.Profile -> ProfileScreen(
                    user = uiState.currentUser,
                    onBack = { viewModel.processIntent(NavigateTo(Screen.Home)) },
                    onLogout = { viewModel.processIntent(Logout) }
                )
                
                // Driver Screens
                is Screen.DriverDashboard -> DriverDashboardScreen(
                    onNavigateToTasks = { viewModel.processIntent(NavigateTo(Screen.DriverTasks)) },
                    onNavigateToRoute = { viewModel.processIntent(NavigateTo(Screen.DriverRoute)) }
                )
                is Screen.DriverTasks -> DriverTasksScreen(
                    onNavigateToRoute = { viewModel.processIntent(NavigateTo(Screen.DriverRoute)) }
                )
                is Screen.DriverRoute -> DriverRouteScreen()
                is Screen.DriverProfile -> DriverProfileScreen(
                    user = uiState.currentUser,
                    onBack = { viewModel.processIntent(NavigateTo(Screen.DriverDashboard)) },
                    onLogout = { viewModel.processIntent(Logout) }
                )
            }
        }
    }
}
