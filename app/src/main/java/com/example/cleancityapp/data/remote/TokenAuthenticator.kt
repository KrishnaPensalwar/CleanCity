package com.example.cleancityapp.data.remote

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TokenAuthenticator(context: Context) : Authenticator, KoinComponent {
    private val authApi: AuthApi by inject()
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = sharedPreferences.getString("refresh_token", null) ?: return null

        synchronized(this) {
            val newTokenResponse = authApi.refresh(mapOf("refreshToken" to refreshToken)).execute()

            if (newTokenResponse.isSuccessful && newTokenResponse.body() != null) {
                val loginResponse = newTokenResponse.body()!!
                sharedPreferences.edit()
                    .putString("access_token", loginResponse.accessToken)
                    .putString("refresh_token", loginResponse.refreshToken)
                    .apply()

                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${loginResponse.accessToken}")
                    .build()
            }
        }
        return null
    }
}
