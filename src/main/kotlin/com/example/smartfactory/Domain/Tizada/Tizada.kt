package com.example.smartfactory.Domain.Tizada

import java.util.UUID

//data class User(val name: String, val age: Int)

class Tizada(
    val uuid: UUID,
    val configuration: TizadaConfiguration,
    val parts: ArrayList<TizadaPart>,
    val spaceBetweenParts: Int,
    val curveTolerance: Int,
    val partRotation: Int
)

/*
{
    "id": {{$randomUUID}},
    "configuration": {
    "time": 10,
    "percentaje": 10,
    "space_between_parts": 0,
    "curve_tolerance": 25399,
    "part_rotations": 4,
    "cpu_cores": 4,
    "svg_scale": 2,
    "endpoint_tolerance": 12699,
    "merge_common_lines": true,
    "optimization_ratio": 0.5,
    "ga_population": 10,
    "ga_mutation_rate": 10
},
    "parts": [
    {
        "id": {{$randomUUID}},
        "cantidad": 10
    }
    ]
}*/
