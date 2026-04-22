package com.example.cleancityapp.presentation.user

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.ReportResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

data class UserState(
    val reports: List<ReportResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isReportSuccess: Boolean = false
)

class UserViewModel(
    private val authApi: AuthApi,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun fetchUserReports() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = authApi.getMeReports("Bearer $token")
                if (response.isSuccessful) {
                    _state.update { it.copy(reports = response.body() ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to fetch reports") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun submitReport(imageUri: Uri, description: String, lat: Double, lon: Double, userId: String) {
        val token = sharedPreferences.getString("access_token", null) ?: return
        val timestamp = System.currentTimeMillis().toString()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val mimeType = context.contentResolver.getType(imageUri)
                val file = getFileFromUri(imageUri)
                val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val timestampBody = timestamp.toRequestBody("text/plain".toMediaTypeOrNull())
                val latBody = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val lonBody = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = authApi.submitReport(
                    "Bearer $token",
                    body,
                    userIdBody,
                    timestampBody,
                    latBody,
                    lonBody,
                    descBody
                )

                if (response.isSuccessful) {
                    _state.update { it.copy(isLoading = false, isReportSuccess = true) }
                    fetchUserReports()
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to submit report: ${response.message()}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
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

    fun resetReportStatus() {
        _state.update { it.copy(isReportSuccess = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
