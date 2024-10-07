package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Molde.MoldeDeTizada
import com.example.smartfactory.Domain.Molde.MoldeDeTizadaId
import com.example.smartfactory.Domain.Tizada.*
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.MoldeDeTizadaRepository
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.Repository.TizadaContainerRepository
import com.example.smartfactory.Repository.TizadaRepository
import com.example.smartfactory.application.Tizada.Request.CreateTizadaRequest
import com.example.smartfactory.application.Tizada.Request.InvokeTizadaRequest
import com.example.smartfactory.application.Tizada.Request.Part
import com.example.smartfactory.application.Tizada.Request.TizadaNotificationRequest
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TizadaService(
    @Autowired
    private val tizadaRepo: TizadaRepository,
    @Autowired
    private val moldesRepo: MoldeRepository,
    @Autowired
    private val moldesDeTizadaRepo: MoldeDeTizadaRepository,
    @Autowired
    private val lambdaService: LambdaService,
    @Autowired
    private val tizadaContainerRepo: TizadaContainerRepository
) {
    val logger = KotlinLogging.logger {}

    @Transactional(rollbackOn = [])
    suspend fun createTizada(request: CreateTizadaRequest): TizadaResponse<Any> {
        val uuid = UUID.randomUUID() // id of this new tizada
        val tizadaParts: MutableList<MoldsQuantity> = mutableListOf()

        // First step: persist on moldes_de_tizada intermediate table
        // Esto es necesario porque la tabla intermedia del many to many es customizada para tener el quantity
        for (mold: Part in request.molds) {
            val moldeDeTizada = MoldeDeTizada(MoldeDeTizadaId(moldeId = UUID.fromString(mold.uuid), tizadaId = uuid), mold.quantity)
            withContext(Dispatchers.IO) {
                moldesDeTizadaRepo.save(moldeDeTizada)
            }
        }

        // Second step prepare configuration (maxTime and utilizationPercentage)
        val configuration =
            TizadaConfiguration(UUID.randomUUID(), time = request.maxTime, utilizationPercentage = request.utilizationPercentage)

        // Third step: Retrieve bin for this tizada
        val bin = withContext(Dispatchers.IO) {
            tizadaContainerRepo.findByWidthAndHeight(TizadaContainer.DEFAULT_WIDTH, TizadaContainer.DEFAULT_HEIGHT)
        }
        if (bin == null) {
            /* habría que generar un svg, persistirlo en s3 y luego persistirlo en la DB
            * para simplificar, usamos un bin generico pro ahora.
            * */
        }

        // Fourth step: create tizada and save it into DB, ready for executing
        val tizada = Tizada(
            uuid = uuid,
            name = request.name,
            configuration = configuration,
            parts = tizadaParts,
            bin = bin,
            results = emptyList(),
            state = TizadaState.CREATED,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )

        withContext(Dispatchers.IO) {
            tizadaRepo.save(tizada)
        }

        // Fifth step: return uuid of this new created tizada.
        /* No me parece necesario devolver el objeto tizada completo, ya que la app vuelve a Mis tizadas, consultando
        de nuevo las tizadas */
        return TizadaResponse(status = "success", data = mapOf("uuid" to uuid))
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

    // FIXME: what fields can be updateable?
    fun updateTizada(id: UUID, name: String): Tizada {
        val tizada = tizadaRepo.getTizadaByUuid(id)
        if (tizada != null) {
            return Tizada(
                uuid = id,
                name = tizada.name,
                configuration = tizada.configuration,
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
    fun invokeTizada(invokeTizadaRequest: InvokeTizadaRequest): InvokeTizadaResponse? {

        val jsonPayload = Json.encodeToString(invokeTizadaRequest)
        return lambdaService.invokeLambdaAsync(jsonPayload)
    }

    fun saveTizadaFinalizada(request: TizadaNotificationRequest) {
        logger.info {
            "Received save tizada finalizada notification for" +
                    " tizadaUUUID: ${request.tizadaUUID} and userUUID: ${request.userUUID}"
        }
    }
}
