package com.example.smartfactory.application.Inventory.Response

import com.example.smartfactory.Domain.Inventory.Garment
import com.example.smartfactory.Domain.Tizada.MoldsQuantity
import com.example.smartfactory.application.Tizada.Request.Part

data class GetGarmentResponse (
    val name: String,
    val stock: Int,
    val molds: List<MoldsQuantity>
)