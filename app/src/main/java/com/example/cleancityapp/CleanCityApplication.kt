package com.example.cleancityapp

import android.app.Application
import com.example.cleancityapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CleanCityApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@CleanCityApplication)
            modules(appModule)
        }
    }
}
