package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.MoldsQuantity
import com.example.smartfactory.application.Inventory.Response.GetGarmentResponse
import com.example.smartfactory.application.Tizada.Request.Part
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*
import kotlin.jvm.Transient

@Entity
@Table(name = "garments")
class Garment (
    @Id @Column(name = "garment_id", nullable = false)
    val garmentId: UUID,
    var name: String,
    var stock: Int,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?
): Auditable {
    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY, mappedBy = "garmentMoldId.garmentId")
    lateinit var garmentMolds: MutableList<GarmentMold>

    fun toGarmentResponse(): GetGarmentResponse {
        val parts = mutableListOf<Part>()
        for (mold in this.garmentMolds) {
            parts.add(
                Part(
                uuid = mold.garmentMoldId.moldeId.toString(),
                quantity = mold.quantity
            )
            )
        }
        return GetGarmentResponse(
            name = this.name,
            stock = this.stock,
            molds = parts
        )
    }
}

@Entity
@Table(name = "garment_molds")
data class GarmentMold(
    @EmbeddedId
    val garmentMoldId: GarmentMoldId,
    var quantity: Int
)

@Embeddable
data class GarmentMoldId(
    @Column(name = "garment_id", nullable = false)
    val garmentId: UUID,
    @Column(name = "molde_id", nullable = false)
    val moldeId: UUID
)