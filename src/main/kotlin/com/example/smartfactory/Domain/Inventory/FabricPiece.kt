package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Usuarios.Usuario
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "fabric_pieces")
class FabricPiece (
    @Id @Column(name = "fabric_piece_id", nullable = false)
    val fabricPieceId: UUID,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "fabric_color_id")
    val color: FabricColor,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "molde_id")
    val molde: Molde,
    var name: String = "${molde.name} color ${color.name}",
    var stock: Int,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    var user: Usuario,
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "fabric_roll_id")
    val fabricRoll: FabricRoll? = null
)
