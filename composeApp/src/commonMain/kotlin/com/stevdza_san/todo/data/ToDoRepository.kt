package com.stevdza_san.todo.data

import com.stevdza_san.todo.domain.ToDoTask
import com.stevdza_san.todo.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface ToDoRepository {
    fun createTask(task: ToDoTask): RequestState<Unit>
    fun updateTask(task: ToDoTask): RequestState<Unit>
    fun readSelectedTask(taskId: String): RequestState<ToDoTask>
    fun readAllTasks(context: CoroutineContext): Flow<RequestState<List<ToDoTask>>>
    fun removeTask(taskId: String): RequestState<Unit>
}