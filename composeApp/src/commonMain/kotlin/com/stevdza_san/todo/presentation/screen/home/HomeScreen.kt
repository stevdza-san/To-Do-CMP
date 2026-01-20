package com.stevdza_san.todo.presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevdza_san.todo.domain.Priority
import com.stevdza_san.todo.presentation.component.InfoCard
import com.stevdza_san.todo.presentation.component.LoadingCard
import com.stevdza_san.todo.presentation.component.PriorityColors.getColor
import com.stevdza_san.todo.presentation.component.TaskCard
import com.stevdza_san.todo.util.DisplayResult
import com.stevdza_san.todo.util.Resource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToTask: (String?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val viewModel = koinViewModel<HomeViewModel>()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()

    var dropdownMenuOpened by remember { mutableStateOf(false) }
    var searchBarOpened by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = searchBarOpened
                    ) { isOpened ->
                        if (isOpened) {
                            TextField(
                                modifier = Modifier.height(56.dp),
                                value = searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                placeholder = { Text(text = "Search...") },
                                shape = RoundedCornerShape(size = 99.dp),
                                textStyle = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )
                        } else {
                            Text(text = "To Do")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(Resource.Icon.HAMBURGER_MENU),
                            contentDescription = "Hamburger menu icon"
                        )
                    }
                },
                actions = {
                    AnimatedContent(
                        targetState = searchBarOpened
                    ) { isOpened ->
                        if (isOpened) {
                            IconButton(
                                onClick = {
                                    searchBarOpened = false
                                    viewModel.updateSearchQuery(query = "")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Resource.Icon.CLOSE),
                                    contentDescription = "Close icon"
                                )
                            }
                        } else {
                            Row {
                                Box {
                                    Box(
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        IconButton(onClick = { dropdownMenuOpened = true }) {
                                            Icon(
                                                painter = painterResource(Resource.Icon.SORT),
                                                contentDescription = "Sort icon"
                                            )
                                        }
                                        if (priorityFilter != Priority.None) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .offset(x = (-6).dp, y = 6.dp)
                                                    .clip(CircleShape)
                                                    .background(color = MaterialTheme.colorScheme.error)
                                            ) {}
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = dropdownMenuOpened,
                                        shape = RoundedCornerShape(size = 12.dp),
                                        onDismissRequest = { dropdownMenuOpened = false },
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Priority.entries.forEach { priority ->
                                            DropdownMenuItem(
                                                modifier = Modifier
                                                    .background(
                                                        if (priorityFilter == priority && priority != Priority.None) MaterialTheme.colorScheme.outlineVariant
                                                        else MaterialTheme.colorScheme.surfaceVariant
                                                    ),
                                                text = { Text(text = if (priority.name == "None") "All" else priority.name) },
                                                leadingIcon = {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(color = priority.getColor())
                                                    ) {}
                                                },
                                                onClick = {
                                                    dropdownMenuOpened = false
                                                    viewModel.updatePriorityFilter(priority)
                                                }
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { searchBarOpened = true }) {
                                    Icon(
                                        painter = painterResource(Resource.Icon.SEARCH),
                                        contentDescription = "Search icon"
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigateToTask(null) },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(Resource.Icon.ADD),
                    contentDescription = "Plus icon"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "New Task")
            }
        }
    ) { paddingValues ->
        allTasks.DisplayResult(
            modifier = Modifier.padding(paddingValues),
            onLoading = { LoadingCard() },
            onSuccess = { tasks ->
                if (tasks.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(all = 12.dp)
                    ) {
                        items(
                            items = tasks,
                            key = { it.id }
                        ) {
                            TaskCard(
                                task = it,
                                onClick = navigateToTask,
                                onComplete = {
                                    val isCompleted = !it.isCompleted
                                    val result = viewModel.markTaskAsCompleted(
                                        task = it.copy(isCompleted = isCompleted)
                                    )

                                    if (result.isSuccess()) {
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = if (isCompleted) "Task marked as Completed"
                                                else "Task marked as Not Completed"
                                            )
                                        }
                                    }
                                },
                                onDelete = {
                                    val result = viewModel.removeTask(taskId = it.id)

                                    if (result.isSuccess()) {
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message = "Task removed successfully"
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                } else {
                    InfoCard(message = "Empty Tasks.")
                }
            },
            onError = { message ->
                InfoCard(
                    message = message,
                    lightModeIcon = Resource.Image.WARNING_LIGHT,
                    darkModeIcon = Resource.Image.WARNING_DARK
                )
            },
            transitionSpec = slideInVertically() + fadeIn() togetherWith
                    slideOutVertically() + fadeOut()
        )
    }
}