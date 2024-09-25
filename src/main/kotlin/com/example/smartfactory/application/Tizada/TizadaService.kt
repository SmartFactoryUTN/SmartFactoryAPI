package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Molde.MoldeDeTizada
import com.example.smartfactory.Domain.Molde.MoldeDeTizadaId
import com.example.smartfactory.Domain.Tizada.MoldsQuantity
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.application.Tizada.Request.CreateTizadaRequest
import com.example.smartfactory.application.Tizada.Request.TizadaPartsRequest
import com.example.smartfactory.application.Tizada.Request.TizadaRequest
//import com.example.smartfactory.application.Tizada.Response.CreateTizadaResponse
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.exceptions.TizadaNoEncontradaException
import com.example.smartfactory.repository.MoldeDeTizadaRepository
import com.example.smartfactory.repository.MoldeRepository
import com.example.smartfactory.repository.TizadaRepository
import jakarta.transaction.Transactional
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
    @Autowired
    private val moldesDeTizadaRepo: MoldeDeTizadaRepository
) {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }

    fun getTizada(id: UUID): Tizada? {
        val tizada = tizadaRepo.getTizadaByUuid(id)
        if (tizada != null) {
            val parts: MutableList<MoldsQuantity> = mutableListOf()
            val moldesDeTizada = moldesDeTizadaRepo.getMoldesByTizadaId(tizada.uuid)
            for (moldeDeTizada in moldesDeTizada) {
                val molde = moldesRepo.findMoldeByUuid(moldeDeTizada.moldeDeTizadaId.moldeId)!!
                parts.add(MoldsQuantity(molde, moldeDeTizada.quantity))
            }
            tizada.parts = parts
            return tizada
        }
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
        tizada.deletedAt = LocalDateTime.now()
        tizadaRepo.save(tizada)
    }

    @Transactional
    fun getAllTizadas(): Collection<Tizada> {
        val tizadas = tizadaRepo.getAllTizadas()
        for (tizada in tizadas) {
            this.getTizada(tizada.uuid)
        }
        return tizadas
    }

    @Transactional
    fun queueTizada(req: CreateTizadaRequest): Tizada {
        val tizadaId = UUID.randomUUID()
        val tizadaPart: MutableList<MoldsQuantity> = mutableListOf()
        for (tizadaPartRequest: TizadaPartsRequest in req.molds) {
            val molde = moldesRepo.findMoldeByUuid(tizadaPartRequest.uuid)!!
            tizadaPart.add(MoldsQuantity(molde, tizadaPartRequest.quantity))
            val moldeDeTizada = MoldeDeTizada(MoldeDeTizadaId(moldeId = tizadaPartRequest.uuid, tizadaId = tizadaId), tizadaPartRequest.quantity)
            moldesDeTizadaRepo.save(moldeDeTizada)
        }
        val tizada = Tizada(
            uuid = tizadaId,
            name = req.name,
            configuration = TizadaConfiguration(),
            parts = tizadaPart,
            bin = null,
            results = mutableListOf(),
            state = TizadaState.CREATED,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
        tizadaRepo.save(tizada)
        return tizada
    }
}
