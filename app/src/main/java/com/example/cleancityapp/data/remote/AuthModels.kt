package com.example.cleancityapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val isSuccess: Boolean,
    val message: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val roles: List<String>,
    val account: AccountDto,
    val profile: UserDto
)

@Serializable
data class MeResponse(
    val account: AccountDto,
    val roles: List<String>,
    val userProfile: UserDto?,
    val driverProfile: UserDto? = null
)

@Serializable
data class AccountDto(
    val id: String,
    val email: String,
    val phone: String?,
    val status: String
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val roles: List<String> = emptyList(),
    val role: String? = null,
    val rewardPoints: Int,
    val isVerified: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val reportsFiled: Int,
    val reportsResolved: Int,
    val address: String? = null,
    val profileImage: String? = null
)

@Serializable
data class CityDto(
    val id: String,
    val name: String
)

@Serializable
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

@Serializable
data class RankResponse(
    val currentUser: UserRankDto,
    val topUsers: List<UserRankDto>
)

@Serializable
data class UserRankDto(
    val name: String,
    val rank: Int,
    val rewardPoints: Int
)
