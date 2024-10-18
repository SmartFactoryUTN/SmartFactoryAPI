package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Exceptions.*
import com.example.smartfactory.application.Inventory.InventoryService
import com.example.smartfactory.application.Inventory.Request.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/inventario")
@Tag(name = "Inventario", description = "Endpoints para inventariado")
@Validated
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

    @PostMapping("/rollo/convert")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Convertir un rollo de tela a stock de moldes")
    @ApiResponses(value = [
        ApiResponse(description = "Rollos de tela convertido correctamente", responseCode = "200")
    ])
    fun convertFabricRollsToFabricPieces(
        @RequestBody convertFabricRollRequest: ConvertFabricRollRequest,
    ): ResponseEntity<GenericResponse<Any>> {
        inventoryService.convertFabricRoll(convertFabricRollRequest)
        return try {
            ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(
                    status = "success",
                    data = mapOf(
                        "message" to "Rollos de tela convertidos correctamente!"
                    )
                )
            )
        } catch (e: TizadaNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                GenericResponse(
                    status = "fail",
                    data = mapOf(
                        "exception" to e.javaClass.simpleName,
                        "message" to e.message
                    )
                )
            )
        } catch (e: TizadaInvalidStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(
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

    @PostMapping("/color")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Crear un color")
    @ApiResponses(value = [
        ApiResponse(description = "Color creado correctamente", responseCode = "200")
    ])
    fun createColor(@Valid @RequestBody createColorRequest: CreateColorRequest): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.createColor(createColorRequest)
        return ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(
                    status = "success",
                    data = response
                )
        )
    }

    @GetMapping("/color")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Convertir un rollo de tela a stock de moldes")
    @ApiResponses(value = [
        ApiResponse(description = "Colores obtenidos correctamente", responseCode = "200")
    ])
    fun getColors(): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.getColors()
        return ResponseEntity.status(HttpStatus.OK.value()).body(
            GenericResponse(
                status = "success",
                data = response
            )
        )
    }

    @PostMapping("/fabricPiece/convert")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Convertir piezas de tela a prendas")
    @ApiResponses(value = [
        ApiResponse(description = "Piezas de tela convertidas correctamente", responseCode = "200")
    ])
    fun convertFabricPiecesToGarments(
        @RequestBody convertFabricPiecesRequest: ConvertFabricPiecesRequest
    ): ResponseEntity<GenericResponse<Any>> {
        inventoryService.convertFabricPieces(convertFabricPiecesRequest)
        return try {
            ResponseEntity.status(HttpStatus.OK.value()).body(
                GenericResponse(
                    status = "success",
                    data = mapOf(
                        "message" to "Piezas de tela convertidas correctamente!"
                    )
                )
            )
        } catch (e: GarmentNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                GenericResponse(
                    status = "fail",
                    data = mapOf(
                        "message" to e.message,
                        "exception" to e.javaClass.simpleName
                    )
                )
            )
        } catch (e: FabricPieceNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                GenericResponse(
                    status = "fail",
                    data = mapOf(
                        "message" to e.message,
                        "exception" to e.javaClass.simpleName
                    )
                )
            )
        } catch (e: FabricPieceOutOfStockException) {
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

    @GetMapping("/prenda/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener el detalle de una prenda")
    @ApiResponses(value = [
        ApiResponse(description = "Detalle de prende obtenido correctamente", responseCode = "200")
    ])
    fun getDetailedGarment(@PathVariable id: UUID): ResponseEntity<GenericResponse<Any>> {
        val response = inventoryService.getDetailedGarment(id)
        return ResponseEntity.ok().body(
            GenericResponse(
                status = "success",
                data = response
            )
        )
    }
}