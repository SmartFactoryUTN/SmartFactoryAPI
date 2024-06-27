package com.example.smartfactory.api

import com.example.smartfactory.Domain.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.Response.TizadaResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tizada")
class TizadaController(private val tizadaService: TizadaService) {
    @PostMapping("")
    @Operation(summary = "Crea una nueva tizada", description = "Persiste una tizada que luego será consumida por el servicio de tizada")
    @ApiResponse(responseCode = "201", description = "Tizada creada correctamente")
    @ApiResponse(responseCode = "400", description = "Tizada inválida")
    @ApiResponse(responseCode = "500", description = "Ocurrió un error")
    fun createTizada(@RequestBody request: TizadaRequest): ResponseEntity<TizadaRequest> {
        val response = tizadaService.createTizada(request)
        return ResponseEntity(request, HttpStatus.CREATED);
    }
}

@Service
class TizadaService {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        // Aquí se realizaría la lógica de negocio para crear una Tizada
        // Por ejemplo, podrías guardar la Tizada en una base de datos y generar un ID
        val generatedId = 1L // Esto es un ejemplo; normalmente, el ID sería generado por la base de datos

        return TizadaResponse(id = generatedId, status= "ok", message = "Tizada creada exitosamente")
    }
}
