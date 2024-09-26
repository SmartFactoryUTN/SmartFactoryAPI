package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Tizada.MoldsQuantity
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.application.Tizada.Request.InvokeTizadaRequest
import com.example.smartfactory.application.Tizada.Request.CreateTizadaRequest
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.MoldeDeTizadaRepository
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.Repository.TizadaRepository
import com.example.smartfactory.integration.AwsProperties
import com.example.smartfactory.integration.LambdaService
import jakarta.transaction.Transactional
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    private val moldesDeTizadaRepo: MoldeDeTizadaRepository,
    @Autowired
    private val lambdaService: NewLambdaService,
    @Autowired
    private val awsProperties: AwsProperties
) {
    // FIXME save tizada to db
    fun createTizada(request: CreateTizadaRequest): TizadaResponse<Any> {
        val generatedId = 1L
        return TizadaResponse(status = "ok", message = "Tizada creada exitosamente", data = mapOf("id" to generatedId))
/*
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
        return tizada*/
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
        throw TizadaNotFoundException("TIZADA_NO_ENCONTRADA")
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
        throw TizadaNotFoundException("TIZADA_NO_ENCONTRADA")
    }

    fun deleteTizada(id: UUID) {
        val tizada = this.getTizada(id)
        tizada!!.active = false
        tizada.deletedAt = LocalDateTime.now()
        tizadaRepo.save(tizada)
    }

    // FIXME add limit
    @Transactional
    fun getAllTizadas(): Collection<Tizada> {
        val tizadas = tizadaRepo.getAllTizadas()
        for (tizada in tizadas) {
            this.getTizada(tizada.uuid)
        }
        return tizadas
    }

    @Transactional
    fun invokeTizada(invokeTizadaRequest: InvokeTizadaRequest): String? {

        val jsonPayload = Json.encodeToString(invokeTizadaRequest)
        val response = lambdaService.invokeLambda(jsonPayload)

        return response
    }
}
