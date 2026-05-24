package com.example.cleancityapp.di

import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.TokenAuthenticator
import com.example.cleancityapp.data.remote.ComplaintDetailsApi
import com.example.cleancityapp.data.remote.DeviceRegistrationApi
import com.example.cleancityapp.data.repository.ComplaintDetailsRepository
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import com.example.cleancityapp.notification.NotificationHelper
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.auth.AuthViewModel
import com.example.cleancityapp.presentation.history.HistoryViewModel
import com.example.cleancityapp.presentation.user.UserViewModel
import com.example.cleancityapp.presentation.history.ComplaintDetailsViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
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
    // Retrofit (Existing)
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { TokenAuthenticator(androidContext()) }

    single(named("AuthClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    single(named("AuthRetrofit")) {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080/")
            .client(get(named("AuthClient")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<AuthApi>(named("AuthService")) {
        get<Retrofit>(named("AuthRetrofit")).create(AuthApi::class.java)
    }

    single {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AuthApi::class.java) }

    // Ktor (New)
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    // Services/APIs
    single { DeviceRegistrationApi(get()) }
    single { ComplaintDetailsApi(get()) }
    single { NotificationHelper(androidContext()) }

    // Repositories
    single { DeviceRegistrationRepository(get(), androidContext()) }
    single { ComplaintDetailsRepository(get()) }

    // ViewModels
    viewModel { MainViewModel(get(), get(), androidContext()) }
    viewModel { DriverViewModel(get(), androidContext()) }
    viewModel { AuthViewModel(get(), androidContext()) }
    viewModel { HistoryViewModel(get(), androidContext()) }
    viewModel { UserViewModel(get(), androidContext()) }
    viewModel { ComplaintDetailsViewModel(get(), androidContext()) }
}
