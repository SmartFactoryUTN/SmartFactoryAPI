package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Tizada.Tizada
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface TizadaRepository: CrudRepository<Tizada, UUID> {
    fun getTizadaByUuid(uuid: UUID): Tizada?

    @Query("SELECT t FROM Tizada t WHERE t.owner = :owner AND t.active = true")
    fun findTizadasByOwner(@Param("owner") owner: UUID): List<Tizada>?
}
