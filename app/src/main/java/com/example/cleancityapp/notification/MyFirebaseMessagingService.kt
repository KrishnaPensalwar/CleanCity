package com.example.cleancityapp.notification

import android.content.SharedPreferences
import android.util.Log
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val repository: DeviceRegistrationRepository by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) return

        val accessToken = sharedPreferences.getString("access_token", null)
        if (accessToken != null) {
            scope.launch {
                try {
                    repository.registerDevice(accessToken, token)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to register device token", e)
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) return

        val data = message.data
        val title = data["title"] ?: message.notification?.title ?: "Clean City Update"
        val body = data["body"] ?: message.notification?.body ?: "There's an update on your report."
        val complaintId = com.example.cleancityapp.security.ComplaintIdValidator
            .sanitize(data["complaintId"])
        val status = data["status"]

        if (complaintId != null && (status == "APPROVED" || status == "REJECTED")) {
            notificationHelper.showNotification(title, body, complaintId)
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
