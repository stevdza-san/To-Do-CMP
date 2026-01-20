package com.stevdza_san.todo.di

import com.stevdza_san.todo.data.AndroidDatabaseDriverFactory
import com.stevdza_san.todo.data.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val targetModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
}