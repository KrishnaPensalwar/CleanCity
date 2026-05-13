package com.example.cleancityapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ComplaintDetailsDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val category: String,
    val imageUrls: List<String>,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val createdAt: Long,
    val updatedAt: Long,
    val adminRemarks: String? = null,
    val resolutionDetails: String? = null,
    val assignedWorkerName: String? = null
)

class ComplaintDetailsApi(private val client: HttpClient) {
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getComplaintDetails(token: String, complaintId: String): ComplaintDetailsDto {
        return client.get("$baseUrl/complaints/$complaintId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
}
