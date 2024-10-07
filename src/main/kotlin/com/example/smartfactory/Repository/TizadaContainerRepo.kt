package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Tizada.TizadaContainer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TizadaContainerRepository: CrudRepository<TizadaContainer, UUID> {
    fun findByUuid(containerId: UUID): TizadaContainer
    fun findByWidthAndHeight(containerWidth: Int, containerHeight: Int): TizadaContainer?
}