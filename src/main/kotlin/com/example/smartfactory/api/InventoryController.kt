package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Exceptions.FabricRollNotFoundException
import com.example.smartfactory.Exceptions.FabricRollOutOfStockException
import com.example.smartfactory.Exceptions.GarmentNotFoundException
import com.example.smartfactory.Exceptions.GarmentOutOfStockException
import com.example.smartfactory.application.Inventory.InventoryService
import com.example.smartfactory.application.Inventory.Request.CreateFabricRollRequest
import com.example.smartfactory.application.Inventory.Request.CreateGarmentRequest
import com.example.smartfactory.application.Inventory.Request.UpdateFabricRollRequest
import com.example.smartfactory.application.Inventory.Request.UpdateGarmentRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/inventario")
@Tag(name = "Inventario", description = "Endpoints para inventariado")
class InventoryController(
    private val inventoryService: InventoryService
) {
    @PostMapping("/prenda")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva prenda", description = "Dado un nombre y moldes, genera una prenda")
    @ApiResponses(value = [
        ApiResponse(description = "Prenda creada correctamente", responseCode = "201")
    ])
    fun createGarment(@Valid @RequestBody createGarmentRequest: CreateGarmentRequest): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.createGarment(createGarmentRequest)
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
            GenericResponse(
                status = "success",
                data = mapOf(
                    "garmentId" to response
                )
            )
        )
    }

    @PostMapping("/rollo")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo rollo de tela", description = "Dado un nombre y un color, genera un rollo de tela")
    @ApiResponses(value = [
        ApiResponse(description = "Rollo de tela creado correctamente", responseCode = "201")
    ])
    fun createFabricRoll(@Valid @RequestBody createFabricRollRequest: CreateFabricRollRequest): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.createFabricRoll(createFabricRollRequest)
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
            GenericResponse(
                status = "success",
                data = mapOf(
                    "fabricRollId" to response
                )
            )
        )
    }

    @GetMapping("/prenda")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las prendas")
    @ApiResponses(value = [
        ApiResponse(description = "ok", responseCode = "200")
    ])
    fun getGarments(): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.getGarments()
        return ResponseEntity.status(HttpStatus.OK.value()).body(
            GenericResponse(
                status = "success",
                data = response
            )
        )
    }

    @GetMapping("/rollo")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todos los rollos de tela")
    @ApiResponses(value = [
        ApiResponse(description = "ok", responseCode = "200")
    ])
    fun getFabricRolls(): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.getFabricRolls()
        return ResponseEntity.status(HttpStatus.OK.value()).body(
            GenericResponse(
                status = "success",
                data = response
            )
        )
    }

    @PatchMapping("/prenda/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar una prenda")
    @ApiResponses(value = [
        ApiResponse(description = "Prenda actualizada correctamente", responseCode = "200"),
        ApiResponse(description = "Error en los parámetros enviados", responseCode = "400"),
        ApiResponse(description = "No autorizado a obtener esta prneda", responseCode = "401"),
        ApiResponse(description = "Prenda no encontrada", responseCode = "404")
    ])
    fun updateGarment(@RequestBody updateGarmentRequest: UpdateGarmentRequest, @PathVariable("id") id: UUID): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.updateGarment(id, updateGarmentRequest)
        return try {
            ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(
                    status = "success",
                    data = response
                )
            )
        } catch (e: GarmentNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(GenericResponse(
                status = "fail",
                data = mapOf(
                    "exception" to e.javaClass.simpleName,
                    "message" to e.message
                )
            ))
        } catch (e: GarmentOutOfStockException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(GenericResponse(
                status = "fail",
                data = mapOf(
                    "exception" to e.javaClass.simpleName,
                    "message" to e.message
                )
            ))
        }
    }

    @PatchMapping("/rollo/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar un rollo de tela")
    @ApiResponses(value = [
        ApiResponse(description = "Rollo de tela actualizado correctamente", responseCode = "200"),
        ApiResponse(description = "Error en los parámetros enviados", responseCode = "400"),
        ApiResponse(description = "No autorizado a obtener este rollo de tela", responseCode = "401"),
        ApiResponse(description = "Rollo de tela no encontrado", responseCode = "404")
    ])
    fun updateFabricRoll(@RequestBody request: UpdateFabricRollRequest, @PathVariable("id") id: UUID): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.updateFabricRoll(id, request)
        return try {
            ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(
                    status = "success",
                    data = response
                )
            )
        } catch (e: FabricRollNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                GenericResponse(
                    status = "fail",
                    data = mapOf(
                        "exception" to e.javaClass.simpleName,
                        "message" to e.message
                    )
                )
            )
        } catch (e: FabricRollOutOfStockException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(
                GenericResponse(
                    status = "fail",
                    data = mapOf(
                        "exception" to e.javaClass.simpleName,
                        "message" to e.message
                    )
                )
            )
        }
    }
}