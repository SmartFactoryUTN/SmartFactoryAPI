package com.example.smartfactory.application.Tizada.Request

import java.util.*

data class TizadaNotificationRequest(
    val tizadaUUID: UUID,
    val url: String,
    val userUUID: UUID,
    val configuration: TizadaConfigurationRequest,
    val bin: TizadaContainerRequest,
    val parts: List<String>,
    val materialUtilization: Number,
    val iterations: Number,
    val timeoutReached: Boolean
)

data class TizadaConfigurationRequest(
    val id: Number,
    val spaceBetweenParts: Double
)
class TizadaContainerRequest(
    val uuid: UUID,
    val name: String,
    val height: Number,
    val width: Number,
    val area: Double
)
