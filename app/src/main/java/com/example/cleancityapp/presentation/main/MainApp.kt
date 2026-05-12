package com.example.cleancityapp.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Sync NavController state back to ViewModel (for highlight & state consistency)
    LaunchedEffect(currentRoute) {
        currentRoute?.let { route ->
            val screen = when (route) {
                Screen.Home.route -> Screen.Home
                Screen.Report.route -> Screen.Report
                Screen.Map.route -> Screen.Map
                Screen.Rewards.route -> Screen.Rewards
                Screen.History.route -> Screen.History
                Screen.Profile.route -> Screen.Profile
                Screen.DriverDashboard.route -> Screen.DriverDashboard
                Screen.DriverTasks.route -> Screen.DriverTasks
                Screen.DriverRoute.route -> Screen.DriverRoute
                Screen.DriverProfile.route -> Screen.DriverProfile
                Screen.ReportDetails.route -> Screen.ReportDetails
                else -> null
            }
            screen?.let { viewModel.processIntent(NavigateTo(it)) }
        }
    }

    val bottomNavRoutes = remember(uiState.userRole) {
        if (uiState.userRole == UserRole.USER) {
            listOf(Screen.Home.route, Screen.Report.route, Screen.Map.route, Screen.Rewards.route, Screen.History.route)
        } else {
            listOf(Screen.DriverDashboard.route, Screen.DriverTasks.route, Screen.DriverRoute.route, Screen.History.route)
        }
    }

    // Sync ViewModel navigation state with NavController
    LaunchedEffect(uiState.currentScreen) {
        val targetRoute = uiState.currentScreen.route
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                // Clear backstack on core transitions
                if (targetRoute == Screen.Login.route || targetRoute == Screen.Home.route || targetRoute == Screen.DriverDashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
                launchSingleTop = true
            }
        }
    }

    val darkTheme = when (uiState.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    CleanCityAppTheme(darkTheme = darkTheme) {
        val isAuthScreen = currentRoute == Screen.Login.route || currentRoute == Screen.SignUp.route

        BackHandler(enabled = !isAuthScreen && currentRoute != Screen.Home.route && currentRoute != Screen.DriverDashboard.route) {
            if (bottomNavRoutes.contains(currentRoute)) {
                val startDestination = if (uiState.userRole == UserRole.DRIVER) Screen.DriverDashboard.route else Screen.Home.route
                navController.navigate(startDestination) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.popBackStack()
            }
        }
        
        val greeting = remember {
            val calendar = Calendar.getInstance()
            when (calendar.get(Calendar.HOUR_OF_DAY)) {
                in 5..11 -> "Good morning"
                in 12..16 -> "Good afternoon"
                in 17..20 -> "Good evening"
                else -> "Good night"
            }
        }

        val topBarInfo = when (currentRoute) {
            Screen.Home.route -> {
                val displayName = uiState.currentUser?.name ?: "User"
                Pair("$greeting, $displayName!", "Hyderabad · ${uiState.currentUser?.rewardPoints ?: 0} pts")
            }
            Screen.Report.route -> Pair("File a report", "Capture image & details")
            Screen.Map.route -> Pair("Nearby Activity", "Live waste tracking")
            Screen.Rewards.route -> Pair("Rewards", "Earn points for a clean city")
            Screen.History.route -> Pair("My Reports", "History of your contributions")
            Screen.ReportDetails.route -> Pair("Report Details", "Status: ${uiState.selectedReport?.status ?: ""}")
            Screen.Profile.route -> Pair("Profile", "Your account details")
            Screen.DriverDashboard.route -> Pair("Driver Dashboard", "Manage your tasks")
            Screen.DriverTasks.route -> Pair("My Tasks", "Pending assignments")
            Screen.DriverRoute.route -> Pair("Optimized Route", "Follow the path")
            Screen.DriverProfile.route -> Pair("Driver Profile", "Account details")
            else -> Pair("", "")
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (!isAuthScreen && currentRoute != null) {
                    TopNavBar(
                        title = topBarInfo.first,
                        subtitle = topBarInfo.second,
                        onProfileClick = if (currentRoute == Screen.Home.route) {
                            {
                                viewModel.processIntent(GetMe)
                                navController.navigate(if (uiState.userRole == UserRole.DRIVER) Screen.DriverProfile.route else Screen.Profile.route)
                            }
                        } else null,
                        onBackClick = if (currentRoute != Screen.Home.route && currentRoute != Screen.DriverDashboard.route) {
                            {
                                if (bottomNavRoutes.contains(currentRoute)) {
                                    val startDestination = if (uiState.userRole == UserRole.DRIVER) Screen.DriverDashboard.route else Screen.Home.route
                                    navController.navigate(startDestination) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        } else null
                    )
                }
            },
            bottomBar = {
                if (!isAuthScreen && currentRoute != Screen.ReportDetails.route && currentRoute != null) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        userRole = uiState.userRole,
                        onNavigate = { screen ->
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (currentRoute == Screen.Home.route) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.Report.route) },
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
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = { viewModel.processIntent(MainContract.Intent.LoginSuccess) },
                        onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
                    )
                }
                composable(Screen.SignUp.route) {
                    SignUpScreen(
                        onSignUpSuccess = { navController.navigate(Screen.Login.route) },
                        onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                    )
                }
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToReport = { navController.navigate(Screen.Report.route) },
                        onNavigateToProfile = {
                            viewModel.processIntent(GetMe)
                            navController.navigate(Screen.Profile.route)
                        },
                        user = uiState.currentUser
                    )
                }
                composable(Screen.Report.route) {
                    ReportScreen()
                }
                composable(Screen.Map.route) {
                    MapScreen()
                }
                composable(Screen.Rewards.route) {
                    RewardsScreen()
                }
                composable(Screen.History.route) {
                    HistoryScreen(
                        onReportClick = { report ->
                            viewModel.processIntent(MainContract.Intent.ViewReportDetails(report))
                        }
                    )
                }
                composable(Screen.ReportDetails.route) {
                    ReportDetailsScreen(
                        report = uiState.selectedReport,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        user = uiState.currentUser,
                        onBack = { navController.popBackStack() },
                        onLogout = { viewModel.processIntent(Logout) },
                        viewModel = viewModel
                    )
                }
                composable(Screen.DriverDashboard.route) {
                    DriverDashboardScreen(
                        onNavigateToTasks = { navController.navigate(Screen.DriverTasks.route) },
                        onNavigateToRoute = { navController.navigate(Screen.DriverRoute.route) }
                    )
                }
                composable(Screen.DriverTasks.route) {
                    DriverTasksScreen(
                        onNavigateToRoute = { navController.navigate(Screen.DriverRoute.route) }
                    )
                }
                composable(Screen.DriverRoute.route) {
                    DriverRouteScreen()
                }
                composable(Screen.DriverProfile.route) {
                    DriverProfileScreen(
                        user = uiState.currentUser,
                        onBack = { navController.popBackStack() },
                        onLogout = { viewModel.processIntent(Logout) }
                    )
                }
            }
        }
    }
}
