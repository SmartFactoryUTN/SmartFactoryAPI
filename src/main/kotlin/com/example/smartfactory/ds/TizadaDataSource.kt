package com.example.smartfactory.ds

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaResponse

interface TizadaDataSource {
    fun getTizada(): Tizada
    fun createTizada(t: Tizada): TizadaResponse
    fun getAllTizadas(): Collection<Tizada>
}