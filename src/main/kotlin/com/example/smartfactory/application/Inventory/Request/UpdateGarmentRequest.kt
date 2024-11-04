package com.example.smartfactory.application.Inventory.Request

data class UpdateGarmentRequest(
    val article: String?,
    val description: String?,
    val stock: Int?
)