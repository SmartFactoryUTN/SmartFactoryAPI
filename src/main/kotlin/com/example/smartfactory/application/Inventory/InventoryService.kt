package com.example.smartfactory.application.Inventory

import com.example.smartfactory.Domain.Inventory.*
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.Exceptions.*
import com.example.smartfactory.Repository.*
import com.example.smartfactory.application.Inventory.Request.*
import com.example.smartfactory.application.Inventory.Response.GetDetailedGarmentResponse
import com.example.smartfactory.application.Inventory.Response.UpdateFabricRollResponse
import com.example.smartfactory.application.Inventory.Response.UpdateGarmentResponse
import com.example.smartfactory.application.Tizada.TizadaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.math.abs

@Service
class InventoryService(
    private val garmentRepository: GarmentRepository,
    private val moldeRepository: MoldeRepository,
    private val fabricRollRepository: FabricRollRepository,
    private val tizadaRepository: TizadaRepository,
    private val fabricPieceRepository: FabricPieceRepository,
    private val fabricColorRepository: FabricColorRepository,
    private val tizadaService: TizadaService
) {
    fun createGarment(createGarmentRequest: CreateGarmentRequest): UUID {
        val garmentUUID = UUID.randomUUID()
        val garmentPieces: MutableList<GarmentPiece> = mutableListOf()
        createGarmentRequest.garmentComponents.forEach {
            val fabricPiece = getFabricPieceIfExistsOrCreate(it.moldeId, it.fabricColorId)
            val garmentPiece = GarmentPiece(
                GarmentPieceId(garmentUUID, fabricPiece.fabricPieceId), it.quantity
            )
            garmentPieces.add(garmentPiece)
        }
        val garment = Garment(
            garmentId = garmentUUID,
            name = createGarmentRequest.name,
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null,
            garmentPieces = garmentPieces
        )
        garmentRepository.save(garment)
        return garmentUUID
    }

    fun getFabricPieceIfExistsOrCreate(moldeId: UUID, fabricColorId: UUID): FabricPiece {
        val molde =
            moldeRepository.findMoldeByUuid(moldeId) ?: throw MoldeNotFoundException("No pudimos obtener este molde")
        val fabricColor = fabricColorRepository.getFabricColorByFabricColorId(fabricColorId)
            ?: throw FabricColorNotFoundException("No pudimos obtener este color")
        val fabricPiece =
            fabricPieceRepository.getFabricPieceByColorAndMolde(fabricColor, molde) ?: fabricPieceRepository.save(
                FabricPiece(
                    fabricPieceId = UUID.randomUUID(),
                    color = fabricColor,
                    molde = molde,
                    stock = 0
                )
            )
        return fabricPiece
    }

    // Itero el metodo de arriba ;)
    fun getFabricPiecesIfExistsOrCreate(garmentComponents: List<GarmentComponents>): MutableList<FabricPiece> {
        val fabricPieces = mutableListOf<FabricPiece>()
        for (garmentComponent in garmentComponents) {
            val fabricPiece =
                this.getFabricPieceIfExistsOrCreate(garmentComponent.moldeId, garmentComponent.fabricColorId)
            fabricPieces.add(fabricPiece)
        }
        return fabricPieces
    }

    fun createFabricRoll(createFabricRollRequest: CreateFabricRollRequest): UUID {
        val fabricUUID = UUID.randomUUID()
        val fabricColor = fabricColorRepository.getFabricColorByFabricColorId(createFabricRollRequest.fabricColorId)
            ?: throw FabricColorNotFoundException("No se encontró el color con ID $fabricUUID")
        val fabricRoll = FabricRoll(
            fabricRollId = fabricUUID,
            name = createFabricRollRequest.name,
            color = fabricColor,
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
        fabricRollRepository.save(fabricRoll)
        return fabricUUID
    }

    fun getGarments(): List<Garment> {
        val garments = garmentRepository.findAll().toList()
        return garments
    }

    fun getFabricRolls(): List<FabricRoll> {
        return fabricRollRepository.findAll().toList()
    }

    fun updateGarment(garmentId: UUID, updateGarmentRequest: UpdateGarmentRequest): UpdateGarmentResponse {
        val garment = garmentRepository.getGarmentByGarmentId(garmentId) ?: throw GarmentNotFoundException(
            "No pudimos encontrar la prenda con ID $garmentId"
        )
        updateGarmentRequest.name?.let { garment.name = it }
        updateGarmentRequest.stock?.let {
            if (it < 0 && it + garment.stock < 0) {
                throw GarmentOutOfStockException("No se pueden restar ${abs(it)} unidades de esta prenda. La cantidad en stock es ${garment.stock}")
            }
            garment.stock += updateGarmentRequest.stock
        }
        garment.updatedAt = LocalDateTime.now()
        garmentRepository.save(garment)
        return UpdateGarmentResponse(garmentId = garment.garmentId, newStock = garment.stock)
    }

    fun updateFabricRoll(
        fabricRollId: UUID,
        updateFabricRollRequest: UpdateFabricRollRequest
    ): UpdateFabricRollResponse {
        val fabricRoll =
            fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) ?: throw FabricRollNotFoundException(
                "No se encontró el rollo de tella con ID $fabricRollId"
            )
        updateFabricRollRequest.name?.let { fabricRoll.name = it }
        updateFabricRollRequest.stock?.let {
            if (it < 0 && it + fabricRoll.stock < 0) {
                throw FabricRollOutOfStockException(
                    "No se pueden restar ${abs(it)} unidades de este rollo. La cantidad en stock es ${fabricRoll.stock}"
                )
            }
            fabricRoll.stock += updateFabricRollRequest.stock
        }
        fabricRoll.updatedAt = LocalDateTime.now()
        fabricRollRepository.save(fabricRoll)
        return UpdateFabricRollResponse(fabricRollId = fabricRoll.fabricRollId, newStock = fabricRoll.stock)
    }

    @Transactional(rollbackFor = [FabricRollOutOfStockException::class])
    fun convertFabricRoll(convertFabricRollRequest: ConvertFabricRollRequest) {
        val tizada = tizadaService.getTizadaByUUID(convertFabricRollRequest.tizadaId)
        if (tizada.state != TizadaState.FINISHED) {
            throw TizadaInvalidStateException("No se puede convertir una tizada que no esté en estado finalizada.")
        }

        for (roll in convertFabricRollRequest.rollsQuantity) {
            val fabricRoll = fabricRollRepository.getFabricRollByFabricRollId(roll.rollId)!!
            if (fabricRoll.stock - roll.quantity < 0) {
                throw FabricRollOutOfStockException("No se pueden convertir ${roll.quantity} rollos, porque tenés en stock ${fabricRoll.stock}")
            }
            for (mold in tizada.parts) {
                val fabricPiece =
                    fabricPieceRepository.getFabricPieceByColorAndMolde(fabricRoll.color, mold.mold) ?: FabricPiece(
                        fabricPieceId = UUID.randomUUID(),
                        color = fabricRoll.color,
                        molde = mold.mold,
                        stock = 0
                    )
                fabricPiece.stock += mold.quantity * roll.quantity * convertFabricRollRequest.layerMultiplier
                fabricPieceRepository.save(fabricPiece)
            }
            fabricRoll.stock -= roll.quantity
            fabricRollRepository.save(fabricRoll)
        }
    }

    @Transactional(rollbackFor = [FabricPieceOutOfStockException::class, FabricPieceNotFoundException::class])
    fun convertFabricPieces(convertFabricPiecesRequest: ConvertFabricPiecesRequest) {
        val garment = garmentRepository.getGarmentByGarmentId(convertFabricPiecesRequest.garmentId)
            ?: throw GarmentNotFoundException("No pudimos encontrar la prenda con ID ${convertFabricPiecesRequest.garmentId}")
        for (garmentPiece in garment.garmentPieces) {
            val fabricPiece =
                fabricPieceRepository.getFabricPieceByFabricPieceId(garmentPiece.garmentPieceId.fabricPieceId)
                    ?: throw FabricPieceNotFoundException("No se encontró el molde con color especificado.")
            /* Al stock de un molde con color le sacamos la cantidad de piezas que pide una prenda multiplicado por la
            cantidad de prendas que el usuario quiere hacer */
            if (fabricPiece.stock - (garmentPiece.quantity * convertFabricPiecesRequest.quantity) < 0) {
                throw FabricPieceOutOfStockException("No se pueden restar ${garmentPiece.quantity * convertFabricPiecesRequest.quantity}, hay ${fabricPiece.stock} en stock")
            }
            fabricPiece.stock -= garmentPiece.quantity * convertFabricPiecesRequest.quantity
            fabricPieceRepository.save(fabricPiece)
        }
        garment.stock += convertFabricPiecesRequest.quantity
        garmentRepository.save(garment)
    }

    fun createColor(createColorRequest: CreateColorRequest): FabricColor {
        val uuid = UUID.randomUUID()
        val fabricColor = FabricColor(
            fabricColorId = uuid,
            name = createColorRequest.name
        )
        return fabricColorRepository.save(fabricColor)
    }

    fun getColors(): List<FabricColor> {
        return fabricColorRepository.findAll().toList()
    }

    fun getDetailedGarment(garmentId: UUID): GetDetailedGarmentResponse {
        val garment = garmentRepository.getGarmentByGarmentId(garmentId)
            ?: throw GarmentNotFoundException("No pudimos encontrar la prenda con ID $garmentId")
        val fabricPieces = mutableListOf<Map<String, Any>>()
        garment.garmentPieces.forEach {
            val fabricPiece = fabricPieceRepository.getFabricPieceByFabricPieceId(it.garmentPieceId.fabricPieceId)!!
            fabricPieces.add(
                mapOf(
                    "fabricPieceId" to fabricPiece.fabricPieceId,
                    "name" to fabricPiece.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    "color" to fabricPiece.color.name,
                    "colorId" to fabricPiece.color.fabricColorId,
                    "moldeId" to fabricPiece.molde.uuid,
                    "url" to fabricPiece.molde.url,
                    "quantity" to it.quantity,
                )
            )
        }
        val response = GetDetailedGarmentResponse(
            name = garment.name,
            stock = garment.stock,
            fabricPieces = fabricPieces.toList()
        )
        return response
    }

    fun getFabricPieces(): Any {
        val fabricPieces = fabricPieceRepository.findAll().toList()
        return fabricPieces
    }
}
