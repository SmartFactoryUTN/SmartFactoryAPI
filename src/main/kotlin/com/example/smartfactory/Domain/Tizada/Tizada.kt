package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

@Table(name = "tizadas")
@Entity
class Tizada(
    @Id @Column(name = "tizada_id", nullable = false)
    val uuid: UUID,
    @NotNull
    val name: String,
    @OneToOne @PrimaryKeyJoinColumn(name = "tizada_configuration_id")
    val configuration: TizadaConfiguration,
    @Transient
    var parts: MutableList<MoldsQuantity>,
    @OneToOne @PrimaryKeyJoinColumn(name = "tizada_container_id")
    val bin: TizadaContainer?,
    @OneToMany @JoinColumn(name = "tizada_id")
    val results: List<TizadaResult>?,
    val state: TizadaState,
    var active: Boolean,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?,
) : Auditable


enum class TizadaState {
    CREATED,
    IN_PROGRESS,
    FINISHED,
    ERROR
}

class MoldsQuantity(
    val mold: Molde,
    val quantity: Int
)
