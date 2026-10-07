package com.example.cleancityapp.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IncomingNotificationHandlerTest {

    @Test
    fun showsApprovedComplaintUpdateWhenEnabled() {
        val shown = mutableListOf<Triple<String, String, String>>()
        val handler = IncomingNotificationHandler(
            notificationsEnabled = { true },
            onShow = { title, body, id -> shown += Triple(title, body, id) },
        )

        val handled = handler.onReceived(
            IncomingNotification(
                title = "Approved",
                body = "Your report moved",
                extras = mapOf("complaintId" to "cmp-1", "status" to "APPROVED"),
            ),
        )

        assertTrue(handled)
        assertEquals(listOf(Triple("Approved", "Your report moved", "cmp-1")), shown)
    }

    @Test
    fun readsDevLensPayloadPrefixedComplaintId() {
        val shown = mutableListOf<String>()
        val handler = IncomingNotificationHandler(
            notificationsEnabled = { true },
            onShow = { _, _, id -> shown += id },
        )

        val handled = handler.onReceived(
            IncomingNotification(
                title = "DevLens test",
                body = "Local notification",
                extras = mapOf(
                    "payload_complaintId" to "cmp-9",
                    "status" to "REJECTED",
                ),
            ),
        )

        assertTrue(handled)
        assertEquals(listOf("cmp-9"), shown)
    }

    @Test
    fun skipsWhenNotificationsDisabled() {
        var shown = false
        val handler = IncomingNotificationHandler(
            notificationsEnabled = { false },
            onShow = { _, _, _ -> shown = true },
        )

        val handled = handler.onReceived(
            IncomingNotification(
                title = "Approved",
                body = "body",
                extras = mapOf("complaintId" to "cmp-1", "status" to "APPROVED"),
            ),
        )

        assertFalse(handled)
        assertFalse(shown)
    }

    @Test
    fun skipsUnknownStatusAndUnsafeId() {
        val shown = mutableListOf<String>()
        val handler = IncomingNotificationHandler(
            notificationsEnabled = { true },
            onShow = { _, _, id -> shown += id },
        )

        assertFalse(
            handler.onReceived(
                IncomingNotification("t", "b", mapOf("complaintId" to "cmp-1", "status" to "PENDING")),
            ),
        )
        assertFalse(
            handler.onReceived(
                IncomingNotification("t", "b", mapOf("complaintId" to "../x", "status" to "APPROVED")),
            ),
        )
        assertTrue(shown.isEmpty())
    }

    @Test
    fun clickTargetUsesSanitizedComplaintId() {
        assertEquals(
            "cmp-1",
            IncomingNotification.complaintIdFrom(
                mapOf("payload_complaintId" to "cmp-1", "complaintId" to "ignored-if-prefixed-present"),
            ),
        )
        assertEquals("abc-2", IncomingNotification.complaintIdFrom(mapOf("complaintId" to "abc-2")))
        assertNull(IncomingNotification.complaintIdFrom(mapOf("complaintId" to "not/valid")))
    }
}
