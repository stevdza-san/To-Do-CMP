package com.stevdza_san.todo.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object Home : Screen()
    @Serializable
    data class Task(
        val id: String? = null,
    ) : Screen()
}