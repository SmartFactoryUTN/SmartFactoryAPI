package com.example.smartfactory.application.Inventory

import com.example.smartfactory.Domain.Inventory.*
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.Domain.Usuarios.Usuario
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

@Service
class InventoryService(
    private val garmentRepository: GarmentRepository,
    private val moldeRepository: MoldeRepository,
    private val fabricRollRepository: FabricRollRepository,
    private val fabricPieceRepository: FabricPieceRepository,
    private val fabricColorRepository: FabricColorRepository,
    private val tizadaService: TizadaService
) {
    fun createGarment(createGarmentRequest: CreateGarmentRequest, user: Usuario): UUID {
        val garmentUUID = UUID.randomUUID()
        val garmentPieces: MutableList<GarmentPiece> = mutableListOf()
        createGarmentRequest.garmentComponents.forEach {
            val fabricPiece = getFabricPieceIfExistsOrCreate(it.moldeId, it.fabricRollId, user)
            val garmentPiece = GarmentPiece(
                GarmentPieceId(garmentUUID, fabricPiece.fabricPieceId), it.quantity
            )
            garmentPieces.add(garmentPiece)
        }
        val garment = Garment(
            garmentId = garmentUUID,
            article = createGarmentRequest.article,
            description = createGarmentRequest.description,
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null,
            garmentPieces = garmentPieces,
            user = user
        )
        garmentRepository.save(garment)
        return garmentUUID
    }

    fun getFabricPieceIfExistsOrCreate(moldeId: UUID, fabricRollId: UUID, user: Usuario): FabricPiece {
        val molde =
            moldeRepository.findMoldeByUuid(moldeId) ?: throw MoldeNotFoundException("No pudimos obtener este molde")
        val fabricRoll = fabricRollRepository.getFabricRollByFabricRollId(fabricRollId)
            ?: throw FabricColorNotFoundException("No pudimos obtener el rollo con id $fabricRollId")
        val fabricPiece =
            fabricPieceRepository.getFabricPieceByFabricRollAndMolde(fabricRoll, molde) ?: fabricPieceRepository.save(
                FabricPiece(
                    fabricPieceId = UUID.randomUUID(),
                    fabricRoll = fabricRoll,
                    molde = molde,
                    stock = 0,
                    user = user,
                    createdAt = LocalDateTime.now()
                )
            )
        return fabricPiece
    }

    // Itero el metodo de arriba ;)
//    fun getFabricPiecesIfExistsOrCreate(garmentComponents: List<GarmentComponents>): MutableList<FabricPiece> {
//        val fabricPieces = mutableListOf<FabricPiece>()
//        for (garmentComponent in garmentComponents) {
//            val fabricPiece =
//                this.getFabricPieceIfExistsOrCreate(garmentComponent.moldeId, garmentComponent.fabricColorId)
//            fabricPieces.add(fabricPiece)
//        }
//        return fabricPieces
//    }

    fun createFabricRoll(createFabricRollRequest: CreateFabricRollRequest, user: Usuario): UUID {
        val fabricUUID = UUID.randomUUID()
        val fabricColor = fabricColorRepository.getFabricColorByFabricColorId(createFabricRollRequest.fabricColorId)
            ?: throw FabricColorNotFoundException("No se encontró el color con ID $fabricUUID")
        val fabricRoll = FabricRoll(
            fabricRollId = fabricUUID,
            name = createFabricRollRequest.name,
            description = createFabricRollRequest.description,
            color = fabricColor,
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null,
            user = user
        )
        fabricRollRepository.save(fabricRoll)
        return fabricUUID
    }

    fun getGarments(userUUID: UUID): List<Garment> {
        val garments = garmentRepository.findGarmentsByUserUuidAndActive(userUUID)
        return garments
    }

    fun getFabricRolls(userUUID: UUID): List<FabricRoll> {
        return fabricRollRepository.findFabricRollsByUserUuidAndActive(userUUID)
    }

    fun updateGarment(garmentId: UUID, updateGarmentRequest: UpdateGarmentRequest): UpdateGarmentResponse {
        val garment = garmentRepository.getGarmentByGarmentId(garmentId) ?: throw GarmentNotFoundException(
            "No pudimos encontrar la prenda con ID $garmentId"
        )
        updateGarmentRequest.article?.let { garment.article = it }
        updateGarmentRequest.description?.let { garment.description = it }
        updateGarmentRequest.stock?.let {
            if (it < 0) {
                throw GarmentOutOfStockException("El stock ingresado (${updateGarmentRequest.stock}) debe ser una cantidad positiva")
            }
            garment.stock = updateGarmentRequest.stock
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
        updateFabricRollRequest.description?.let { fabricRoll.description }
        updateFabricRollRequest.stock?.let {
            if (it < 0) {
                throw FabricRollOutOfStockException(
                    "El stock ingresado (${updateFabricRollRequest.stock}) debe ser una cantidad positiva"
                )
            }
            fabricRoll.stock = updateFabricRollRequest.stock
        }
        fabricRoll.updatedAt = LocalDateTime.now()
        fabricRollRepository.save(fabricRoll)
        return UpdateFabricRollResponse(fabricRollId = fabricRoll.fabricRollId, newStock = fabricRoll.stock)
    }

    @Transactional(rollbackFor = [FabricRollOutOfStockException::class])
    fun convertFabricRoll(convertFabricRollRequest: ConvertFabricRollRequest, usuario: Usuario) {
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
                    fabricPieceRepository.getFabricPieceByFabricRollAndMolde(fabricRoll, mold.mold) ?: FabricPiece(
                        fabricPieceId = UUID.randomUUID(),
                        fabricRoll = fabricRoll,
                        molde = mold.mold,
                        stock = 0,
                        user = usuario,
                        createdAt = LocalDateTime.now()
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

    fun createColor(createColorRequest: CreateColorRequest, user: Usuario): FabricColor {
        val uuid = UUID.randomUUID()
        val fabricColor = FabricColor(
            fabricColorId = uuid,
            name = createColorRequest.name,
            user = user
        )
        return fabricColorRepository.save(fabricColor)
    }

    fun getColors(user: Usuario): List<FabricColor> {
        return fabricColorRepository.getFabricColorsByUserUuid(user.uuid)
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
                    "fabricRoll" to fabricPiece.fabricRoll,
                    "moldeId" to fabricPiece.molde.uuid,
                    "url" to fabricPiece.molde.url,
                    "quantity" to it.quantity,
                )
            )
        }
        val response = GetDetailedGarmentResponse(
            article = garment.article,
            description = garment.description,
            stock = garment.stock,
            fabricPieces = fabricPieces.toList()
        )
        return response
    }

    fun getFabricPieces(user: UUID): Any {
        val fabricPieces = fabricPieceRepository.findAllByUserUuidAndActive(user)
        return fabricPieces
    }

    fun updateFabricPiece(id: UUID, updateGarmentRequest: UpdateFabricPieceRequest): Any {
        val fabricPiece = fabricPieceRepository.getFabricPieceByFabricPieceId(id) ?: throw FabricPieceNotFoundException(
            "No pudimos encontrar el molde cortado con ID $id"
        )
        updateGarmentRequest.name?.let { fabricPiece.name = it }
        updateGarmentRequest.stock?.let {
            if (it < 0) {
                throw FabricPieceOutOfStockException("El stock ingresado (${updateGarmentRequest.stock}) debe ser una cantidad positiva")
            }
            fabricPiece.stock = it
        }
        fabricPieceRepository.save(fabricPiece)
        return "Molde cortado ${fabricPiece.name} actualizado correctamente"
    }

    fun deleteFabricRoll(id: UUID) {
        val fabricRoll = fabricRollRepository.getFabricRollByFabricRollId(id)
            ?: throw FabricRollNotFoundException("No pudimos encontrar el rollo de tela con ID $id")
        fabricRoll.active = false
        fabricRoll.deletedAt = LocalDateTime.now()
        fabricRollRepository.save(fabricRoll)
    }

    fun deleteFabricPiece(id: UUID) {
        val fabricPiece = fabricPieceRepository.getFabricPieceByFabricPieceId(id) ?: throw FabricPieceNotFoundException(
            "No pudimos encontrar el molde cortado con ID $id"
        )
        fabricPiece.deletedAt = LocalDateTime.now()
        fabricPiece.active = false
        fabricPieceRepository.save(fabricPiece)
    }

    fun deleteGarment(id: UUID) {
        val garment = garmentRepository.getGarmentByGarmentId(id) ?: throw GarmentNotFoundException(
            "No pudimos encontrar la prenda con ID $id"
        )
        garment.deletedAt = LocalDateTime.now()
        garment.active = false
        garmentRepository.save(garment)
    }
}
