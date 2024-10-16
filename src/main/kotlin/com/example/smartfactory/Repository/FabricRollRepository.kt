package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.FabricRoll
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface FabricRollRepository: CrudRepository<FabricRoll, UUID> {
}