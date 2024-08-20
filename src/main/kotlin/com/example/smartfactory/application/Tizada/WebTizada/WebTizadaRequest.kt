package com.example.smartfactory.Domain.WebTizada

import com.example.smartfactory.Domain.Molde.Molde

data class WebTizadaRequest(
    val anchoMesa: Int,
    val largoMesa: Int,
    val tipoTizada: Pair<TipoTizada, Int>,
    val moldes: Collection<Molde>
)