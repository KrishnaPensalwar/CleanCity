package com.example.cleancityapp.presentation.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cleancityapp.presentation.auth.LoginScreen
import com.example.cleancityapp.presentation.auth.SignUpScreen
import com.example.cleancityapp.presentation.components.BottomNavBar
import com.example.cleancityapp.presentation.components.TopNavBar
import com.example.cleancityapp.presentation.driver.dashboard.DriverDashboardScreen
import com.example.cleancityapp.presentation.driver.profile.DriverProfileScreen
import com.example.cleancityapp.presentation.driver.route.DriverRouteScreen
import com.example.cleancityapp.presentation.driver.tasks.DriverTasksScreen
import com.example.cleancityapp.presentation.history.HistoryScreen
import com.example.cleancityapp.presentation.history.ReportDetailsScreen
import com.example.cleancityapp.presentation.home.HomeScreen
import com.example.cleancityapp.presentation.main.MainContract.Intent.GetMe
import com.example.cleancityapp.presentation.main.MainContract.Intent.Logout
import com.example.cleancityapp.presentation.main.MainContract.Intent.NavigateTo
import com.example.cleancityapp.presentation.map.MapScreen
import com.example.cleancityapp.presentation.profile.ProfileScreen
import com.example.cleancityapp.presentation.report.ReportScreen
import com.example.cleancityapp.presentation.rewards.RewardsScreen
import com.example.cleancityapp.ui.theme.CleanCityAppTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun MainApp(viewModel: MainViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    val darkTheme = when (uiState.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    CleanCityAppTheme(darkTheme = darkTheme) {
        val currentScreen = uiState.currentScreen
        val isAuthScreen = currentScreen == Screen.Login || currentScreen == Screen.SignUp
        
        val greeting = remember {
            val calendar = Calendar.getInstance()
            when (calendar.get(Calendar.HOUR_OF_DAY)) {
                in 5..11 -> "Good morning"
                in 12..16 -> "Good afternoon"
                in 17..20 -> "Good evening"
                else -> "Good night"
            }
        }

        val topBarInfo = when (currentScreen) {
            is Screen.Home -> {
                val displayName = uiState.currentUser?.name ?: "User"
                Pair("$greeting, $displayName!", "Hyderabad · ${uiState.currentUser?.rewardPoints ?: 0} pts")
            }
            is Screen.Report -> Pair("File a report", "Capture image & details")
            is Screen.Map -> Pair("Nearby Activity", "Live waste tracking")
            is Screen.Rewards -> Pair("Rewards", "Earn points for a clean city")
            is Screen.History -> Pair("My Reports", "History of your contributions")
            is Screen.ReportDetails -> Pair("Report Details", "Status: ${uiState.selectedReport?.status ?: ""}")
            is Screen.Profile -> Pair("Profile", "Your account details")
            is Screen.DriverDashboard -> Pair("Driver Dashboard", "Manage your tasks")
            is Screen.DriverTasks -> Pair("My Tasks", "Pending assignments")
            is Screen.DriverRoute -> Pair("Optimized Route", "Follow the path")
            is Screen.DriverProfile -> Pair("Driver Profile", "Account details")
            else -> Pair("", "")
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (!isAuthScreen) {
                    TopNavBar(
                        title = topBarInfo.first,
                        subtitle = topBarInfo.second,
                        onProfileClick = if (currentScreen == Screen.Home) {
                            {
                                viewModel.processIntent(GetMe)
                                viewModel.processIntent(NavigateTo(if (uiState.userRole == UserRole.DRIVER) Screen.DriverProfile else Screen.Profile))
                            }
                        } else null,
                        onBackClick = if (currentScreen != Screen.Home && currentScreen != Screen.DriverDashboard) {
                            {
                                val backScreen = when (currentScreen) {
                                    Screen.ReportDetails -> Screen.History
                                    Screen.Profile, Screen.Report, Screen.Map, Screen.Rewards, Screen.History -> Screen.Home
                                    Screen.DriverTasks, Screen.DriverRoute, Screen.DriverProfile -> Screen.DriverDashboard
                                    else -> Screen.Home
                                }
                                viewModel.processIntent(NavigateTo(backScreen))
                            }
                        } else null
                    )
                }
            },
            bottomBar = {
                if (!isAuthScreen && currentScreen != Screen.ReportDetails) {
                    BottomNavBar(
                        currentScreen = currentScreen,
                        userRole = uiState.userRole,
                        onNavigate = { screen ->
                            viewModel.processIntent(NavigateTo(screen))
                        }
                    )
                }
            },
            floatingActionButton = {
                if (currentScreen == Screen.Home) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.processIntent(NavigateTo(Screen.Report)) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = {
                            Text("Report issue", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        },
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                when (currentScreen) {
                    is Screen.Login -> LoginScreen(
                        onLoginSuccess = { viewModel.processIntent(MainContract.Intent.LoginSuccess) },
                        onNavigateToSignUp = { viewModel.processIntent(NavigateTo(Screen.SignUp)) }
                    )

                    is Screen.SignUp -> SignUpScreen(
                        onSignUpSuccess = { viewModel.processIntent(NavigateTo(Screen.Login)) },
                        onNavigateToLogin = { viewModel.processIntent(NavigateTo(Screen.Login)) }
                    )

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
}
