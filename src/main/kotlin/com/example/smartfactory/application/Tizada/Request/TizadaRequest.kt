package com.example.smartfactory.application.Tizada.Request

import com.example.smartfactory.Domain.Tizada.TipoTizada
import com.example.smartfactory.Domain.Tizada.Tizada
import jakarta.validation.constraints.Min
import java.util.Date
import java.util.UUID

class TizadaRequest(
    val date: Date,
    val tizada: Tizada
)


class CreateTizadaRequest(
    @Min(value = 0, message = "El ancho debe ser un número mayor a 0")
    val width: Int,
    @Min(value = 0, message = "La altura debe ser un número mayor a 0")
    val height: Int,
    val name: String,
    val tizadaType: TipoTizada,
    @Min(value = 0, message = "El porcentaje de desperdicio debe ser mayor a 0.")
    val wastePercentage: Int,
    @Min(value = 0, message = "El tiempo máximo debe ser mayor a 0.")
    val maxTime: Int,
    val molds: List<TizadaPartsRequest>
)

class TizadaPartsRequest(
    val uuid: UUID,
    @Min(value = 0, message = "La cantidad de moldes debe ser mayor a 0.")
    val quantity: Int,
)

/*
{
    "configuration": {
        "time": 10,
        "percentage": 10,
    },
    "mold": [
        {
        "id": {{$randomUUID}},
        "cantidad": 10
        }
    ]
}*/
