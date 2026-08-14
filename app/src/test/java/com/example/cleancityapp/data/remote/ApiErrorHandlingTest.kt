package com.example.cleancityapp.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiErrorHandlingTest {

    @Test
    fun formatsInvalidCredentialsError() {
        val rawError = """
            {
              "isSuccess": false,
              "status": 401,
              "errorCode": "AUTH_002",
              "error": "Invalid Credentials",
              "message": "The email or password you entered is incorrect. Please try again.",
              "timestamp": "2026-08-11T12:10:00.123Z",
              "path": "/auth/login",
              "details": null
            }
        """.trimIndent()

        val message = rawError.toAppErrorMessage("Fallback")

        assertEquals(
            "Invalid Credentials\nThe email or password you entered is incorrect. Please try again.",
            message,
        )
    }

    @Test
    fun formatsValidationDetailsAsMultilineMessage() {
        val rawError = """
            {
              "isSuccess": false,
              "status": 400,
              "errorCode": "VALID_001",
              "error": "Validation Failed",
              "message": "Email is required",
              "timestamp": "2026-08-11T12:10:00.123Z",
              "path": "/auth/login",
              "details": [
                { "field": "email", "message": "Email is required", "rejectedValue": null },
                { "field": "password", "message": "Password is required", "rejectedValue": null }
              ]
            }
        """.trimIndent()

        val message = rawError.toAppErrorMessage("Fallback")

        assertEquals(
            "Validation Failed\nEmail is required\nPassword is required",
            message,
        )
    }
}
