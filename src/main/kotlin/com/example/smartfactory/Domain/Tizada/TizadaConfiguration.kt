package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.application.Tizada.Request.InvokeConfiguration
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*


@Table(name="tizada_configurations")
@Entity
class TizadaConfiguration(
    @Id @Column(name="tizada_configuration_id")
    val id: UUID,
    val time: Int,
    val utilizationPercentage: Int,
    @Transient @JsonIgnore
    val max_iterations: Int = MAX_ITERATIONS,
    @Transient @JsonIgnore
    val space_between_parts: Int = DEFAULT_SPACE_BETWEEN_PARTS, //Este valor no se utiliza, se lee directamente desde la UI
    @Transient @JsonIgnore
    val curve_tolerance: Double = DEFAULT_CURVE_TOLERANCE,  //Este valor no se utiliza, se lee directamente desde la UI
    @Transient @JsonIgnore
    val part_rotations: Int = DEFAULT_PART_ROTATIONS, //Este valor no se utiliza, se lee directamente desde la UI
    @Transient @JsonIgnore
    val ga_population: Int = DEFAULT_GA_POPULATION,  //Este valor no se utiliza, se lee directamente desde la UI
    @Transient @JsonIgnore
    val ga_mutation_rate: Int = DEFAULT_GA_MUTATION_RATE,  //Este valor no se utiliza, se lee directamente desde la UI
    @Transient @JsonIgnore
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
        const val MAX_ITERATIONS = 100 // iteraciones maximas
    }

    fun toInvokeConfiguration(): InvokeConfiguration {
        return InvokeConfiguration(max_iterations, utilizationPercentage, time)
    }
}
