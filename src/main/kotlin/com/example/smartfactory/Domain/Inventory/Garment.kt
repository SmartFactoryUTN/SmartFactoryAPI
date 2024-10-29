package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Auditable
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "garments")
class Garment (
    @Id @Column(name = "garment_id", nullable = false)
    val garmentId: UUID,
    var name: String,
    var stock: Int,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?,
    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY, mappedBy = "garmentPieceId.garmentId")
    @JsonIgnore
    var garmentPieces: MutableList<GarmentPiece>
): Auditable {
}

@Entity
@Table(name = "garment_pieces")
data class GarmentPiece(
    @EmbeddedId
    val garmentPieceId: GarmentPieceId,
    var quantity: Int
)

@Embeddable
data class GarmentPieceId(
    @Column(name = "garment_id", nullable = false)
    val garmentId: UUID,
    @Column(name = "fabric_piece_id", nullable = false)
    val fabricPieceId: UUID
)
