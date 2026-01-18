package com.example.builderdiary.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

sealed class Screen(val route: String, val navArguments: List<NamedNavArgument> = emptyList()) {
    data object FocusTimer : Screen("focus_timer")
    data object InitializeProject : Screen("initialize_project")
    data object Dashboard : Screen("dashboard")

    data object SessionReceipt : Screen(
        route = "session_receipt/{xpEarned}/{duration}",
        navArguments = listOf(
            navArgument("xpEarned") { defaultValue = 0 },
            navArgument("duration") { defaultValue = 0L }
        )
    ) {
        fun createRoute(xpEarned: Int, duration: Long) = "session_receipt/$xpEarned/$duration"
    }

    data object ProjectDetail : Screen(
        route = "project_detail/{projectId}",
        navArguments = listOf(
            navArgument("projectId") { defaultValue = "" }
        )
    ) {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
}