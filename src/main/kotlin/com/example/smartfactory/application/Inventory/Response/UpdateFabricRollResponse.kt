package com.example.smartfactory.application.Inventory.Response

import java.util.*

data class UpdateFabricRollResponse (
    val fabricRollId: UUID,
    val newStock: Int
)