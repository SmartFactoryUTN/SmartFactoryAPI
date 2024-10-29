package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Molde.Molde
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MoldeRepository: CrudRepository<Molde, UUID> {
    fun findMoldeByUuid(id: UUID): Molde?

    @Query("select m from Molde m where m.active = true")
    fun getAllMoldes(): List<Molde>

    @Query("SELECT m FROM Molde m WHERE m.owner = :owner AND m.active = true")
    fun findMoldeByOwner(@Param("owner") owner: UUID): List<Molde>?
}
