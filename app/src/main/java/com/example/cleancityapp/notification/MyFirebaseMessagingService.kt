package com.example.cleancityapp.notification

import android.content.Context
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
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        
        val sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("access_token", null)
        
        if (accessToken != null) {
            scope.launch {
                try {
                    repository.registerDevice(accessToken, token)
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to register token", e)
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Message received: ${message.data}")
        
        val data = message.data
        val title = data["title"] ?: message.notification?.title ?: "Clean City Update"
        val body = data["body"] ?: message.notification?.body ?: "There's an update on your report."
        val complaintId = data["complaintId"]
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
