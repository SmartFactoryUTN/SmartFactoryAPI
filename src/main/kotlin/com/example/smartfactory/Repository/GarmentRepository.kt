package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.Garment
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GarmentRepository: CrudRepository<Garment, UUID> {
}