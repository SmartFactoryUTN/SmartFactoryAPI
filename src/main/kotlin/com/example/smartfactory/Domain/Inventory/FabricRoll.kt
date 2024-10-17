package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Auditable
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "fabric_rolls")
data class FabricRoll (
    @Id @Column(name = "fabric_roll_id", nullable = false)
    val fabricRollId: UUID,
    var name: String,
    var color: String,
    var stock: Int,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?
) : Auditable