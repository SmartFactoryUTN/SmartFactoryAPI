package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.TizadaResponse
import com.example.smartfactory.ds.TizadaDataSource
import org.springframework.stereotype.Service
import java.util.*

@Service
class TizadaService(tizadaDataSource: TizadaDataSource) {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }

    fun getTizada(id: Long): Any {
        
        return TizadaResponse(id = id, status = "ok", message = "Tizada creada exitosamente")
    }
}
