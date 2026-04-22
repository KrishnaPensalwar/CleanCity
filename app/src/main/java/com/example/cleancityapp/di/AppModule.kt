package com.example.cleancityapp.di

import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.TokenAuthenticator
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.driver.DriverViewModel
import com.example.cleancityapp.presentation.auth.AuthViewModel
import com.example.cleancityapp.presentation.history.HistoryViewModel
import com.example.cleancityapp.presentation.user.UserViewModel
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
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { TokenAuthenticator(androidContext()) }

    // Separate client for Auth related tasks to avoid circular dependency
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

    // Separate Retrofit instance for Auth/Refresh
    single(named("AuthRetrofit")) {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(get(named("AuthClient")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // AuthApi for the Authenticator
    single<AuthApi>(named("AuthService")) {
        get<Retrofit>(named("AuthRetrofit")).create(AuthApi::class.java)
    }

    // Standard Retrofit for the rest of the app
    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AuthApi::class.java) }

    viewModel { MainViewModel(get(), androidContext()) }
    viewModel { DriverViewModel(get(), androidContext()) }
    viewModel { AuthViewModel(get(), androidContext()) }
    viewModel { HistoryViewModel(get(), androidContext()) }
    viewModel { UserViewModel(get(), androidContext()) }
}
