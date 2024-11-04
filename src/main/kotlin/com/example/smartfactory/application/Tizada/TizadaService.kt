package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Molde.MoldeDeTizada
import com.example.smartfactory.Domain.Molde.MoldeDeTizadaId
import com.example.smartfactory.Domain.Tizada.*
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.Repository.TizadaRepository
import com.example.smartfactory.Repository.TizadaResultRepository
import com.example.smartfactory.application.Tizada.Request.*
import com.example.smartfactory.integration.InvokeTizadaFrontResponse
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
    private val tizadaRepository: TizadaRepository,
    @Autowired
    private val moldesRepo: MoldeRepository,
    @Autowired
    private val lambdaService: LambdaService,
    @Autowired
    private val tizadaResultRepository: TizadaResultRepository
) {
    val logger = KotlinLogging.logger {}

    @Transactional
    suspend fun createTizada(request: CreateTizadaRequest, owner: UUID): UUID {
        val uuid = UUID.randomUUID() // id of this new tizada
        val tizadaParts: MutableList<MoldeDeTizada> = mutableListOf()
        request.molds.forEach {
            tizadaParts.add(
                MoldeDeTizada(MoldeDeTizadaId(moldeId = UUID.fromString(it.uuid), tizadaId = uuid), it.quantity)
            )
        }

        // Second step prepare configuration (maxTime and utilizationPercentage)
        val configuration = TizadaConfiguration(
            UUID.randomUUID(), time = request.maxTime, utilizationPercentage = request.utilizationPercentage
        )
        val height = request.height * CM_TO_SVG_FACTOR_FORM
        val width = request.width * CM_TO_SVG_FACTOR_FORM
        val svg =
            """<svg width='$width' height='$height' xmlns='http://www.w3.org/2000/svg'
                viewBox='0 0 $width $height'>
                <rect width="$width" height="$height" fill="none" stroke="#010101"></rect>
            </svg>""".trimIndent()
        val containerId = UUID.randomUUID()
        val url = lambdaService.uploadContainer(owner, containerId, svg)
        val bin = TizadaContainer(
            uuid = containerId,
            name = "Mesa de corte",
            height = request.height,
            width = request.width,
            url = url,
            area = (request.height * request.width).toDouble(),
            createdAt = LocalDateTime.now()
        )

        // Fourth step: create tizada and save it into DB, ready for executing
        val tizada = Tizada(
            uuid = uuid,
            owner = owner,
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
            tizadaRepository.save(tizada)
        }


        // Fifth step: return uuid of this new created tizada.
        /* No me parece necesario devolver el objeto tizada completo, ya que la app vuelve a Mis tizadas, consultando
        de nuevo las tizadas */
        return uuid
    }

    fun getTizadaByUUID(id: UUID): Tizada {
        val tizada = tizadaRepository.getTizadaByUuid(id) ?: throw TizadaNotFoundException("No se encontró la tizada con ID $id")
        val parts: MutableList<MoldsQuantity> = mutableListOf()
        for (moldeDeTizada in tizada.moldesDeTizada) {
            val molde = moldesRepo.findMoldeByUuid(moldeDeTizada.moldeDeTizadaId.moldeId)!!
            parts.add(MoldsQuantity(molde, moldeDeTizada.quantity))
        }
        tizada.parts = parts
        return tizada
    }

    fun updateTizada(id: UUID, request: UpdateTizadaRequest): UUID {
        val tizada = tizadaRepository.getTizadaByUuid(id) ?: throw TizadaNotFoundException("No se encontró la tizada con ID $id")
        request.name?.let {
            tizada.name = it
            tizada.updatedAt = LocalDateTime.now()
        }
        tizadaRepository.save(tizada)
        return tizada.uuid
    }

    fun deleteTizada(id: UUID) {
        val tizada = tizadaRepository.getTizadaByUuid(id)
            ?: throw TizadaNotFoundException("No se encontró la tizada con ID $id")
        tizada.active = false
        tizada.deletedAt = LocalDateTime.now()
        tizadaRepository.save(tizada)
    }

    @Transactional
    fun invokeTizada(invokeTizadaRequest: InvokeTizadaRequest): InvokeTizadaFrontResponse? {
        val tizada = this.getTizadaByUUID(UUID.fromString(invokeTizadaRequest.tizadaUUID))

        tizada.state = TizadaState.IN_PROGRESS
        tizada.updatedAt = LocalDateTime.now()
        tizada.invokedAt = tizada.updatedAt

        tizada.configuration.time
        tizada.estimatedEndTime = tizada.invokedAt!!.plusSeconds(tizada.configuration.time.toLong() / 1000)

        val parts = tizada.parts.map { it.toPart() }

        val invokeTizadaPayload = InvokeTizadaPayload(
            tizadaUUID = invokeTizadaRequest.tizadaUUID,
            user = invokeTizadaRequest.userUUID,
            parts = parts,
            bin = tizada.bin.toBin(),
            configuration = tizada.configuration.toInvokeConfiguration()
        )
        val jsonPayload = Json.encodeToString(invokeTizadaPayload)
        lambdaService.invokeLambdaAsync(jsonPayload)
        tizadaRepository.save(tizada)
        return InvokeTizadaFrontResponse(tizada.invokedAt!!, tizada.estimatedEndTime!!)
    }

    fun saveTizadaFinalizada(request: TizadaNotificationRequest) {
        logger.info {
            "Received save tizada finalizada notification for" +
                    " tizadaUUID: ${request.tizadaUUID} and userUUID: ${request.userUUID}"
        }
        val tizada = tizadaRepository.getTizadaByUuid(request.tizadaUUID) ?:
            throw TizadaNotFoundException("No se encontró la tizada con ID ${request.tizadaUUID}")
        val moldes = request.parts?.map {
            moldesRepo.findMoldeByUuid(UUID.fromString(it.replace("molde-", "")))!!
        }
        val uuid = UUID.randomUUID()
        val tizadaResult = TizadaResult(
            uuid = uuid,
            url = request.url!!,
            tizada = tizada,
            configuration = tizada.configuration,
            bin = tizada.bin,
            parts = moldes,
            materialUtilization = request.materialUtilization,
            iterations = request.iterations,
            timeoutReached = request.timeoutReached,
            createdAt = LocalDateTime.now(),
        )
        tizada.results?.add(tizadaResult)
        tizada.state = TizadaState.FINISHED
        tizada.updatedAt = LocalDateTime.now()
        tizadaRepository.save(tizada)

        logger.info {
            "Tizada finalizada correctamente" +
                    " tizadaUUID: ${request.tizadaUUID} and userUUID: ${request.userUUID}"
        }
    }

    @Transactional
    fun getAllTizadasByOwner(userUUID: UUID): List<Tizada>? {
        val tizadas = tizadaRepository.findTizadasByOwner(userUUID)

        return tizadas
    }

    fun saveTizadaFinalizadaConError(request: TizadaNotificationRequest) {
        logger.info {
            "Received save tizada finalizada notification for" +
                    " tizadaUUID: ${request.tizadaUUID} and userUUID: ${request.userUUID}"
        }

        val tizada = tizadaRepository.getTizadaByUuid(request.tizadaUUID) ?:
            throw TizadaNotFoundException("No se encontró la tizada con ID ${request.tizadaUUID}")
        val moldes = request.parts?.map {
            moldesRepo.findMoldeByUuid(UUID.fromString(it.replace("molde-", "")))!!
        }
        val uuid = UUID.randomUUID()
        val tizadaResult = TizadaResult(
            uuid = uuid,
            url = null,
            tizada = tizada,
            configuration = tizada.configuration,
            bin = tizada.bin,
            parts = moldes,
            materialUtilization = null,
            iterations = null,
            timeoutReached = false,
            createdAt = LocalDateTime.now()
        )
        tizada.results?.add(tizadaResult)
        tizada.state = TizadaState.ERROR
        tizada.updatedAt = LocalDateTime.now()
        tizadaRepository.save(tizada)

        logger.info {
            "Tizada finalizada con error" +
                    " tizadaUUID: ${request.tizadaUUID} and userUUID: ${request.userUUID}"
        }
    }

    fun findTizadaResult(tizadaUUID: UUID): TizadaResult? {
        return tizadaResultRepository.getTizadaResultByTizada(tizadaUUID)
    }
}
