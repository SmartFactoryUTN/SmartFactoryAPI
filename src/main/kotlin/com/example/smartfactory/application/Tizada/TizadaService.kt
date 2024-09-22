package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Inventario.BatchStage
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.application.Tizada.Request.CreateTizadaRequest
import com.example.smartfactory.application.Tizada.Request.TizadaPartsRequest
import com.example.smartfactory.application.Tizada.Request.TizadaRequest
import com.example.smartfactory.application.Tizada.Response.CreateTizadaResponse
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.exceptions.TizadaNoEncontradaException
import com.example.smartfactory.repository.MoldeRepository
import com.example.smartfactory.repository.TizadaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TizadaService(
    @Qualifier("tizadaRepository") @Autowired
    private val tizadaRepo: TizadaRepository,
    @Autowired
    private val moldesRepo: MoldeRepository,
) {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }

    fun getTizada(id: UUID): Tizada? {
        val tizada = tizadaRepo.getTizadaByUuid(id)
        if (tizada != null)
            return tizada
        throw TizadaNoEncontradaException("TIZADA_NO_ENCONTRADA")
    }

    fun updateTizada(id: UUID, name: String): Tizada {
        val tizada = tizadaRepo.getTizadaByUuid(id)
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

    fun queueTizada(req: CreateTizadaRequest): CreateTizadaResponse {
        val moldes: MutableList<Molde> = mutableListOf()
        for (part: TizadaPartsRequest in req.molds) {
            val parts = List(part.cantidad) { moldesRepo.findMoldeByUuid(part.uuid)!! }
            moldes.addAll(parts)
        }
        val tizada = Tizada(
            uuid = UUID.randomUUID(),
            name = req.name!!,
            configuration = TizadaConfiguration(),
            parts = moldes,
            bin = null,
            results = null,
            stage = BatchStage.AWAITING_TIZADA,
            state = TizadaState.CREATED,
            active = true,
            createdAt = LocalDateTime.now(),
            null,
            null
        )

        tizadaRepo.save(tizada)
        return CreateTizadaResponse(message = "Tizada encolada correctamente")
    }
}
