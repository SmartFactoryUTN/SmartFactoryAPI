package com.example.smartfactory.application.Inventory.Response

import java.util.*

data class UpdateGarmentResponse (
    val garmentId: UUID,
    val newStock: Int
)