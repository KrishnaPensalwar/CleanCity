package com.example.cleancityapp.data.remote

import android.content.SharedPreferences
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class TokenAuthenticator(
    private val sharedPreferences: SharedPreferences
) : Authenticator, KoinComponent {
    private val authApi: AuthApi by inject(named("AuthService"))

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.responseCount >= 3) {
            return null
        }

        val refreshToken = sharedPreferences.getString("refresh_token", null) ?: return null

        synchronized(this) {
            val currentToken = sharedPreferences.getString("access_token", null)
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

            if (currentToken != requestToken && currentToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val newTokenResponse = authApi.refresh(mapOf("refreshToken" to refreshToken)).execute()

            if (newTokenResponse.isSuccessful && newTokenResponse.body() != null) {
                val loginResponse = newTokenResponse.body()!!
                sharedPreferences.edit()
                    .putString("access_token", loginResponse.token)
                    .putString("refresh_token", loginResponse.refreshToken)
                    .apply()

                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${loginResponse.token}")
                    .build()
            }
        }
        return null
    }

    private val Response.responseCount: Int
        get() {
            var result = 1
            var prevResponse = priorResponse
            while (prevResponse != null) {
                result++
                prevResponse = prevResponse.priorResponse
            }
            return result
        }
}
