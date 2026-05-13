package com.example.cleancityapp.di

import com.example.cleancityapp.data.remote.ComplaintDetailsApi
import com.example.cleancityapp.data.remote.DeviceRegistrationApi
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    single { DeviceRegistrationApi(get()) }
    single { ComplaintDetailsApi(get()) }
}
