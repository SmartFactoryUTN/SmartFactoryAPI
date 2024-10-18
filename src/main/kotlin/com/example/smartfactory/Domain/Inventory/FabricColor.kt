package com.example.smartfactory.Domain.Inventory

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "fabric_colors")
class FabricColor (
    @Id @Column(name="fabric_color_id")
    val fabricColorId: UUID,
    val name: String
)