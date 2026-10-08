package com.example.cleancityapp.presentation.user

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.toAppErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

data class UserState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isReportSuccess: Boolean = false
)

class UserViewModel(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val isSubmitting = AtomicBoolean(false)

    fun submitReport(
        imageUri: Uri,
        description: String,
        lat: Double,
        lon: Double,
        address: String = ""
    ) {
        if (!isSubmitting.compareAndSet(false, true)) return
        val token = sharedPreferences.getString("access_token", null)
        val userId = sharedPreferences.getString("user_id", null)
        if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
            isSubmitting.set(false)
            _state.update { it.copy(error = "Unauthorized\nAuthentication is required. Please sign in again.") }
            return
        }
        val timestamp = System.currentTimeMillis().toString()
        val fullDescription = if (address.isBlank()) description else "$description\nAddress: $address"

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isReportSuccess = false) }
            try {
                val response = withContext(Dispatchers.IO) {
                    val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
                    val file = getFileFromUri(imageUri, mimeType)
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                    authApi.submitReport(
                        "Bearer $token",
                        body,
                        userId.toRequestBody("text/plain".toMediaTypeOrNull()),
                        timestamp.toRequestBody("text/plain".toMediaTypeOrNull()),
                        lat.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                        lon.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                        fullDescription.toRequestBody("text/plain".toMediaTypeOrNull())
                    )
                }

                if (response.isSuccessful) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isReportSuccess = true
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.toAppErrorMessage("Unable to submit your report.")
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.toAppErrorMessage("Unable to submit your report.")
                    )
                }
            } finally {
                isSubmitting.set(false)
            }
        }
    }

    private fun getFileFromUri(uri: Uri, mimeType: String): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Unable to open image")
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        val file = File(context.cacheDir, "temp_report_${System.currentTimeMillis()}.$extension")
        FileOutputStream(file).use { outputStream ->
            inputStream.use { it.copyTo(outputStream) }
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
