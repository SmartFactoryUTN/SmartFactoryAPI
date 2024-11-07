package com.example.smartfactory.Domain.Usuarios

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.Domain.Inventory.FabricColor
import com.example.smartfactory.Domain.Inventory.FabricPiece
import com.example.smartfactory.Domain.Inventory.FabricRoll
import com.example.smartfactory.Domain.Inventory.Garment
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.Tizada
import com.fasterxml.jackson.annotation.JsonIgnore
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
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var fabricRolls: List<FabricRoll>? = null,
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var garments: List<Garment>? = null,
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var fabricPieces: List<FabricPiece>? = null,
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var fabricColors: List<FabricColor>? = null,
    val subscription: String = "PREMIUM",
    override var createdAt: LocalDateTime = LocalDateTime.now(),
    override var updatedAt: LocalDateTime? = null,
    override var deletedAt: LocalDateTime? = null
) : Auditable
