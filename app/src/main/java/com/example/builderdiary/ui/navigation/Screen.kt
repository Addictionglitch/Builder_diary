package com.example.builderdiary.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String, val navArguments: List<NamedNavArgument> = emptyList()) {
    data object FocusTimer : Screen("focus_timer")
    data object InitializeProject : Screen("initialize_project")
    data object Dashboard : Screen("dashboard")

    // FIXED: Added projectId to the route
    data object SessionReceipt : Screen(
        route = "session_receipt/{xpEarned}/{duration}/{projectId}",
        navArguments = listOf(
            navArgument("xpEarned") { type = NavType.IntType; defaultValue = 0 },
            navArgument("duration") { type = NavType.LongType; defaultValue = 0L },
            navArgument("projectId") { type = NavType.LongType; defaultValue = 1L }
        )
    ) {
        fun createRoute(xpEarned: Int, duration: Long, projectId: Long) = 
            "session_receipt/$xpEarned/$duration/$projectId"
    }

    data object ProjectDetail : Screen(
        route = "project_detail/{projectId}",
        navArguments = listOf(
            navArgument("projectId") { type = NavType.StringType; defaultValue = "" }
        )
    ) {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
}
