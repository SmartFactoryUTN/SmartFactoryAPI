package com.example.smartfactory.application.Tizada.Request

import java.util.*

enum class TizadaResultStatus {
    SUCCESS, ERROR;

    override fun toString(): String {
        return name.lowercase(Locale.getDefault())
    }

}

data class TizadaNotificationRequest(
    val tizadaUUID: UUID,
    val url: String? = null,
    val userUUID: UUID,
    val parts: List<String>? = null,
    val materialUtilization: Number? = null,
    val iterations: Number? = null,
    val timeoutReached: Boolean? = null,
    val status: TizadaResultStatus,
)
