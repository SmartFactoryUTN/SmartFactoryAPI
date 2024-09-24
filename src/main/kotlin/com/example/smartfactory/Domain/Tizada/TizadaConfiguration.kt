package com.example.smartfactory.Domain.Tizada

import java.util.*

class TizadaConfiguration(
    val id: UUID = UUID.randomUUID(),
    val time: Int = DEFAULT_TIME,
    val percentage: Int = DEFAULT_PERCENTAGE,
    val space_between_parts: Int = DEFAULT_SPACE_BETWEEN_PARTS, //Este valor no se utiliza, se lee directamente desde la UI
    val curve_tolerance: Double = DEFAULT_CURVE_TOLERANCE,  //Este valor no se utiliza, se lee directamente desde la UI
    val part_rotations: Int = DEFAULT_PART_ROTATIONS, //Este valor no se utiliza, se lee directamente desde la UI
    val ga_population: Int = DEFAULT_GA_POPULATION,  //Este valor no se utiliza, se lee directamente desde la UI
    val ga_mutation_rate: Int = DEFAULT_GA_MUTATION_RATE,  //Este valor no se utiliza, se lee directamente desde la UI
    val part_in_part: Boolean = false,  //Este valor no se utiliza, se lee directamente desde la UI
) {
    companion object {
        const val DEFAULT_SPACE_BETWEEN_PARTS = 10
        const val DEFAULT_CURVE_TOLERANCE = 0.3 // mantiza
        const val DEFAULT_PART_ROTATIONS = 4
        const val DEFAULT_GA_POPULATION = 10 // Meta heuristics
        const val DEFAULT_GA_MUTATION_RATE = 10  // Meta heuristics
        const val DEFAULT_TIME = 10000 // ms short medium large
        const val DEFAULT_PERCENTAGE = 60 // aprovechamiento
    }
}

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
