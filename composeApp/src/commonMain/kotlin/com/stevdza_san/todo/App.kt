package com.stevdza_san.todo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.stevdza_san.todo.navigation.NavGraph
import com.stevdza_san.todo.util.darkScheme
import com.stevdza_san.todo.util.lightScheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val colorScheme = if (isSystemInDarkTheme()) darkScheme else lightScheme

    MaterialTheme(colorScheme) {
        NavGraph()
    }
}