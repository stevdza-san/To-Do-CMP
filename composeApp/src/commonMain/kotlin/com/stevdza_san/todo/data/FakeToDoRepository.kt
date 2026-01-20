package com.stevdza_san.todo.data

import androidx.compose.runtime.mutableStateListOf
import com.stevdza_san.todo.domain.Priority
import com.stevdza_san.todo.domain.ToDoTask
import com.stevdza_san.todo.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.coroutines.CoroutineContext

class FakeToDoRepository : ToDoRepository {
    private val tasks = mutableStateListOf<ToDoTask>()

    init {
        tasks.addAll(
            listOf(
                ToDoTask(
                    title = "Simple Task 1",
                    description = "Some random text.",
                    isCompleted = true,
                    priority = Priority.Low
                ),
                ToDoTask(
                    title = "Simple Task 2",
                    description = "Some random text.",
                    isCompleted = false,
                    priority = Priority.Medium
                ),
            )
        )
    }

    override fun createTask(task: ToDoTask): RequestState<Unit> {
        return try {
            val existingTask = tasks.find { it.id == task.id }
            if (existingTask != null) {
                RequestState.Error(message = "Task with ID: ${task.id} already exists.")
            } else {
                tasks.add(task)
                RequestState.Success(data = Unit)
            }
        } catch (e: Exception) {
            RequestState.Error(message = "Failed to create a task: ${e.message}")
        }
    }

    override fun updateTask(task: ToDoTask): RequestState<Unit> {
        return try {
            val index = tasks.indexOfFirst { it.id == task.id }
            if (index != -1) {
                tasks[index] = task
                RequestState.Success(data = Unit)
            } else {
                RequestState.Error(message = "Task with ID: ${task.id} not found.")
            }
        } catch (e: Exception) {
            RequestState.Error(message = "Failed to update a task: ${e.message}")
        }
    }

    override fun readSelectedTask(taskId: String): RequestState<ToDoTask> {
        return try {
            val existingTask = tasks.find { it.id == taskId }
            if (existingTask != null) {
                RequestState.Success(data = existingTask)
            } else {
                RequestState.Error(message = "Task with ID: $taskId not found.")
            }
        } catch (e: Exception) {
            RequestState.Error(message = "Failed to read a selected task: ${e.message}")
        }
    }

    override fun readAllTasks(context: CoroutineContext): Flow<RequestState<List<ToDoTask>>> {
        return try {
            flowOf(RequestState.Success(data = tasks))
        } catch (e: Exception) {
            flowOf(RequestState.Error(message = "Failed to read all tasks: ${e.message}"))
        }
    }

    override fun removeTask(taskId: String): RequestState<Unit> {
        return try {
            val taskToRemove = tasks.find { it.id == taskId }
            if (taskToRemove != null) {
                tasks.remove(element = taskToRemove)
                RequestState.Success(data = Unit)
            } else {
                RequestState.Error(message = "Task with ID: $taskId not found.")
            }
        } catch (e: Exception) {
            RequestState.Error(message = "Failed to remove the task: ${e.message}")
        }
    }
}