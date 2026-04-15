package com.example.cleancityapp.presentation.main

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MainViewModel(
    private val authApi: AuthApi,
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainContract.State())
    val uiState: StateFlow<MainContract.State> = _uiState.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private var accessToken: String? = null

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        val savedToken = sharedPreferences.getString("access_token", null)
        val roleString = sharedPreferences.getString("user_role", null)
        if (!savedToken.isNullOrEmpty()) {
            accessToken = savedToken
            val role = if (roleString == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
            _uiState.update { it.copy(userRole = role) }
            getMe() // fetch user data
            navigateToDashboard()
        }
    }

    fun processIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.NavigateTo -> {
                _uiState.update { it.copy(currentScreen = intent.screen, error = null) }
            }
            is MainContract.Intent.SetRole -> {
                _uiState.update { it.copy(userRole = intent.role) }
            }
            is MainContract.Intent.Login -> {
                login(intent.email, intent.password)
            }
            is MainContract.Intent.SignUp -> {
                signUp(intent.name, intent.mobile, intent.email, intent.password, intent.city)
            }
            is MainContract.Intent.SubmitReport -> {
                submitReport(intent.imageUri, intent.description, intent.latitude, intent.longitude)
            }
            is MainContract.Intent.ResetReportStatus -> {
                _uiState.update { it.copy(isReportSuccess = false) }
            }
            is MainContract.Intent.FetchCities -> {
                fetchCities()
            }
            is MainContract.Intent.FetchUserReports -> {
                fetchUserReports()
            }
            is MainContract.Intent.GetMe -> {
                getMe()
            }
            is MainContract.Intent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
            is MainContract.Intent.LoginSuccess -> {
                navigateToDashboard()
            }
        }
    }

    private fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = retryApiCall(3) {
                authApi.login(mapOf("email" to email, "password" to pass))
            }

            result?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    accessToken = loginData.accessToken
                    
                    sharedPreferences.edit()
                        .putString("access_token", loginData.accessToken)
                        .putString("refresh_token", loginData.refreshToken)
                        .putString("user_role", loginData.user.role)
                        .apply()
                        
                    val role = if (loginData.user.role == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
                    _uiState.update { 
                        it.copy(
                            userRole = role, 
                            isLoading = false,
                            currentUser = loginData.user
                        ) 
                    }
                    navigateToDashboard()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Login failed: ${response.message()}") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed after retries") }
            }
        }
    }

    private fun fetchUserReports() {
        val token = accessToken ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = retryApiCall(3) {
                authApi.getMeReports("Bearer $token")
            }
            result?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(userReports = response.body()!!, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch history") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed") }
            }
        }
    }

    private fun submitReport(imageUri: Uri, description: String, lat: Double, lon: Double) {
        val token = accessToken ?: return
        val userId = _uiState.value.currentUser?.id ?: return
        val timestamp = System.currentTimeMillis().toString()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val mimeType = context.contentResolver.getType(imageUri)
                if (mimeType != "image/jpeg" && mimeType != "image/png") {
                    _uiState.update { it.copy(isLoading = false, error = "Only JPEG and PNG images are allowed") }
                    return@launch
                }

                val file = getFileFromUri(imageUri)
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val timestampBody = timestamp.toRequestBody("text/plain".toMediaTypeOrNull())
                val latBody = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val lonBody = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

                val result = retryApiCall(3) {
                    authApi.submitReport(
                        "Bearer $token",
                        body,
                        userIdBody,
                        timestampBody,
                        latBody,
                        lonBody,
                        descBody
                    )
                }

                if (result?.isSuccessful == true) {
                    _uiState.update { it.copy(isLoading = false, isReportSuccess = true) }
                    fetchUserReports() // Refresh history after successful submission
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to submit report: ${result?.message()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
        val file = File(context.cacheDir, "temp_report_image.$extension")
        FileOutputStream(file).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return file
    }

    private fun getMe() {
        val token = accessToken ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = retryApiCall(3) {
                authApi.getMe("Bearer $token")
            }
            
            result?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val role = if (user.role == "ROLE_DRIVER") UserRole.DRIVER else UserRole.USER
                    _uiState.update { 
                        it.copy(
                            currentUser = user,
                            userRole = role,
                            isLoading = false
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch profile") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed") }
            }
        }
    }

    private fun fetchCities() {
        if (_uiState.value.cities.isNotEmpty()) return

        viewModelScope.launch {
            val result = retryApiCall(3) {
                authApi.getCities()
            }
            result?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(cities = response.body()!!) }
                }
            }
        }
    }

    private fun signUp(name: String, mobile: String, email: String, pass: String, city: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = retryApiCall(3) {
                authApi.signUp(
                    mapOf(
                        "name" to name,
                        "mobile" to mobile,
                        "email" to email,
                        "password" to pass,
                        "city" to city
                    )
                )
            }

            result?.let { response ->
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.update { it.copy(isLoading = false, currentScreen = Screen.Login) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.body()?.message ?: "Sign up failed") }
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Connection failed after retries") }
            }
        }
    }

    private suspend fun <T> retryApiCall(
        times: Int,
        initialDelay: Long = 1000,
        block: suspend () -> Response<T>
    ): Response<T>? {
        var currentDelay = initialDelay
        repeat(times) { attempt ->
            try {
                val response = block()
                if (response.isSuccessful) return response
                if (response.code() in 400..499) return response
            } catch (e: Exception) {
                if (attempt == times - 1) return null
            }
            delay(currentDelay)
            currentDelay *= 2
        }
        return null
    }

    private fun navigateToDashboard() {
        val initialScreen = when (_uiState.value.userRole) {
            UserRole.USER -> Screen.Home
            UserRole.DRIVER -> Screen.DriverDashboard
        }
        _uiState.update { it.copy(currentScreen = initialScreen) }
    }
}
