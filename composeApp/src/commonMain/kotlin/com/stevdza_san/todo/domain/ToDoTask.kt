package com.stevdza_san.todo.domain

import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ToDoTask(
    val id: String = Uuid.random().toHexString(),
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: Priority,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)

enum class Priority {
    None,
    Low,
    Medium,
    High
}