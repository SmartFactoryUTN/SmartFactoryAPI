package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Molde.Molde
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "fabric_pieces")
class FabricPiece (
    @Id @Column(name = "fabric_piece_id", nullable = false)
    val fabricPieceId: UUID,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "fabric_color_id")
    val color: FabricColor,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "molde_id")
    val molde: Molde,
    var name: String = "${molde.name} color ${color.name}",
    var stock: Int
)
