package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.Garment
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GarmentRepository: CrudRepository<Garment, UUID> {
    fun getGarmentByGarmentId(id: UUID): Garment?
    fun findGarmentsByUserUuidAndActive(id: UUID, active: Boolean = true): List<Garment>
}