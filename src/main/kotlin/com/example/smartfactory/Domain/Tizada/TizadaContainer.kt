package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

@Suppress("all")
@Entity
@Table(name = "tizada_containers")
class TizadaContainer (
    @Id @Column(name = "tizada_container_id")
    val uuid: UUID,
    val name: String,
    @NotNull
    val height: Number,
    @NotNull
    val width: Number,
    val area: Double,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?,
): Auditable
