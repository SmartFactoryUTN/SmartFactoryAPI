package com.example.smartfactory.application.Inventory.Request

data class UpdateFabricRollRequest (
    val name: String?,
    val stock: Int?,
    val description: String?
)