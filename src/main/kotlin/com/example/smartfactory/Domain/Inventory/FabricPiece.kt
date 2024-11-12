package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Usuarios.Usuario
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "fabric_pieces")
class FabricPiece (
    @Id @Column(name = "fabric_piece_id", nullable = false)
    val fabricPieceId: UUID,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "molde_id")
    val molde: Molde,
    var stock: Int,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    var user: Usuario,
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "fabric_roll_id")
    val fabricRoll: FabricRoll,
    var name: String = "${molde.name} color ${fabricRoll.color.name}",
    var active: Boolean = true,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime? = null,
    override var deletedAt: LocalDateTime? = null
): Auditable
