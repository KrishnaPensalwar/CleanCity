package com.example.cleancityapp.data.remote

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class TokenAuthenticator(context: Context) : Authenticator, KoinComponent {
    // ✅ Fix: Inject the AuthService that doesn't have an authenticator to avoid circular dependency
    private val authApi: AuthApi by inject(named("AuthService"))
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun authenticate(route: Route?, response: Response): Request? {
        // If we've already tried to refresh 3 times for this request, stop.
        if (response.responseCount >= 3) {
            return null
        }

        val refreshToken = sharedPreferences.getString("refresh_token", null) ?: return null

        synchronized(this) {
            // Check if token was already refreshed by another thread
            val currentToken = sharedPreferences.getString("access_token", null)
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")
            
            if (currentToken != requestToken && currentToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Perform synchronous refresh call
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
