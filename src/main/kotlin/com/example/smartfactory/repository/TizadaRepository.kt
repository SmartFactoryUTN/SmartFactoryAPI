package com.example.smartfactory.repository

import com.example.smartfactory.Domain.Tizada.Tizada
import java.util.*


interface TizadaRepository {
    fun getTizada(id: UUID): Tizada?
    fun createTizada(t: Tizada)
    fun getAllTizadas(): Collection<Tizada>
    fun queueTizada(tizada: Tizada): Tizada
}