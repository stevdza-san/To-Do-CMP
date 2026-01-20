package com.stevdza_san.todo.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdza_san.todo.domain.Priority
import com.stevdza_san.todo.domain.ToDoRepository
import com.stevdza_san.todo.domain.ToDoTask
import com.stevdza_san.todo.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val repository: ToDoRepository,
) : ViewModel() {
    private var _priorityFilter = MutableStateFlow(Priority.None)
    val priorityFilter: StateFlow<Priority> = _priorityFilter

    private var _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allTasks = combine(
        repository.readAllTasks(context = viewModelScope.coroutineContext),
        _priorityFilter,
        _searchQuery
    ) { tasks, priority, query ->
        when (tasks) {
            is RequestState.Success -> {
                val filteredTasks = tasks.data
                    .let { list ->
                        if (priority == Priority.None) list
                        else list.filter { it.priority == priority }
                    }
                    .let { list ->
                        if (query.isBlank()) list
                        else list.filter {
                            it.title.lowercase().contains(query, ignoreCase = false) ||
                                    it.description.lowercase().contains(query, ignoreCase = false)
                        }
                    }
                    .sortedByDescending { it.priority.ordinal }
                RequestState.Success(data = filteredTasks)
            }

            else -> tasks
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    fun markTaskAsCompleted(task: ToDoTask): RequestState<Unit> {
        return repository.updateTask(task)
    }

    fun removeTask(taskId: String): RequestState<Unit> {
        return repository.removeTask(taskId)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updatePriorityFilter(priority: Priority) {
        _priorityFilter.value = priority
    }
}