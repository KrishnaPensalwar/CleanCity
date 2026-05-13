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
    suspend fun registerDevice(token: String, fcmToken: String) = withContext(Dispatchers.IO) {
        val request = DeviceRegistrationRequest(
            deviceId = DeviceInfoUtils.getDeviceId(context),
            fcmToken = fcmToken,
            deviceName = DeviceInfoUtils.getDeviceName()
        )
        api.registerDevice(token, request)
    }

    suspend fun unregisterDevice(token: String) = withContext(Dispatchers.IO) {
        val deviceId = DeviceInfoUtils.getDeviceId(context)
        api.deleteDevice(token, deviceId)
    }
}
