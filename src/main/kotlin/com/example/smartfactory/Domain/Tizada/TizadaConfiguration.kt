package com.example.smartfactory.Domain.Tizada

import java.util.*

class TizadaConfiguration(
    val id: UUID = UUID.randomUUID(),
    val time: Int = DEFAULT_TIME,
    val percentage: Int,
    val space_between_parts: Int,
    val curve_tolerance: Int,
    val cpu_cores: Int,
    val svg_scale: Int,
    val endpoint_tolerance: Int,
    val merge_common_lines: Boolean,
    val optimization_ratio: Float,
    val ga_population: Int,
    val ga_mutation_rate: Int,
) {
    companion object {
        const val DEFAULT_DISPLAY_UNIT = "mm"
        const val DEFAULT_SPACE_BETWEEN_PARTS = 0
        const val DEFAULT_CURVE_TOLERANCE = 25399 // mantiza
        const val DEFAULT_PART_ROTATIONS = 4
        const val DEFAULT_OPTIMIZATION_TYPE = "GRAVITY"
        const val DEFAULT_CPU_CORES = 4
        const val DEFAULT_SVG_SCALE = 2834 // Import/export
        const val DEFAULT_ENDPOINT_TOLERANCE = 2834 // Import/export
        const val DEFAULT_MERGE_COMMON_LINES = true // laser option
        const val DEFAULT_OPTIMIZATION_RATIO = 0.5 // laser option
        const val DEFAULT_GA_POPULATION = 10 // Meta heuristics
        const val DEFAULT_GA_MUTATION_RATE = 10  // Meta heuristics
        const val DEFAULT_TIME = 10000 // ms
        const val DEFAULT_PERCENTAGE = 10 // aprovechamiento
    }
}

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
