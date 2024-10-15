package com.example.smartfactory.Domain.Molde

import com.example.smartfactory.Domain.Auditable
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "moldes")
class Molde(
    @Id @Column(name = "molde_id", nullable = false)
    val uuid: UUID,
    var name: String,
    val url: String,
    var description: String,
    val area: Double?,
    var active: Boolean,
    var stock: Int,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime? = null,
    override var deletedAt: LocalDateTime? = null
): Auditable


@Embeddable
class MoldeDeTizadaId(
    @Column(name = "molde_id", nullable = false)
    val moldeId: UUID,
    @Column(name = "tizada_id", nullable = false)
    val tizadaId: UUID
)

@Entity
@Table(name = "moldes_de_tizada")
class MoldeDeTizada(
    @EmbeddedId
    val moldeDeTizadaId: MoldeDeTizadaId,
    val quantity: Int
)
