package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.application.Inventory.InventoryService
import com.example.smartfactory.application.Inventory.Request.CreateFabricRollRequest
import com.example.smartfactory.application.Inventory.Request.CreateGarmentRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
}