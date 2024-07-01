package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.TizadaResponse
import org.springframework.stereotype.Service

@Service
class TizadaService {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }
}
