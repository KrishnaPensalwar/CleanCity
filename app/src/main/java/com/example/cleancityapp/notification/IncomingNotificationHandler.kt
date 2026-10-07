package com.example.cleancityapp.notification

import com.example.cleancityapp.security.ComplaintIdValidator

data class IncomingNotification(
    val title: String,
    val body: String,
    val extras: Map<String, String?>,
) {
    companion object {
        fun complaintIdFrom(extras: Map<String, String?>): String? =
            ComplaintIdValidator.sanitize(
                extras["payload_complaintId"] ?: extras["complaintId"],
            )
    }
}

class IncomingNotificationHandler(
    private val notificationsEnabled: () -> Boolean,
    private val onShow: (title: String, body: String, complaintId: String) -> Unit,
) {
    fun onReceived(notification: IncomingNotification): Boolean {
        if (!notificationsEnabled()) return false
        val complaintId = IncomingNotification.complaintIdFrom(notification.extras) ?: return false
        val status = notification.extras["status"]?.trim()?.uppercase()
        if (status != "APPROVED" && status != "REJECTED") return false
        onShow(notification.title, notification.body, complaintId)
        return true
    }
}
