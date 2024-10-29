package com.example.smartfactory.application.Tizada.Request

import java.util.*

data class TizadaNotificationRequest(
    val tizadaUUID: UUID,
    val url: String?,
    val userUUID: UUID,
    val parts: List<String>,
    val materialUtilization: Long?,
    val iterations: Long?,
    val timeoutReached: Boolean
)

data class TizadaConfigurationRequest(
    val id: Number,
    val spaceBetweenParts: Double?
)
class TizadaContainerRequest(
    val uuid: String,
    val name: String,
    val height: Number,
    val width: Number,
    val area: Double
)
