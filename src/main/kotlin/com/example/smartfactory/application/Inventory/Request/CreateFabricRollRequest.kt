package com.example.smartfactory.application.Inventory.Request

import org.jetbrains.annotations.NotNull

data class CreateFabricRollRequest (
    @field:NotNull(value = "Name cannot be null or blank")
    val name: String,
    @field:NotNull(value = "Color cannot be null or blank")
    val color: String
)