package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.application.Tizada.Request.TizadaRequest
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.exceptions.TizadaNoEncontradaException
import com.example.smartfactory.repository.TizadaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TizadaService(
    @Autowired
    private val tizadaRepo: TizadaRepository
) {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }

    fun getTizada(id: UUID): Tizada? {
        val tizada = tizadaRepo.getTizada(id)
        if (tizada != null)
            return tizada
        throw TizadaNoEncontradaException("TIZADA_NO_ENCONTRADA")
    }

    fun updateTizada(id: UUID, name: String): Tizada {
        val tizada = tizadaRepo.getTizada(id)
        if (tizada != null) {
            return Tizada(
                uuid = id,
                name = tizada.name,
                configuration = TizadaConfiguration(),
                parts = tizada.parts,
                bin = tizada.bin,
                results = tizada.results,
                stage = tizada.stage,
                state = tizada.state,
                active = tizada.active,
                createdAt = tizada.createdAt,
                updatedAt = LocalDateTime.now(),
                deletedAt = null
            )
        }
        throw TizadaNoEncontradaException("TIZADA_NO_ENCONTRADA")
    }

    fun deleteTizada(id: UUID) {
        val tizada = this.getTizada(id)
        tizada!!.active = false
    }

    fun getAllTizadas(): Collection<Tizada> {
        return tizadaRepo.getAllTizadas()
    }

    fun queueTizada(tizada: Tizada): Tizada {
        return tizadaRepo.queueTizada(tizada)
    }
}
