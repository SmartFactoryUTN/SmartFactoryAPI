package com.example.smartfactory.application.Inventory.Request

import java.util.*

data class ConvertFabricRollRequest (
    val tizadaId: UUID,
    val layerMultiplier: Int,
    val rollsQuantity: List<Rolls>
)

data class Rolls(
    val rollId: UUID,
    val quantity: Int,
)