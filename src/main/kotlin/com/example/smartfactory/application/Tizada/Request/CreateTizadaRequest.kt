package com.example.smartfactory.application.Tizada.Request

import com.example.smartfactory.Domain.Tizada.Tizada
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable
import java.util.*


class CreateTizadaRequest(
    val date: Date,
    val tizada: Tizada
)


@Serializable
data class Part(
    @field:NotEmpty(message = "Part UUID cannot be empty")
    val uuid: String,

    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)

@Serializable
data class Bin(
    @field:NotEmpty(message = "Bin UUID cannot be empty")
    val uuid: String,
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)

@Serializable
data class InvokeConfiguration(
    @field:Min(value = 1, message = "Max iterations must be at least 1")
    val maxIterations: Int,

    @field:Min(value = 1, message = "Material utilization must be at least 1")
    val materialUtilization: Int,

    @field:Min(value = 0, message = "Timeout must be at least 0")
    val timeout: Int
)


@Serializable
data class InvokeTizadaRequest(
    @field:NotEmpty(message = "Tizada.kt ID cannot be empty")
    val tizadaUUID: String,

    @field:NotEmpty(message = "User ID cannot be empty")
    val user: String,

    @field:NotEmpty(message = "Parts list cannot be empty")
    val parts: List<Part>,

    @field:NotNull(message = "Bin cannot be null")
    val bin: Bin,

    @field:NotNull(message = "Configuration cannot be null")
    val configuration: InvokeConfiguration
)

