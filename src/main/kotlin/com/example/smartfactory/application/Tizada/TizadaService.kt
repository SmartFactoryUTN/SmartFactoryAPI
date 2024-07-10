package com.example.smartfactory.application.Tizada

import com.example.smartfactory.Domain.Tizada.Request.TizadaRequest
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaResponse
import com.example.smartfactory.Domain.WebTizada.WebTizada
import com.example.smartfactory.Domain.WebTizada.WebTizadaResponse
import com.example.smartfactory.ds.TizadaDataSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class TizadaService(private val tizadaDataSource: TizadaDataSource) {
    fun createTizada(request: TizadaRequest): TizadaResponse {
        val generatedId = 1L
        return TizadaResponse(id = generatedId, status = "ok", message = "Tizada creada exitosamente")
    }


    // TODO: conviene separar esta lógica de acá abajo de la de arriba?
    fun getTizada(id: Long): WebTizada? {
        return tizadaDataSource.getTizada(id)
    }

    fun updateTizada(id: Long, name: String, favorite: Boolean): WebTizada {
        val tizada = this.getTizada(id)
        return WebTizada(
            id,
            tizada!!.uuid,
            tizada.tableWidth,
            tizada.tableLength,
            tizada.result,
            name,
            favorite,
            tizada.active
        )
    }

    fun deleteTizada(id: Long) {
        val tizada = this.getTizada(id)
        tizada!!.active = false
    }

    fun getAllTizadas(): Collection<WebTizada> {
        return tizadaDataSource.getAllTizadas()
    }

    fun createWebTizada(webTizada: WebTizada): Tizada {
        return tizadaDataSource.createWebTizada(webTizada)
    }
}
