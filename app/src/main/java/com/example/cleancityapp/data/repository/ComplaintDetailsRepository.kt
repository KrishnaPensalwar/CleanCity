package com.example.cleancityapp.data.repository

import com.example.cleancityapp.data.remote.ComplaintDetailsApi
import com.example.cleancityapp.data.remote.ComplaintDetailsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComplaintDetailsRepository(private val api: ComplaintDetailsApi) {
    suspend fun getComplaintDetails(token: String, complaintId: String): Result<ComplaintDetailsDto> = withContext(Dispatchers.IO) {
        try {
            val details = api.getComplaintDetails(token, complaintId)
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
