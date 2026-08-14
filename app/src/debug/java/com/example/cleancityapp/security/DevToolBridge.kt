package com.example.cleancityapp.security

import android.app.Application
import io.github.krishnapensalwar.devkit.DevTool
import io.github.krishnapensalwar.devkit.DevToolPlugin
import io.github.krishnapensalwar.devkit.DevtoolConfig
import io.github.krishnapensalwar.devkit.network.interceptor.DevToolNetworkInterceptor
import io.ktor.client.HttpClientConfig
import okhttp3.OkHttpClient

/**
 * Debug-only DevTool wiring. Release counterpart is a no-op so the SDK
 * (debugImplementation) is never required on the release classpath.
 */
object DevToolBridge {
    fun init(application: Application) {
        DevTool.init(
            context = application,
            config = DevtoolConfig(
                isFloatingButtonEnabled = true,
                sensitiveHeaders = setOf("Authorization", "Cookie", "X-Api-Key")
            )
        )
    }

    fun configureOkHttp(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        return builder.addInterceptor(DevToolNetworkInterceptor())
    }

    fun configureKtor(clientConfig: HttpClientConfig<*>) {
        clientConfig.install(DevToolPlugin) {
            mockingEnabled = false
        }
    }
}
