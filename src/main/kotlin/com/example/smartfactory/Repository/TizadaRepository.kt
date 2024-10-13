package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Tizada.Tizada
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface TizadaRepository: JpaRepository<Tizada, UUID> {
    fun getTizadaByUuid(uuid: UUID): Tizada?

    @Query("select t.uuid from Tizada t where t.active == true")
    fun getAllTizadas():List<UUID>
}
