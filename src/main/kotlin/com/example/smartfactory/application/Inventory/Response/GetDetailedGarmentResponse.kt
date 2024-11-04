package com.example.smartfactory.application.Inventory.Response

data class GetDetailedGarmentResponse (
    val article: String,
    val description: String,
    val stock: Int,
    val fabricPieces: List<Map<String, Any>>
)
