package com.example.smartfactory.api

import com.example.smartfactory.Domain.Tizada.GenericResponse
import com.example.smartfactory.Domain.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaResponse
import com.example.smartfactory.application.Tizada.TizadaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tizada")
class TizadaController(private val tizadaService: TizadaService) {
    // Le agrego este mapping para diferenciarlo del post que hace el front a la API
    @PostMapping("/ext")
    @Operation(
        summary = "Crea una nueva tizada",
        description = "Persiste una tizada que luego será consumida por el servicio de tizada"
    )
    @ApiResponse(responseCode = "201", description = "Tizada creada correctamente")
    @ApiResponse(responseCode = "400", description = "Tizada inválida")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error")
    fun createTizada(@RequestBody request: TizadaRequest): GenericResponse<TizadaResponse> {
        val tizadaResponse = tizadaService.createTizada(request)
        return GenericResponse(HttpStatus.CREATED.value(), tizadaResponse.status, tizadaResponse);
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
    fun getTizada(@PathVariable id: Long) = tizadaService.getTizada(id)

    @PatchMapping("/{id}")
    fun updateTizada(@PathVariable id: Long, @RequestBody request: TizadaRequest): GenericResponse<Tizada> {
        val tizadaResponse = tizadaService.updateTizada(id, request)
        return GenericResponse(HttpStatus.OK.value(), tizadaResponse.status, tizadaResponse.body())
    }

    @DeleteMapping("/{id}")
    fun deleteTizada(@PathVariable id: Long): GenericResponse<Void> {
        val response = tizadaService.deleteTizada(id)
        return GenericResponse(HttpStatus.NO_CONTENT.value(), response.status, response)
    }

    @GetMapping("")
    fun getAllTizadas() {
        return tizadaService.getAllTizadas()
    }
}
