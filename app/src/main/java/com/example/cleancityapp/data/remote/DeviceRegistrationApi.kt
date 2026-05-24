package com.example.cleancityapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRegistrationRequest(
    val deviceId: String,
    val fcmToken: String,
    val deviceName: String,
    val platform: String
)

@Serializable
data class DeviceRegistrationResponse(
    val message: String,
    val deviceId: String? = null,
    val fcmToken: String? = null,
    val deviceName: String? = null
)

class DeviceRegistrationApi(private val client: HttpClient) {
    private val baseUrl = "http://127.0.0.1:8080/api"
    suspend fun registerDevice(token: String, request: DeviceRegistrationRequest): DeviceRegistrationResponse {
        return client.post("$baseUrl/devices/register") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteDevice(token: String, deviceId: String): HttpResponse {
        return client.delete("$baseUrl/devices/$deviceId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
