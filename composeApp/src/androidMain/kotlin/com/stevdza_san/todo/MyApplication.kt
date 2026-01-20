package com.stevdza_san.todo

import android.app.Application
import com.stevdza_san.todo.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            config = { androidContext(this@MyApplication) }
        )
    }
}