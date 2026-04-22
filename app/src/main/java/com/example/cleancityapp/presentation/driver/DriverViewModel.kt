package com.example.cleancityapp.presentation.driver

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
import java.io.File
import java.io.FileOutputStream

data class DriverState(
    val assignedReports: List<ReportResponse> = emptyList(),
    val selectedReport: ReportResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isActionSuccess: Boolean = false
)

class DriverViewModel(
    private val authApi: AuthApi,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(DriverState())
    val state: StateFlow<DriverState> = _state.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        fetchAssignedReports()
    }

    fun fetchAssignedReports() {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = authApi.getAssignedReports("Bearer $token")
                if (response.isSuccessful) {
                    _state.update { it.copy(assignedReports = response.body() ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to fetch tasks: ${response.message()}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun setSelectedReport(report: ReportResponse?) {
        _state.update { it.copy(selectedReport = report) }
    }

    fun uploadCompletionPhoto(reportId: String, imageUri: Uri) {
        val token = sharedPreferences.getString("access_token", null) ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
                val file = getFileFromUri(imageUri)
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = authApi.uploadCompletionPhoto("Bearer $token", reportId, body)
                if (response.isSuccessful) {
                    _state.update { it.copy(isLoading = false, isActionSuccess = true) }
                    fetchAssignedReports()
                    if (_state.value.selectedReport?.id == reportId) {
                        _state.update { it.copy(selectedReport = null) }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to upload photo: ${response.message()}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: "jpg"
        val file = File(context.cacheDir, "temp_completion_image.$extension")
        FileOutputStream(file).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return file
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetActionStatus() {
        _state.update { it.copy(isActionSuccess = false) }
    }
}
