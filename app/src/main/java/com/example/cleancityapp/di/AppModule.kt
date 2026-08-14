package com.example.cleancityapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.cleancityapp.BuildConfig
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.ComplaintDetailsApi
import com.example.cleancityapp.data.remote.DeviceRegistrationApi
import com.example.cleancityapp.data.remote.DriverApi
import com.example.cleancityapp.data.remote.TokenAuthenticator
import com.example.cleancityapp.data.repository.ComplaintDetailsRepository
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import com.example.cleancityapp.notification.NotificationHelper
import com.example.cleancityapp.presentation.auth.AuthViewModel
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.history.ComplaintDetailsViewModel
import com.example.cleancityapp.presentation.history.HistoryViewModel
import com.example.cleancityapp.presentation.home.HomeViewModel
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.profile.ProfileViewModel
import com.example.cleancityapp.presentation.rewards.RewardsViewModel
import com.example.cleancityapp.presentation.user.UserViewModel
import com.example.cleancityapp.security.DevToolBridge
import com.example.cleancityapp.util.ApiConstants
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        HttpLoggingInterceptor().apply {
            // Never log bodies in release — login/password/token leakage via logcat.
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
            redactHeader("Cookie")
        }
    }

    single { TokenAuthenticator(get()) }

    single(named("AuthClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .let { DevToolBridge.configureOkHttp(it) }
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .let { DevToolBridge.configureOkHttp(it) }
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    single(named("AuthRetrofit")) {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(get(named("AuthClient")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<AuthApi>(named("AuthService")) {
        get<Retrofit>(named("AuthRetrofit")).create(AuthApi::class.java)
    }

    single {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AuthApi::class.java) }

    single {
        HttpClient(Android) {
            DevToolBridge.configureKtor(this)

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = BuildConfig.DEBUG
                        isLenient = true
                        encodeDefaults = true
                    }
                )
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    // HEADERS avoids dumping passwords/tokens from JSON bodies.
                    level = LogLevel.HEADERS
                }
            }
        }
    }

    single { DeviceRegistrationApi(get()) }
    single { ComplaintDetailsApi(get()) }
    single { DriverApi(get()) }
    single { NotificationHelper(androidContext()) }

    // Encrypted session storage — fail closed (no plaintext SharedPreferences fallback).
    single<SharedPreferences> {
        val context = androidContext()
        val masterKey = androidx.security.crypto.MasterKey.Builder(context)
            .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
            .build()
        androidx.security.crypto.EncryptedSharedPreferences.create(
            context,
            "auth_prefs_encrypted",
            masterKey,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ).also { encrypted ->
            val legacy = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            if (legacy.all.isNotEmpty() && encrypted.all.isEmpty()) {
                encrypted.edit().apply {
                    legacy.all.forEach { (key, value) ->
                        when (value) {
                            is String -> putString(key, value)
                            is Boolean -> putBoolean(key, value)
                            is Int -> putInt(key, value)
                            is Long -> putLong(key, value)
                            is Float -> putFloat(key, value)
                        }
                    }
                    apply()
                }
                legacy.edit().clear().apply()
            }
        }
    }

    single { DeviceRegistrationRepository(get(), get(), androidContext()) }
    single { ComplaintDetailsRepository(get()) }

    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { DriverViewModel(get(), get(), get(), androidContext()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { HistoryViewModel(get(), get(), get()) }
    viewModel { UserViewModel(get(), get(), androidContext()) }
    viewModel { ComplaintDetailsViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { RewardsViewModel(get(), get()) }
}
