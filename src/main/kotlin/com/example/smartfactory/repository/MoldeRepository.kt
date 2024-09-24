package com.example.smartfactory.repository

import com.example.smartfactory.Domain.Molde.Molde
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MoldeRepository: CrudRepository<Molde, UUID> {
    fun findMoldeByUuid(id: UUID): Molde?

    @Query("select m from Molde m")
    fun getAllMoldes(): List<Molde>
    //fun getMoldes(): List<Molde>
    //fun updateMolde(molde: Molde): Molde
    //fun deleteMoldeByUuid(uuid: UUID)
}