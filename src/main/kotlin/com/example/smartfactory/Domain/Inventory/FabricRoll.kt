package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Usuarios.Usuario
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "fabric_rolls")
data class FabricRoll (
    @Id @Column(name = "fabric_roll_id", nullable = false)
    val fabricRollId: UUID,
    var name: String,
    @ManyToOne(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fabric_color_id")
    var color: FabricColor,
    var stock: Int,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    var user: Usuario,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?
) : Auditable