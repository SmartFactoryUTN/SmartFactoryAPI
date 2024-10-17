package com.example.smartfactory.application.Inventory

import com.example.smartfactory.Domain.Inventory.FabricRoll
import com.example.smartfactory.Domain.Inventory.Garment
import com.example.smartfactory.Domain.Inventory.GarmentMold
import com.example.smartfactory.Domain.Inventory.GarmentMoldId
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.MoldsQuantity
import com.example.smartfactory.Exceptions.FabricRollNotFoundException
import com.example.smartfactory.Exceptions.FabricRollOutOfStockException
import com.example.smartfactory.Exceptions.GarmentNotFoundException
import com.example.smartfactory.Exceptions.GarmentOutOfStockException
import com.example.smartfactory.Repository.FabricRollRepository
import com.example.smartfactory.Repository.GarmentRepository
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.application.Inventory.Request.CreateFabricRollRequest
import com.example.smartfactory.application.Inventory.Request.CreateGarmentRequest
import com.example.smartfactory.application.Inventory.Request.UpdateFabricRollRequest
import com.example.smartfactory.application.Inventory.Request.UpdateGarmentRequest
import com.example.smartfactory.application.Inventory.Response.GetGarmentResponse
import com.example.smartfactory.application.Inventory.Response.UpdateFabricRollResponse
import com.example.smartfactory.application.Inventory.Response.UpdateGarmentResponse
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.abs

@Service
class InventoryService(
    private val garmentRepository: GarmentRepository,
    private val moldeRepository: MoldeRepository,
    private val fabricRollRepository: FabricRollRepository
) {
    fun createGarment(createGarmentRequest: CreateGarmentRequest): UUID {
        val garmentUUID = UUID.randomUUID()
        val garment = Garment(
            garmentId = garmentUUID,
            name = createGarmentRequest.name,
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
        val molds: MutableList<GarmentMold> = mutableListOf()
        for (garmentMold in createGarmentRequest.molds) {
            val mold = GarmentMold(GarmentMoldId(garmentUUID, UUID.fromString(garmentMold.uuid)), garmentMold.quantity)
            molds.add(mold)
        }
        garment.garmentMolds = molds
        garmentRepository.save(garment)
        return garmentUUID
    }

    fun createFabricRoll(createFabricRollRequest: CreateFabricRollRequest): UUID {
        val fabricUUID = UUID.randomUUID()
        val fabricRoll = FabricRoll(
            fabricRollId = fabricUUID,
            name = createFabricRollRequest.name,
            color = createFabricRollRequest.color,
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
        fabricRollRepository.save(fabricRoll)
        return fabricUUID
    }

    fun getGarments(): List<GetGarmentResponse> {
        val garments = garmentRepository.findAll().toList()
        val response: MutableList<GetGarmentResponse> = mutableListOf()
        for (garment in garments) {
            val moldQuantities = mutableListOf<MoldsQuantity>()
            for (garmentMold in garment.garmentMolds) {
                val mold = moldeRepository.findMoldeByUuid(garmentMold.garmentMoldId.moldeId)
                val moldQuantity = MoldsQuantity(mold!!, garmentMold.quantity)
                moldQuantities.add(moldQuantity)
            }
            response.add(GetGarmentResponse(
                name = garment.name,
                stock = garment.stock,
                molds = moldQuantities
            ))
        }
        return response
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

    fun updateFabricRoll(fabricRollId: UUID, updateFabricRollRequest: UpdateFabricRollRequest): UpdateFabricRollResponse {
        val fabricRoll = fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) ?: throw FabricRollNotFoundException(
            "No se encontró el rollo de tella con ID $fabricRollId"
        )
        updateFabricRollRequest.name?.let { fabricRoll.name = it }
        updateFabricRollRequest.color?.let { fabricRoll.color = it }
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
}