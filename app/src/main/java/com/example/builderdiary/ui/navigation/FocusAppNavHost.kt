package com.example.builderdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.builderdiary.ui.dashboard.DashboardScreen
import com.example.builderdiary.ui.project.InitializeProjectScreen
import com.example.builderdiary.ui.project.ProjectDetailScreen
import com.example.builderdiary.ui.receipt.SessionReceiptScreen
import com.example.builderdiary.ui.timer.FocusTimerScreen

@Composable
fun FocusAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.FocusTimer.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. FOCUS TIMER (Home)
        composable(Screen.FocusTimer.route) {
            FocusTimerScreen(
                onDashboardClicked = {
                    navController.navigate(Screen.Dashboard.route)
                },
                navigateToSessionReceipt = { xp, duration, projectId ->
                    navController.navigate(Screen.SessionReceipt.createRoute(xp, duration, projectId))
                }
            )
        }

        // 2. DASHBOARD (Grid)
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId.toString()))
                },
                onAddProjectClick = {
                    navController.navigate(Screen.InitializeProject.route)
                },
                onBack = {
                    // Swipe Down Logic: Returns to Timer
                    navController.popBackStack()
                }
            )
        }

        // 3. PROJECT DETAIL
        composable(
            route = Screen.ProjectDetail.route,
            arguments = Screen.ProjectDetail.navArguments
        ) {
            ProjectDetailScreen(
                onStartSession = { projectId ->
                    // Start session: Go to Timer (clearing intermediate screens if needed)
                    navController.navigate(Screen.FocusTimer.route) {
                        popUpTo(Screen.FocusTimer.route) { inclusive = true }
                    }
                },
                onBack = {
                    // Swipe Down Logic: Close Project Detail
                    navController.popBackStack()
                }
            )
        }

        // 4. INITIALIZE PROJECT
        composable(Screen.InitializeProject.route) {
            InitializeProjectScreen(
                onProjectCreated = {
                    navController.popBackStack()
                },
                onBack = {
                    // Swipe Down Logic: Cancel creation
                    navController.popBackStack()
                }
            )
        }

        // 5. SESSION RECEIPT
        composable(
            route = Screen.SessionReceipt.route,
            arguments = Screen.SessionReceipt.navArguments
        ) {
            SessionReceiptScreen(
                onComplete = {
                    // Pop back to the Timer screen (clearing the receipt from history)
                    navController.popBackStack(Screen.FocusTimer.route, inclusive = false)
                }
            )
        }
    }
}