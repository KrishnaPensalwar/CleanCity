package com.example.cleancityapp.data.repository

import android.content.Context
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
        if (lastToken == fcmToken) return@withContext

        val request = DeviceRegistrationRequest(
            deviceId = DeviceInfoUtils.getDeviceId(context),
            fcmToken = fcmToken,
            deviceName = DeviceInfoUtils.getDeviceName()
        )
        val response = api.registerDevice(token, request)
        if (response.status.value in 200..299) {
            sharedPreferences.edit().putString("last_registered_fcm_token", fcmToken).apply()
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
