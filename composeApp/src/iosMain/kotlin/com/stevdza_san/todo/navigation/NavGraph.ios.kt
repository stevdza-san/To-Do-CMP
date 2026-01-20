package com.stevdza_san.todo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.stevdza_san.todo.presentation.screen.home.HomeScreen
import com.stevdza_san.todo.presentation.screen.task.TaskScreen

@Composable
actual fun NavGraph() {
    val navController = rememberNavController()
    println("IOS NavGraph")
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                navigateToTask = { taskId ->
                    navController.navigate(route = Screen.Task(taskId))
                }
            )
        }
        composable<Screen.Task> {
            TaskScreen(
                id = it.toRoute<Screen.Task>().id,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}