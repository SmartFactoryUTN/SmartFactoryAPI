package com.example.smartfactory.application.Inventory.Response

import java.util.*

data class GetDetailedGarmentResponse (
    val garmentId: UUID,
    val article: String,
    val description: String,
    val stock: Int,
    val fabricPieces: List<Map<String, Any>>
)
