package com.example.cleancityapp.security

/**
 * Validates externally supplied complaint IDs (notification intents / deep links).
 * Rejects empty, oversized, or path-traversal-like values before navigation.
 */
object ComplaintIdValidator {
    private val SAFE_ID = Regex("^[A-Za-z0-9_\\-]{1,64}$")

    fun sanitize(raw: String?): String? {
        val value = raw?.trim().orEmpty()
        if (value.isEmpty()) return null
        if (!SAFE_ID.matches(value)) return null
        return value
    }
}
