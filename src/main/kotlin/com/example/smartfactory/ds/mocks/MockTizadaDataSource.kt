package com.example.smartfactory.ds.mocks

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaResponse
import com.example.smartfactory.ds.TizadaDataSource
import org.springframework.stereotype.Repository

@Repository
class MockTizadaDataSource: TizadaDataSource {
    override fun getTizada(): Tizada {
        TODO("Not yet implemented")
    }

    override fun createTizada(t: Tizada): TizadaResponse {
        TODO("Not yet implemented")
    }

    override fun getAllTizadas(): Collection<Tizada> {
        TODO("Not yet implemented")
    }
}