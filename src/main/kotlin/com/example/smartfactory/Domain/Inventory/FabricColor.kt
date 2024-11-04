package com.example.smartfactory.Domain.Inventory

import com.example.smartfactory.Domain.Usuarios.Usuario
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "fabric_colors")
class FabricColor (
    @Id @Column(name="fabric_color_id")
    val fabricColorId: UUID,
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    val user: Usuario
)