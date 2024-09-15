package com.example.smartfactory.Domain.Tizada

import java.time.LocalDateTime
import java.util.UUID

//data class User(val name: String, val age: Int)

open class Tizada(
    val uuid: UUID,
    val configuration: TizadaConfiguration,
    val parts: List<TizadaPart>,
    val bin: TizadaPart, // ?
    val state: TizadaState,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // val resultado: String, // TODO guardar en una lista los resultados intermedios
    val name: String,
    val tableLength: Long,
    val tableWidth: Long,
    var active: Boolean
)

enum class TizadaState {
    WAITING,
    IN_PROGRESS,
    FINISHED,
    ERROR
}
