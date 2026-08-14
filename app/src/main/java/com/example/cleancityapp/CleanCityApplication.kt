package com.example.cleancityapp

import android.app.Application
import com.example.cleancityapp.di.appModule
import com.example.cleancityapp.security.DevToolBridge
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CleanCityApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Avoid verbose DI logging in release builds.
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@CleanCityApplication)
            modules(appModule)
        }

        // No-op in release; initializes debug-only SDK in debug builds.
        DevToolBridge.init(this)
    }
}
