package com.example.smartfactory.application.Inventory.Request

import io.swagger.v3.oas.annotations.media.Schema
import org.jetbrains.annotations.NotNull
import java.util.*

@Schema(description = "Request para crear una prenda")
data class CreateGarmentRequest (
    @field:NotNull(value = "Name cannot be null")
    @Schema(
        description = "Artículo de la prenda",
        required = true,
        type = "string",
        example = "Art. 01"
    )
    val article: String,
    val description: String,
    @field:NotNull(value = "Molds cannot be null")
    @Schema(
        description = "Moldes de la prenda. ID del molde + cantidad de veces que aparece ese molde en la prenda",
        required = true,
        type = "List<GarmentComponents>"
    )
    val garmentComponents: List<GarmentComponents>
)

data class GarmentComponents(
    val moldeId: UUID,
    val quantity: Int,
    val fabricRollId: UUID
)