package com.example.smartfactory.repository

import com.example.smartfactory.Domain.Tizada.Tizada
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface TizadaRepository: JpaRepository<Tizada, UUID> {
    fun getTizadaByUuid(uuid: UUID): Tizada?

    @Query("select t from Tizada t")
    fun getAllTizadas():List<Tizada>
}