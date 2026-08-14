package com.example.cleancityapp.security

import android.app.Application
import io.ktor.client.HttpClientConfig
import okhttp3.OkHttpClient

/**
 * Release no-op: DevTool must not ship in production builds.
 */
object DevToolBridge {
    fun init(application: Application) = Unit

    fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient.Builder = builder

    fun configureKtor(clientConfig: HttpClientConfig<*>) = Unit
}
