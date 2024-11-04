package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.FabricColor
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FabricColorRepository: CrudRepository<FabricColor, UUID> {
    fun getFabricColorByFabricColorId(fabricColorId: UUID): FabricColor?
    fun getFabricColorsByUserUuid(userUuid: UUID): List<FabricColor>
}