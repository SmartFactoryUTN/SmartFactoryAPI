package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Inventario.BatchStage
import com.example.smartfactory.Domain.Molde.Molde
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Table(name = "tizadas")
@Entity
class Tizada(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "tizada_id", nullable = false)
    val uuid: UUID,
    val name: String,
    @Transient @JsonIgnore
    val configuration: TizadaConfiguration,
    @ManyToMany
    @JoinTable(name = "moldes_de_tizada", joinColumns = [JoinColumn(name = "tizada_id")], inverseJoinColumns = [JoinColumn(name = "molde_id")])
    val parts: List<Molde>,
    @OneToOne @PrimaryKeyJoinColumn(name = "tizada_container_id")
    val bin: TizadaContainer?,
    @OneToMany @JoinColumn(name = "tizada_id")
    val results: List<TizadaResult>?,
    val stage: BatchStage,
    val state: TizadaState,
    var active: Boolean,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?,
) : Auditable()


enum class TizadaState {
    CREATED,
    IN_PROGRESS,
    FINISHED,
    ERROR
}

enum class TipoTizada {
    RAPIDA, CUSTOM
}

@Entity
class TizadaContainer (
    @Id
    val uuid: UUID,
    val name: String,
    val height: Number,
    val width: Number,
    val area: Double
)
