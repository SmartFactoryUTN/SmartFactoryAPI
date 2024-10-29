package com.example.smartfactory.application.Inventory.Request

import jakarta.validation.constraints.NotNull


data class CreateColorRequest (
    @field:NotNull(message = "ASD")
    var name: String
)
