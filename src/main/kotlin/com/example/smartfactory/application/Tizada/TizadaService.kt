package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Molde.MoldeDeTizada
import com.example.smartfactory.Domain.Molde.MoldeDeTizadaId
import com.example.smartfactory.Domain.Tizada.*
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.MoldeDeTizadaRepository
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.Repository.TizadaContainerRepository
import com.example.smartfactory.Repository.TizadaRepository
import com.example.smartfactory.application.Tizada.Request.*
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

const val CM_TO_SVG_FACTOR_FORM = 37.795275591

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

    @Transactional
    suspend fun createTizada(request: CreateTizadaRequest): UUID {
        val uuid = UUID.randomUUID() // id of this new tizada
        val tizadaParts: MutableList<MoldeDeTizada> = mutableListOf()
        request.molds.forEach {
            tizadaParts.add(MoldeDeTizada(MoldeDeTizadaId(moldeId = UUID.fromString(it.uuid), tizadaId = uuid), it.quantity))
        }

        // Second step prepare configuration (maxTime and utilizationPercentage)
        val configuration = TizadaConfiguration(
            UUID.randomUUID(), time = request.maxTime, utilizationPercentage = request.utilizationPercentage
        )

        // Third step: Retrieve bin for this tizada
        var bin = withContext(Dispatchers.IO) {
            tizadaContainerRepo.findByWidthAndHeight(request.width, request.height)
        }
        if (bin == null) {
            val height = request.height * CM_TO_SVG_FACTOR_FORM
            val width = request.width * CM_TO_SVG_FACTOR_FORM
            val svg =
                """<svg width='$width' height='$height' xmlns='http://www.w3.org/2000/svg'
                    viewBox='0 0 $width $height'>
                    <rect width="$width" height="$height" fill="none" stroke="#010101"></rect>
                </svg>""".trimIndent()
            val containerId = UUID.randomUUID()
            val url = lambdaService.uploadContainer(containerId, svg)
            bin = TizadaContainer(
                uuid = UUID.fromString("60eb9f4c-33b0-4769-a199-3920977ac1ea"),
                name = "Mesa de corte",
                height = request.height,
                width = request.width,
                url = url,
                area = (request.height * request.width).toDouble(),
                createdAt = LocalDateTime.now()
            )
        }

        // Fourth step: create tizada and save it into DB, ready for executing
        val tizada = Tizada(
            uuid = uuid,
            name = request.name,
            configuration = configuration,
            bin = bin,
            results = mutableListOf(),
            state = TizadaState.CREATED,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null,
            parts = mutableListOf()
        )

        tizada.moldesDeTizada = tizadaParts

        withContext(Dispatchers.IO) {
            tizadaRepo.save(tizada)
        }


        // Fifth step: return uuid of this new created tizada.
        /* No me parece necesario devolver el objeto tizada completo, ya que la app vuelve a Mis tizadas, consultando
        de nuevo las tizadas */
        return uuid
    }

    fun getTizada(id: UUID): Tizada {
        val tizada = tizadaRepo.getTizadaByUuid(id) ?: throw TizadaNotFoundException("No se encontró la tizada con ID $id")
        val parts: MutableList<MoldsQuantity> = mutableListOf()
        for (moldeDeTizada in tizada.moldesDeTizada) {
            val molde = moldesRepo.findMoldeByUuid(moldeDeTizada.moldeDeTizadaId.moldeId)!!
            parts.add(MoldsQuantity(molde, moldeDeTizada.quantity))
        }
        tizada.parts = parts
        return tizada
    }

    fun updateTizada(id: UUID, request: UpdateTizadaRequest): UUID {
        val tizada = tizadaRepo.getTizadaByUuid(id) ?: throw TizadaNotFoundException("No se encontró la tizada con ID $id")
        request.name?.let {
            tizada.name = it
            tizada.updatedAt = LocalDateTime.now()
        }
        tizadaRepo.save(tizada)
        return tizada.uuid
    }

    fun deleteTizada(id: UUID) {
        val tizada = tizadaRepo.getTizadaByUuid(id) ?: throw TizadaNotFoundException("No se encontró la tizada con ID $id")
        tizada.active = false
        tizada.deletedAt = LocalDateTime.now()
        tizadaRepo.save(tizada)
    }

    // FIXME add limit
    @Transactional
    fun getAllTizadas(finalizadas: String?): Collection<Tizada> {
        val tizadaIds = if (finalizadas == "true") {
            tizadaRepo.getAllTizadasFinalizadas()
        } else {
            tizadaRepo.getAllTizadas()
        }

        return tizadaIds.map { tizadaId -> this.getTizada(tizadaId) }
    }

    @Transactional
    fun invokeTizada(invokeTizadaRequest: InvokeTizadaRequest): InvokeTizadaResponse? {
        val tizada = this.getTizada(UUID.fromString(invokeTizadaRequest.tizadaUUID))

        tizada.state = TizadaState.IN_PROGRESS
        tizada.updatedAt = LocalDateTime.now()

        val parts = tizada.parts.map { it.toPart() }

        val invokeTizadaPayload = InvokeTizadaPayload(
            tizadaUUID = invokeTizadaRequest.tizadaUUID,
            user = invokeTizadaRequest.userUUID,
            parts = parts,
            bin = tizada.bin.toBin(),
            configuration = tizada.configuration.toInvokeConfiguration()
        )
        val jsonPayload = Json.encodeToString(invokeTizadaPayload)
        val response = lambdaService.invokeLambdaAsync(jsonPayload)
        tizadaRepo.save(tizada)
        return response
    }

    fun saveTizadaFinalizada(request: TizadaNotificationRequest) {
        logger.info {
            "Received save tizada finalizada notification for" +
                    " tizadaUUID: ${request.tizadaUUID} and userUUID: ${request.userUUID}"
        }
        val tizada = tizadaRepo.getTizadaByUuid(request.tizadaUUID) ?:
            throw TizadaNotFoundException("No se encontró la tizada con ID ${request.tizadaUUID}")
        val moldes = request.parts.map {
            moldesRepo.findMoldeByUuid(UUID.fromString(it.replace("molde-", "")))!!
        }
        val uuid = UUID.randomUUID()
        val tizadaResult = TizadaResult(
            uuid = uuid,
            url = request.url!!,
            configuration = tizada.configuration,
            bin = tizada.bin,
            parts = moldes,
            materialUtilization = request.materialUtilization,
            iterations = request.iterations,
            timeoutReached = request.timeoutReached,
            createdAt = LocalDateTime.now(),
            null,
            null
        )
        tizada.results?.add(tizadaResult)
        tizada.state = TizadaState.FINISHED
        tizada.updatedAt = LocalDateTime.now()
        tizadaRepo.save(tizada)
    }
}
