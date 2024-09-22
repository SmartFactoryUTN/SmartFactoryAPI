package com.example.smartfactory.Domain.Usuarios

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.Tizada
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Usuario (
    @Id @Column(name = "usuario_id")
    val uuid: UUID,
    @OneToMany @JoinColumn(name = "usuario_id")
    val tizadas: List<Tizada>,
    @OneToMany @JoinColumn(name = "usuario_id")
    val parts: List<Molde>,
    val nombre: String,
    val email: String,
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?
) : Auditable()