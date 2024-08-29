package com.example.smartfactory.application.Tizada

import com.example.smartfactory.application.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.Domain.WebTizada.WebTizada
import com.example.smartfactory.ds.TizadaDataSource
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TizadaService(private val tizadaDataSource: TizadaDataSource) {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }

    fun getTizada(id: UUID): Tizada? {
        return tizadaDataSource.getTizada(id)
    }

    fun updateTizada(id: UUID, name: String, favorite: Boolean): Tizada {
        val tizada = this.getTizada(id)
        return Tizada(
            uuid = id,
            TizadaConfiguration(),
            tizada!!.parts,
            tizada.parts[0],
            tizada.state,
            tizada.createdAt,
            LocalDateTime.now(),
            "",
            name,
            tizada.tableLength,
            tizada.tableWidth,
            tizada.active
        )
    }

    fun deleteTizada(id: UUID) {
        val tizada = this.getTizada(id)
        tizada!!.active = false
    }

    fun getAllTizadas(): Collection<Tizada> {
        return tizadaDataSource.getAllTizadas()
    }

    fun queueTizada(tizada: Tizada): Tizada {
        return tizadaDataSource.queueTizada(tizada)
    }
}
