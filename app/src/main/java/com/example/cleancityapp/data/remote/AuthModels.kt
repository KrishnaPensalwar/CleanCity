package com.example.cleancityapp.data.remote

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    val isSuccess: Boolean,
    val message: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val rewardPoints: Int,
    val isVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class CityDto(
    val id: String,
    val name: String
)

data class ReportResponse(
    val id: String,
    val userId: String,
    val description: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val timestamp: Long,
    val createdAt: String
)
