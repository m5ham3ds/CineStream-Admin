package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ConfigScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.UserDetailScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Login : Screen("login", "Login", null)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Config : Screen("config", "Config & OTA", Icons.Default.Settings)
    object Notifications : Screen("notifications", "Notifications", Icons.AutoMirrored.Filled.Send)
    object UserDetail : Screen("user_detail/{userId}", "User Detail", null) {
        fun createRoute(userId: String) = "user_detail/$userId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        Screen.Dashboard,
        Screen.Config,
        Screen.Notifications
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar on main screens
            if (currentDestination?.route in items.map { it.route }) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val startDest = if (FirebaseAuth.getInstance().currentUser != null) Screen.Dashboard.route else Screen.Login.route

        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(onUserClick = { userId ->
                    navController.navigate(Screen.UserDetail.createRoute(userId))
                })
            }
            composable(Screen.Config.route) {
                ConfigScreen()
            }
            composable(Screen.Notifications.route) {
                NotificationScreen()
            }
            composable(Screen.UserDetail.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                UserDetailScreen(
                    userId = userId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
