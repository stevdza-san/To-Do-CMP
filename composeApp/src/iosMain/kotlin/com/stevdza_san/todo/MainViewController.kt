package com.stevdza_san.todo

import androidx.compose.ui.window.ComposeUIViewController
import com.stevdza_san.todo.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }