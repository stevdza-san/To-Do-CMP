package com.stevdza_san.todo.presentation.screen.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stevdza_san.todo.domain.Priority
import com.stevdza_san.todo.presentation.component.PriorityChip
import com.stevdza_san.todo.presentation.component.PriorityChipSize
import com.stevdza_san.todo.util.Alpha
import com.stevdza_san.todo.util.Resource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    id: String?,
    navigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<TaskViewModel>()
    val uiState by viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadData(taskId = id)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BoxWithConstraints {
                val isDualPane = maxWidth >= maxHeight
                if (!isDualPane) {
                    TopAppBar(
                        title = { Text(text = "Task") },
                        navigationIcon = {
                            IconButton(onClick = navigateBack) {
                                Icon(
                                    painter = painterResource(Resource.Icon.BACK_ARROW),
                                    contentDescription = "Back arrow icon"
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TaskInputSection(
                    title = "Task Title",
                    value = uiState.title,
                    onValueChange = viewModel::updateTitle,
                    placeholder = "Enter task title...",
                    isRequired = true
                )

                TaskInputSection(
                    title = "Task Description",
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    placeholder = "Enter task description...",
                    minLines = 3,
                    maxLines = 6
                )

                PrioritySection(
                    selectedPriority = uiState.priority,
                    onPrioritySelected = viewModel::updatePriority
                )
            }

            Box(modifier = Modifier.padding(all = 16.dp)) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = {
                        viewModel.saveTask(
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (id != null) "Task updated"
                                        else "New Task created"
                                    )
                                }
                            },
                            onError = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        )
                    }
                ) {
                    Text(
                        text = if (id != null) "Update Task" else "Create Task"
                    )
                }
            }
        }
    }
}

@Composable
fun TaskInputSection(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isRequired: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (isRequired) {
                Text(
                    text = " *",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.HALF)
                )
            },
            shape = RoundedCornerShape(size = 12.dp),
            minLines = minLines,
            maxLines = maxLines
        )
    }
}

@Composable
private fun PrioritySection(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Priority",
            style = MaterialTheme.typography.titleMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Priority.entries.drop(1).forEach { priority ->
                PriorityChip(
                    priority = priority,
                    size = PriorityChipSize.Large,
                    isSelected = priority == selectedPriority,
                    onSelect = onPrioritySelected
                )
            }
        }
    }
}