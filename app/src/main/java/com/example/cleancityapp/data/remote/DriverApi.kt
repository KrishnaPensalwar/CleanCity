package com.example.cleancityapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class DriverApi(private val client: HttpClient) {
    private val baseUrl = "https://wheat-salvaging-underrate.ngrok-free.dev/api"

    suspend fun getAssignedReports(token: String): List<ReportResponse> {
        return client.get("$baseUrl/driver/reports/assigned") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
}
