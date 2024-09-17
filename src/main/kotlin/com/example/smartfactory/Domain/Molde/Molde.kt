package com.example.smartfactory.Domain.Molde

import java.time.LocalDateTime
import java.util.*

open class Molde (
    val uuid: UUID,
    val name: String,
    val url: String,
    val description: String,
    val area: Double,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)