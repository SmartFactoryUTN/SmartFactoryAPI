package com.example.smartfactory.Domain.Usuarios

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.Tizada
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "usuarios")
class Usuario(
    @Id @Column(name = "usuario_id")
    val uuid: UUID,
    @OneToMany @JoinColumn(name = "usuario_id")
    val tizadas: List<Tizada>?,
    @OneToMany @JoinColumn(name = "usuario_id")
    val parts: List<Molde>?,
    val name: String,
    @Column(unique = true)
    val email: String,
    @Column(unique = true)
    val externalId: String,
    val subscription: String = "PREMIUM",
    override var createdAt: LocalDateTime = LocalDateTime.now(),
    override var updatedAt: LocalDateTime? = null,
    override var deletedAt: LocalDateTime? = null
) : Auditable
