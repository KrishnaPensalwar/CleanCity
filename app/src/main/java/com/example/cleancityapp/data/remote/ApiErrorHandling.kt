package com.example.cleancityapp.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.Response

@Serializable
data class ApiErrorDetail(
    val field: String? = null,
    val message: String? = null,
    val rejectedValue: String? = null,
)

@Serializable
data class ApiErrorResponse(
    val isSuccess: Boolean = false,
    val status: Int? = null,
    val errorCode: String? = null,
    val error: String? = null,
    val message: String? = null,
    val timestamp: String? = null,
    val path: String? = null,
    val details: List<ApiErrorDetail>? = null,
)

class AppApiException(
    val errorCode: String? = null,
    val title: String? = null,
    override val message: String,
) : Exception(message)

private val apiErrorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun Response<*>.toAppErrorMessage(defaultMessage: String): String {
    val rawBody = runCatching { errorBody()?.string() }.getOrNull()
    return rawBody.toAppErrorMessage(defaultMessage, fallbackTitle = message())
}

fun Throwable.toAppErrorMessage(
    defaultMessage: String = "Something went wrong. Please try again.",
): String {
    return when (this) {
        is AppApiException -> formatErrorMessage(title, message)
        else -> localizedMessage?.takeIf { it.isNotBlank() } ?: defaultMessage
    }
}

internal suspend inline fun <reified T> HttpResponse.bodyOrApiError(defaultMessage: String): T {
    if (status.isSuccess()) return body<T>()

    val rawBody = runCatching { bodyAsText() }.getOrNull()
    throw AppApiException(
        message = rawBody.toAppErrorMessage(
            defaultMessage = defaultMessage,
            fallbackTitle = status.description,
        ),
    )
}

internal fun String?.toAppErrorMessage(
    defaultMessage: String,
    fallbackTitle: String? = null,
): String {
    val parsed = parseApiError(this)
    if (parsed == null) {
        return fallbackTitle
            ?.takeIf { it.isNotBlank() && !it.equals("OK", ignoreCase = true) }
            ?.let { formatErrorMessage(it, defaultMessage) }
            ?: defaultMessage
    }

    val detailLines = parsed.details
        .orEmpty()
        .mapNotNull { it.message?.trim()?.takeIf(String::isNotBlank) }
        .distinct()

    val bodyMessage = when {
        detailLines.isNotEmpty() -> detailLines.joinToString("\n")
        !parsed.message.isNullOrBlank() -> parsed.message
        else -> defaultMessage
    }

    val title = parsed.error
        ?.trim()
        ?.takeIf { it.isNotBlank() && !it.equals(bodyMessage, ignoreCase = true) }
        ?: parsed.errorCode

    return formatErrorMessage(title, bodyMessage)
}

private fun parseApiError(rawBody: String?): ApiErrorResponse? {
    if (rawBody.isNullOrBlank()) return null
    return runCatching {
        apiErrorJson.decodeFromString<ApiErrorResponse>(rawBody)
    }.getOrNull()
}

private fun formatErrorMessage(title: String?, message: String): String {
    val cleanMessage = message.trim().ifBlank {
        "Something went wrong. Please try again."
    }
    val cleanTitle = title?.trim()?.takeIf { it.isNotBlank() }
    return if (cleanTitle == null) cleanMessage else "$cleanTitle\n$cleanMessage"
}
