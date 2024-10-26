package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.FabricRoll
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FabricRollRepository: CrudRepository<FabricRoll, UUID> {
    fun getFabricRollByFabricRollId(id: UUID): FabricRoll?
}
