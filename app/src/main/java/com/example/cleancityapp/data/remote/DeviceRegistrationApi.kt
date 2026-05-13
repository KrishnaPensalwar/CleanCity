package com.example.cleancityapp.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRegistrationRequest(
    val deviceId: String,
    val fcmToken: String,
    val deviceName: String,
    val platform: String = "ANDROID"
)

class DeviceRegistrationApi(private val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080" // Consistent with existing Retrofit config

    suspend fun registerDevice(token: String, request: DeviceRegistrationRequest): HttpResponse {
        return client.post("$baseUrl/devices/register") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteDevice(token: String, deviceId: String): HttpResponse {
        return client.delete("$baseUrl/devices/$deviceId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
