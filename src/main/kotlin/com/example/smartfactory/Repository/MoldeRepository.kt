package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Molde.Molde
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MoldeRepository: CrudRepository<Molde, UUID> {
    fun findMoldeByUuid(id: UUID): Molde?

    @Query("select m from Molde m")
    fun getAllMoldes(): List<Molde>
}
