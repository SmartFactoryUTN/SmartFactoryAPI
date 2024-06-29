package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.TizadaResponse
import org.springframework.stereotype.Service

@Service
class TizadaService {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        // Aquí se realizaría la lógica de negocio para crear una Tizada
        // Por ejemplo, podrías guardar la Tizada en una base de datos y generar un ID
        val generatedId = 1L // Esto es un ejemplo; normalmente, el ID sería generado por la base de datos

        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }
}
