package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Inventario.BatchStage
import com.example.smartfactory.Domain.Molde.Molde
import java.time.LocalDateTime
import java.util.UUID

//data class User(val name: String, val age: Int)

open class Tizada(
    val uuid: UUID,
    val name: String,
    val configuration: TizadaConfiguration,
    val parts: List<Molde>,
    val bin: TizadaContainer,
    val results: List<TizadaResult>,
    val stage: BatchStage,
    val state: TizadaState,
    var active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)

enum class TizadaState {
    CREATED,
    IN_PROGRESS,
    FINISHED,
    ERROR
}

enum class TipoTizada {
    RAPIDA, CUSTOM
}

class TizadaContainer (
    val uuid: UUID,
    val name: String,
    val height: Number,
    val weight: Number,
    val area: Double
)
