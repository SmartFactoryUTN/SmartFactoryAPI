package com.example.smartfactory.application.Inventory.Request

import java.util.*

data class ConvertFabricPiecesRequest (
    val garmentId: UUID,
    val quantity: Int
)