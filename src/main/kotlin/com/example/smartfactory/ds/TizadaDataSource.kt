package com.example.smartfactory.ds

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.WebTizada.WebTizada

interface TizadaDataSource {
    fun getTizada(id: Long): WebTizada?
    fun createTizada(t: WebTizada)
    fun getAllTizadas(): Collection<WebTizada>
    fun createWebTizada(webTizada: WebTizada): Tizada
}