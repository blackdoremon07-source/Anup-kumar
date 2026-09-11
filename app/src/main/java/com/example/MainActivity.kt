package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.admin.ui.AdminScreen
import com.example.admin.ui.AdminViewModel
import com.example.auth.ui.AuthViewModel
import com.example.auth.ui.LoginScreen
import com.example.calculator.ui.CalculatorScreen
import com.example.jobphoto.ui.JobPhotoSignatureScreen
import com.example.model.JobCategory
import com.example.navigation.Screen
import com.example.pdftools.ui.PdfToolsScreen
import com.example.qr.ui.QrToolsScreen
import com.example.ui.components.DgBottomNavigation
import com.example.ui.components.DgHeader
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JobsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.screens.photoeditor.PhotoEditorScreen
import com.example.unitconverter.ui.UnitConverterScreen
import com.example.ui.theme.DgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DgTheme {
                DgAppNavigation()
            }
        }
    }
}

@Composable
fun DgAppNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route

    val authViewModel: AuthViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Shared state for deep-linking between Home quick actions and Jobs/Updates
    var activeJobCategory by remember { mutableStateOf<JobCategory?>(null) }
    var activeUpdateTabIndex by remember { mutableIntStateOf(0) }

    // List of main root destinations where the global Header and BottomNavigation should be visible
    val mainNavigationRoutes = setOf(
        Screen.Home.route,
        Screen.Jobs.route,
        Screen.Tools.route,
        Screen.Updates.route,
        Screen.Profile.route
    )

    val showMainBars = currentRoute in mainNavigationRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (showMainBars) {
                DgHeader(
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showMainBars) {
                DgBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        if (route == Screen.Jobs.route) {
                            activeJobCategory = JobCategory.ALL
                        }
                        navController.navigate(route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = if (showMainBars) Modifier.padding(innerPadding) else Modifier
        ) {
            // 1. SPLASH SCREEN
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. HOME SCREEN
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToJobs = { category ->
                        activeJobCategory = category ?: JobCategory.ALL
                        navController.navigate(Screen.Jobs.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTools = {
                        navController.navigate(Screen.Tools.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToToolDetail = { route ->
                        navController.navigate(route)
                    },
                    onNavigateToUpdates = { tabIndex ->
                        activeUpdateTabIndex = tabIndex
                        navController.navigate(Screen.Updates.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            // 3. JOBS SCREEN
            composable(Screen.Jobs.route) {
                JobsScreen(
                    initialCategory = activeJobCategory
                )
            }

            // 4. TOOLS SCREEN
            composable(Screen.Tools.route) {
                ToolsScreen(
                    onNavigateToTool = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 5. UPDATES SCREEN
            composable(Screen.Updates.route) {
                UpdatesScreen(
                    initialTabIndex = activeUpdateTabIndex
                )
            }

            // 6. PROFILE SCREEN
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    },
                    onNavigateToAdmin = {
                        navController.navigate(Screen.Admin.route)
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            // 7. LOGIN SCREEN (/login)
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLoginSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // 8. PROTECTED ADMIN ROUTE (/admin)
            composable(Screen.Admin.route) {
                AdminScreen(
                    currentUser = currentUser,
                    adminViewModel = adminViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onToggleTestAdminRole = { isAdmin ->
                        authViewModel.toggleOwnerAdminTestRole(isAdmin)
                    }
                )
            }

            // 8. GLOBAL SEARCH SCREEN (/search)
            composable(Screen.Search.route) {
                SearchScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToTool = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // TOOL SUB-ROUTES (PART 1 PLACEHOLDERS WITH FUTURE ACTIVATION NOTICE)
            composable(Screen.ToolPhotoEditor.route) {
                PhotoEditorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ToolJobPhotoSignature.route) {
                JobPhotoSignatureScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ToolPdf.route) {
                PdfToolsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ToolQr.route) {
                QrToolsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ToolCalculator.route) {
                CalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ToolUnitConverter.route) {
                UnitConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
