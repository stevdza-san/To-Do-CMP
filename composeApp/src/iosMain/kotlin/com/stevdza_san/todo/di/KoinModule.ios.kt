package com.stevdza_san.todo.di

import com.stevdza_san.todo.data.DatabaseDriverFactory
import com.stevdza_san.todo.data.IosDatabaseDriverFactory
import org.koin.dsl.module

actual val targetModule = module {
    single<DatabaseDriverFactory> { IosDatabaseDriverFactory() }
}