package com.example.smartfactory.application.Inventory.Request

data class UpdateFabricRollRequest (
    val name: String?,
    val color: String?,
    val stock: Int?
)