package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val subtitle: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", "", Icons.Default.Lock)
    object Dashboard : Screen("dashboard", "Dashboard", "Overview & Analytics", Icons.Default.Dashboard)
    object Config : Screen("config", "Config", "App & System Settings", Icons.Default.Settings)
    object Notifications : Screen("notifications", "Notifications", "Send Push Notifications", Icons.Default.Notifications)
    object Profile : Screen("profile", "Admin Profile", "Manage Admin Account", Icons.Default.Person)
    object UserDetail : Screen("user_detail/{userId}", "User Detail", "", Icons.Default.Person) {
        fun createRoute(userId: String) = "user_detail/$userId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    
    val mainScreens = listOf(Screen.Dashboard, Screen.Config, Screen.Notifications, Screen.Profile)
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    
    val isMainScreen = currentRoute in mainScreens.map { it.route }
    val currentScreen = mainScreens.find { it.route == currentRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainScreen,
        drawerContent = {
            if (isMainScreen) {
                ModalDrawerSheet(
                    drawerContainerColor = DarkBackground,
                    modifier = Modifier.width(320.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(PrimaryPurple, PrimaryBlue)
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("CineStream Admin", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                Text("Administrator Panel", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.close() } },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Main Menu
                        mainScreens.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            DrawerItem(
                                icon = screen.icon,
                                title = screen.title,
                                subtitle = screen.subtitle,
                                selected = selected,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("SYSTEM", color = TextSecondary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DrawerItem(
                            icon = Icons.Default.Security,
                            title = "Security",
                            subtitle = "Security & Access",
                            selected = false,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DrawerItem(
                            icon = Icons.Default.History,
                            title = "Activity Logs",
                            subtitle = "View System Activity",
                            selected = false,
                            onClick = {}
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Footer Actions
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceVariant, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = TextSecondary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Dark Mode", color = Color.White, fontWeight = FontWeight.Medium)
                                    Text("Enabled", color = PrimaryBlue, style = MaterialTheme.typography.bodySmall)
                                }
                                Switch(checked = true, onCheckedChange = {})
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    coroutineScope.launch { drawerState.close() }
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                                }
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = ErrorRed)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Logout", color = ErrorRed, fontWeight = FontWeight.Medium)
                                    Text("Sign out from this account", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("v1.0.0", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = DarkBackground,
            topBar = {
                if (isMainScreen && currentScreen != null) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(currentScreen.title, color = Color.White, fontWeight = FontWeight.Bold)
                                if (currentScreen == Screen.Dashboard) {
                                    Text("Welcome back, Admin", color = PrimaryBlue, style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Text(currentScreen.subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(40.dp)
                                    .background(PrimaryBlue.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(10.dp)
                                        .background(SuccessGreen, CircleShape)
                                        .border(2.dp, DarkBackground, CircleShape)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkBackground,
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                if (isMainScreen) {
                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = TextSecondary,
                        tonalElevation = 0.dp
                    ) {
                        listOf(Screen.Dashboard, Screen.Config, Screen.Notifications).forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val startDest = try {
                if (FirebaseAuth.getInstance().currentUser != null) Screen.Dashboard.route else Screen.Login.route
            } catch (e: Exception) {
                Screen.Login.route
            }
            
            NavHost(
                navController = navController,
                startDestination = startDest,
                modifier = Modifier.padding(innerPadding).background(DarkBackground)
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
                composable(Screen.Profile.route) {
                    AdminProfileScreen(
                        onLogoutClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
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
}

@Composable
fun DrawerItem(icon: ImageVector, title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    val modifier = if (selected) {
        Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryBlue.copy(alpha = 0.15f), Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    } else {
        Modifier.fillMaxWidth()
    }
    
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) PrimaryBlue else TextSecondary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}
