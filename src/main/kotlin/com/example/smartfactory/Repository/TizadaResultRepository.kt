package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Tizada.TizadaResult
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface TizadaResultRepository: CrudRepository<TizadaResult, UUID> {

    @Query("SELECT t FROM TizadaResult t WHERE t.tizada.uuid = :tizadaUUID")
    fun getTizadaResultByTizada(@Param("tizadaUUID") tizadaUUID: UUID): TizadaResult?
}
