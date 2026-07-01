package com.example.cleancityapp.data.remote

import io.ktor.client.*
import com.example.cleancityapp.util.ApiConstants
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class DriverApi(private val client: HttpClient) {
    private val baseUrl = ApiConstants.BASE_URL.removeSuffix("/") + "/api"

    suspend fun getAssignedReports(token: String): List<ReportResponse> {
        return client.get("$baseUrl/driver/reports/assigned") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
}
