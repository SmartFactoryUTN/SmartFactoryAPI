package com.example.smartfactory.ds.mocks

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.WebTizada.WebTizada
import com.example.smartfactory.ds.TizadaDataSource
import org.springframework.stereotype.Repository

@Repository
class MockTizadaDataSource: TizadaDataSource {

    val tizadas = emptyList<WebTizada>()

    override fun getTizada(id: Long): WebTizada? {
        return tizadas.find { it: WebTizada -> it.id == id }
    }

    override fun createTizada(t: WebTizada) {
        TODO("Not yet implemented")
    }

    override fun getAllTizadas(): Collection<WebTizada> {
        return tizadas
    }

    override fun createWebTizada(webTizada: WebTizada): Tizada {
        TODO("Not yet implemented")
    }
}
