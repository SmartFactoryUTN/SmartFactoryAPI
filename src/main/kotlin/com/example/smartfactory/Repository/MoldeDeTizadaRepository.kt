package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Molde.MoldeDeTizada
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MoldeDeTizadaRepository: CrudRepository<MoldeDeTizada, UUID> {
    @Query("select m from MoldeDeTizada m where m.moldeDeTizadaId.tizadaId = :tizadaId")
    fun getMoldesByTizadaId(tizadaId: UUID): List<MoldeDeTizada>
}
