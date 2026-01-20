package com.stevdza_san.todo.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator {
    val backStack: SnapshotStateList<Screen> = mutableStateListOf(Screen.Home)

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateToTask(taskId: String? = null) {
        if (backStack.lastOrNull() is Screen.Task) {
            backStack[backStack.lastIndex] = Screen.Task(id = taskId)
        } else {
            backStack.add(Screen.Task(id = taskId))
        }
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}