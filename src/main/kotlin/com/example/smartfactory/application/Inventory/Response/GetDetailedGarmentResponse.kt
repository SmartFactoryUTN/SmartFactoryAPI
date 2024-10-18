package com.example.smartfactory.application.Inventory.Response

data class GetDetailedGarmentResponse (
    val name: String,
    val stock: Int,
    val fabricPieces: List<Map<String, Any>>
)
