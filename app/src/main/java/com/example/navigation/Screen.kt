package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Jobs : Screen("jobs")
    object Tools : Screen("tools")
    object Updates : Screen("updates")
    object Profile : Screen("profile")
    object Admin : Screen("admin")
    object Search : Screen("search")

    // Tool sub-routes
    object ToolPhotoEditor : Screen("tools/photo-editor")
    object ToolJobPhotoSignature : Screen("tools/job-photo-signature")
    object ToolPdf : Screen("tools/pdf")
    object ToolQr : Screen("tools/qr")
    object ToolCalculator : Screen("tools/calculator")
    object ToolUnitConverter : Screen("tools/unit-converter")
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_item_home"
    ),
    BottomNavItem(
        route = Screen.Jobs.route,
        label = "Jobs",
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.Work,
        testTag = "nav_item_jobs"
    ),
    BottomNavItem(
        route = Screen.Tools.route,
        label = "Tools",
        selectedIcon = Icons.Filled.Build,
        unselectedIcon = Icons.Outlined.Build,
        testTag = "nav_item_tools"
    ),
    BottomNavItem(
        route = Screen.Updates.route,
        label = "Updates",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        testTag = "nav_item_updates"
    ),
    BottomNavItem(
        route = Screen.Profile.route,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_item_profile"
    )
)
