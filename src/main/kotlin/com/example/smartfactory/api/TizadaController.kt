package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.application.Tizada.Request.TizadaRequest
import com.example.smartfactory.application.Tizada.Request.UpdateTizadaRequest
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.application.Tizada.TizadaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tizada")
class TizadaController(private val tizadaService: TizadaService) {
    @PostMapping("/new")
    @Operation(
        summary = "Crea una nueva tizada",
        description = "Persiste una tizada que luego será consumida por el servicio de tizada"
    )
    @ApiResponse(responseCode = "201", description = "Tizada creada correctamente")
    @ApiResponse(responseCode = "400", description = "Tizada inválida")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error")
    fun createTizada(@RequestBody request: TizadaRequest): GenericResponse<TizadaResponse> {
        val tizadaResponse = tizadaService.createTizada(request)
        return GenericResponse(HttpStatus.CREATED.value(), tizadaResponse.status, tizadaResponse)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtiene una tizada",
        description = "Dado un ID, obtiene la información de la tizada"
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada")
    @ApiResponse(responseCode = "404", description = "Tizada no encontrada")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
    fun getTizada(@PathVariable id: UUID) = tizadaService.getTizada(id)

    @PatchMapping("/{id}")
    @Operation(
        summary = "Modificar una tizada",
        description = "Dado un ID, actualiza/modifica la tizada"
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada")
    @ApiResponse(responseCode = "404", description = "Tizada no encontrada")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
    fun updateTizada(@PathVariable id: UUID, @RequestBody request: UpdateTizadaRequest): GenericResponse<Tizada> {
        val tizadaResponse = tizadaService.updateTizada(id, request.name, request.favorite)
        return GenericResponse(HttpStatus.OK.value(), HttpStatus.OK.name, tizadaResponse)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Eliminar una tizada",
        description = "Dado un ID, elimina de forma lógica la tizada"
    )
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada")
    @ApiResponse(responseCode = "404", description = "Tizada no encontrada")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
    fun deleteTizada(@PathVariable id: UUID): Unit = tizadaService.deleteTizada(id)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Obtener todas las tizadas",
        description = "Obtiene todas las tizadas correspondientes a este usuario"
    )
    fun getAllTizadas(): Collection<Tizada> = tizadaService.getAllTizadas()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Solicitud de creación de tizada",
        description = "Solicita a la API que encole una nueva tizada y le envíe al servicio de tizada la petición de " +
                "creación"
    )
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "401", description = "No autorizado para obtener esta tizada")
    @ApiResponse(responseCode = "404", description = "Tizada no encontrada")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error. Intente nuevamente más tarde.")
    fun createWebTizada(@RequestBody request: Tizada): Tizada = tizadaService.queueTizada(request)

    @PostMapping("/notification")
    fun notificationTizada(){

    }
}
