package com.example.smartfactory.Domain.Molde

import com.example.smartfactory.Domain.Auditable
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "moldes")
class Molde(
    @Id
    @Column(name = "molde_id", nullable = false)
    val uuid: UUID,
    val name: String,
    val url: String,
    val description: String,
    val area: Double?,
    val active: Boolean,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?
): Auditable()
