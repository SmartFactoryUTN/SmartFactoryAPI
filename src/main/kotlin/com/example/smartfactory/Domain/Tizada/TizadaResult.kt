package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Molde.Molde
import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.util.UUID
import kotlin.jvm.Transient

@Entity
@Table(name = "tizada_results")
class TizadaResult(
    @Id @Column(name = "tizada_result_id")
    val uuid: UUID,
    val url: String,
    @Transient
    val configuration: TizadaConfiguration,
    @OneToOne
    @PrimaryKeyJoinColumn(name = "tizada_container_id")
    @Cascade(CascadeType.ALL)
    val bin: TizadaContainer,
    @ManyToMany
    @JoinTable(name = "tizada_results_parts", joinColumns = [JoinColumn(name = "tizada_result_id")], inverseJoinColumns = [JoinColumn(name = "molde_id")])
    val parts: List<Molde>,
    val materialUtilization: Int,
    val iterations: Int,
    val timeoutReached: Boolean
)