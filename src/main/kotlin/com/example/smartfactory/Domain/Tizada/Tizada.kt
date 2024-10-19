package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Molde.MoldeDeTizada
import com.example.smartfactory.application.Tizada.Request.Part
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "tizadas")
class Tizada(
    @Id @Column(name = "tizada_id", nullable = false)
    val uuid: UUID,
    @NotNull
    var name: String,
    @OneToOne(cascade = [CascadeType.ALL]) @JoinColumn(name = "tizada_configuration_id")
    val configuration: TizadaConfiguration,
    @Transient
    var parts: MutableList<MoldsQuantity>,
    @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn(name = "tizada_container_id")
    val bin: TizadaContainer,
    @OneToMany(cascade = [CascadeType.ALL]) @JoinColumn(name = "tizada_id")
    val results: MutableList<TizadaResult>?,
    @Enumerated(EnumType.STRING)
    var state: TizadaState,
    var active: Boolean,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?,
) : Auditable {
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "moldeDeTizadaId.tizadaId")
    @JsonIgnore
    lateinit var moldesDeTizada: MutableList<MoldeDeTizada>
}


enum class TizadaState {
    CREATED,
    IN_PROGRESS,
    FINISHED,
    ERROR
}

class MoldsQuantity(
    val mold: Molde,
    val quantity: Int
) {
    fun toPart(): Part {
        return Part("molde-${mold.uuid}", quantity)
    }
}
