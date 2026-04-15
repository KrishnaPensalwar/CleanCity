package com.example.cleancityapp.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("auth/signup")
    suspend fun signUp(@Body request: Map<String, String>): Response<SignUpResponse>

    @POST("auth/login")
    suspend fun login(@Body request: Map<String, String>): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<UserDto>

    @GET("api/cities")
    suspend fun getCities(): Response<List<CityDto>>

    @Multipart
    @POST("api/reports")
    suspend fun submitReport(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("description") description: RequestBody
    ): Response<ReportResponse>

    @GET("api/reports/me")
    suspend fun getMeReports(@Header("Authorization") token: String): Response<List<ReportResponse>>
}
