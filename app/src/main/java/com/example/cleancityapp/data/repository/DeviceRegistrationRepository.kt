package com.example.cleancityapp.data.repository

import android.content.Context
import android.util.Log
import com.example.cleancityapp.data.remote.DeviceRegistrationApi
import com.example.cleancityapp.data.remote.DeviceRegistrationRequest
import com.example.cleancityapp.util.DeviceInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRegistrationRepository(
    private val api: DeviceRegistrationApi,
    private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    suspend fun registerDevice(token: String, fcmToken: String) = withContext(Dispatchers.IO) {
        val lastToken = sharedPreferences.getString("last_registered_fcm_token", null)
        if (lastToken == fcmToken) {
            Log.d("DeviceReg", "Token already registered, skipping.")
            return@withContext
        }

        val request = DeviceRegistrationRequest(
            deviceId = DeviceInfoUtils.getDeviceId(context),
            fcmToken = fcmToken,
            deviceName = DeviceInfoUtils.getDeviceName(),
            platform = "ANDROID"
        )
        
        try {
            val response = api.registerDevice(token, request)
            Log.d("DeviceReg", "Success: ${response.message}")
            if (response.deviceName != null) {
                Log.d("DeviceReg", "Registered device: ${response.deviceName}")
            }
            sharedPreferences.edit().putString("last_registered_fcm_token", fcmToken).apply()
        } catch (e: Exception) {
            Log.e("DeviceReg", "Failed to register device", e)
        }
    }

    fun clearCachedToken() {
        sharedPreferences.edit().remove("last_registered_fcm_token").apply()
    }

    suspend fun unregisterDevice(token: String) = withContext(Dispatchers.IO) {
        val deviceId = DeviceInfoUtils.getDeviceId(context)
        api.deleteDevice(token, deviceId)
    }
}
