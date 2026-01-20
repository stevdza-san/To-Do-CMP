package com.stevdza_san.todo.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.stevdza_san.TaskDatabase
import com.stevdza_san.todo.domain.Priority
import com.stevdza_san.todo.domain.ToDoRepository
import com.stevdza_san.todo.domain.ToDoTask
import com.stevdza_san.todo.util.RequestState
import com.stevdzasan.TaskTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock

class ToDoRepositoryImpl(
    private val databaseDriverFactory: DatabaseDriverFactory,
) : ToDoRepository {
    private val database = TaskDatabase(
        databaseDriverFactory.createDriver()
    )
    private val query = database.taskDatabaseQueries

    override fun createTask(task: ToDoTask): RequestState<Unit> {
        return try {
            query.insertTask(
                id = task.id,
                title = task.title,
                description = task.description,
                isCompleted = if (task.isCompleted) 1 else 0,
                priority = task.priority.name,
                created_at = Clock.System.now().toEpochMilliseconds(),
                updated_at = Clock.System.now().toEpochMilliseconds()
            )

            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error(message = "${e.message}")
        }
    }

    override fun updateTask(task: ToDoTask): RequestState<Unit> {
        return try {
            query.updateTask(
                id = task.id,
                title = task.title,
                description = task.description,
                isCompleted = if (task.isCompleted) 1 else 0,
                priority = task.priority.name,
                updated_at = Clock.System.now().toEpochMilliseconds()
            )

            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error(message = "${e.message}")
        }
    }

    override fun readSelectedTask(taskId: String): RequestState<ToDoTask> {
        return try {
            val task = query.selectTaskById(taskId)
                .executeAsOneOrNull()
            task?.let {
                RequestState.Success(data = task.convert())
            } ?: RequestState.Error(message = "Task not found.")

        } catch (e: Exception) {
            RequestState.Error(message = "${e.message}")
        }
    }

    override fun readAllTasks(context: CoroutineContext): Flow<RequestState<List<ToDoTask>>> {
        return query.selectAllTasks()
            .asFlow()
            .catch {
                RequestState.Error(message = "${it.message}")
            }
            .mapToList(context)
            .map { taskTables ->
                RequestState.Success(data = taskTables.map { it.convert() })
            }
    }

    override fun removeTask(taskId: String): RequestState<Unit> {
        return try {
            query.deleteTaskById(taskId)
            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error(message = "${e.message}")
        }
    }

    fun TaskTable.convert(): ToDoTask {
        return ToDoTask(
            id = this.id,
            title = this.title,
            description = this.description,
            isCompleted = this.isCompleted == 1L,
            priority = Priority.valueOf(value = this.priority),
            createdAt = this.created_at,
            updatedAt = this.updated_at,
        )
    }
}