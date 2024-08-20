package com.example.smartfactory.application.Tizada.Request

import com.example.smartfactory.Domain.Tizada.Tizada
import java.util.Date

class TizadaRequest(
    val date: Date,
    val tizada: Tizada
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
