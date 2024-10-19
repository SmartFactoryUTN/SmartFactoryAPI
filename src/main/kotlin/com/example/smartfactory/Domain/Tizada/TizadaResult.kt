package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "tizada_results")
class TizadaResult(
    @Id @Column(name = "tizada_result_id")
    val uuid: UUID,
    val url: String,
    @JoinColumn(name = "tizada_configuration_id")
    @OneToOne
    val configuration: TizadaConfiguration,
    @ManyToOne
    @JoinColumn(name = "tizada_container_id")
    @Cascade(CascadeType.ALL)
    val bin: TizadaContainer,
    @ManyToMany
    @JoinTable(name = "tizada_results_moldes", joinColumns = [JoinColumn(name = "tizada_result_id")], inverseJoinColumns = [JoinColumn(name = "molde_id")])
    val parts: List<Molde>,
    val materialUtilization: Number,
    val iterations: Number,
    val timeoutReached: Boolean,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?
): Auditable
