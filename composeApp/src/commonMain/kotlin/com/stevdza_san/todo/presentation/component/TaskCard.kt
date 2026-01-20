package com.stevdza_san.todo.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.stevdza_san.swipeable.Swipeable
import com.stevdza_san.swipeable.domain.ActionAnimationConfig
import com.stevdza_san.swipeable.domain.ActionCustomization
import com.stevdza_san.swipeable.domain.SwipeAction
import com.stevdza_san.swipeable.domain.SwipeBackground
import com.stevdza_san.swipeable.domain.SwipeBehavior
import com.stevdza_san.swipeable.domain.SwipeDirection
import com.stevdza_san.todo.domain.Priority
import com.stevdza_san.todo.domain.ToDoTask
import com.stevdza_san.todo.util.Alpha
import com.stevdza_san.todo.util.Resource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: ToDoTask,
    onClick: (String) -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
) {
    var progressState by remember { mutableStateOf(0f) }
    var directionState by remember { mutableStateOf<SwipeDirection?>(null) }

    Swipeable(
        modifier = modifier,
        direction = SwipeDirection.BOTH,
        behavior = SwipeBehavior.DISMISS,
        threshold = 0.7f,
        leftDismissAction = SwipeAction(
            customization = ActionCustomization(
                icon = if (!task.isCompleted) Resource.Icon.CHECK_BOX
                else Resource.Icon.BLANK_BOX,
                iconSize = 24.dp,
                iconColor = if (!task.isCompleted) MaterialTheme.colorScheme.onTertiary
                else MaterialTheme.colorScheme.onSecondary,
                containerColor = Color.Transparent
            ),
            onAction = onComplete,
            label = "Complete action"
        ),
        rightDismissAction = SwipeAction(
            customization = ActionCustomization(
                icon = Resource.Icon.DELETE,
                iconSize = 24.dp,
                iconColor = MaterialTheme.colorScheme.onError,
                containerColor = Color.Transparent
            ),
            onAction = onDelete,
            label = "Delete action"
        ),
        shape = RoundedCornerShape(16.dp),
        actionAnimation = if (directionState == SwipeDirection.RIGHT) ActionAnimationConfig.SlideLeft
        else ActionAnimationConfig.SlideRight,
        onSwipeProgress = { progress, direction ->
            progressState = progress
            directionState = direction
        },
        leftBackground = if (!task.isCompleted) SwipeBackground.Solid(MaterialTheme.colorScheme.tertiary)
        else SwipeBackground.Solid(MaterialTheme.colorScheme.secondary),
        rightBackground = SwipeBackground.Solid(MaterialTheme.colorScheme.error),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick(task.id) },
            colors = CardDefaults.cardColors(
                containerColor = if (directionState == SwipeDirection.LEFT) {
                    lerp(
                        start = MaterialTheme.colorScheme.surfaceContainer,
                        stop = MaterialTheme.colorScheme.errorContainer,
                        fraction = progressState.coerceIn(0f, 1f)
                    )
                } else {
                    lerp(
                        start = MaterialTheme.colorScheme.surfaceContainer,
                        stop = if (task.isCompleted) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.tertiaryContainer,
                        fraction = progressState.coerceIn(0f, 1f)
                    )
                }
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier
                            .alpha(if (task.isCompleted) Alpha.HALF else Alpha.FULL),
                        text = task.title,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Medium,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough
                            else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (task.description.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .alpha(if (task.isCompleted) Alpha.HALF else Alpha.FULL),
                            text = task.description,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                PriorityChip(
                    priority = task.priority,
                    isCompleted = task.isCompleted
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskCardCompletedPreview() {
    TaskCard(
        task = ToDoTask(
            title = "Example title",
            description = "Some random text.",
            isCompleted = true,
            priority = Priority.Low
        ),
        onClick = {},
        onComplete = {},
        onDelete = {}
    )
}

@Preview
@Composable
fun TaskCardNotCompletedPreview() {
    TaskCard(
        task = ToDoTask(
            title = "Example title",
            description = "Some random text.",
            isCompleted = false,
            priority = Priority.Low
        ),
        onClick = {},
        onComplete = {},
        onDelete = {}
    )
}