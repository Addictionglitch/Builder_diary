package com.example.builderdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.builderdiary.ui.dashboard.DashboardScreen
import com.example.builderdiary.ui.project.InitializeProjectScreen
import com.example.builderdiary.ui.project.ProjectDetailScreen
import com.example.builderdiary.ui.receipt.SessionReceiptScreen
import com.example.builderdiary.ui.timer.FocusTimerScreen

@Composable
fun FocusAppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.FocusTimer.route) {
        composable(Screen.FocusTimer.route) {
            FocusTimerScreen(
                onDashboardClicked = { navController.navigate(Screen.Dashboard.route) },
                navigateToProjectDetail = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId.toString()))
                }
            )
        }
        composable(Screen.InitializeProject.route) {
            InitializeProjectScreen()
        }
        composable(
            route = Screen.SessionReceipt.route,
            arguments = Screen.SessionReceipt.navArguments
        ) {
            SessionReceiptScreen()
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId.toString()))
                },
                onAddProjectClick = {
                    navController.navigate(Screen.InitializeProject.route)
                }
            )
        }
        composable(
            route = Screen.ProjectDetail.route,
            arguments = Screen.ProjectDetail.navArguments
        ) {
            ProjectDetailScreen(
                onStartSession = { projectId ->
                    navController.navigate(Screen.FocusTimer.route)
                }
            )
        }
    }
}