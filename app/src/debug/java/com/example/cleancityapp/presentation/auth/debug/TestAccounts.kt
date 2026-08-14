package com.example.cleancityapp.presentation.auth.debug

/**
 * DEBUG ONLY — update these credentials here.
 *
 * File:
 *   app/src/debug/java/com/example/cleancityapp/presentation/auth/debug/TestAccounts.kt
 *
 * This object is compiled into debug builds only. Release has no test accounts.
 */
data class TestAccount(
    val displayName: String,
    val subtitle: String,
    val email: String,
    val password: String,
    val emoji: String,
)

object TestAccounts {
    val user = TestAccount(
        displayName = "Citizen",
        subtitle = "User account",
        email = "krishanpensalwar3@gmail.com",
        password = "Krishna123",
        emoji = "👤",
    )

    val driver = TestAccount(
        displayName = "Driver",
        subtitle = "Driver account",
        email = "driver@gmail.com",
        password = "Driver",
        emoji = "🚚",
    )
}
