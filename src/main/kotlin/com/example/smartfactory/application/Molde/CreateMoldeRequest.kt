package com.example.smartfactory.application.Molde

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Schema(description = "Request object for creating a new Molde")
data class CreateMoldeRequest (
    @Schema(description = "Name of the molde",
        example = "Molde ABC", required = true)
    val name: String,
    @Schema(description = "UUID of the user creating the molde",
        example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    val userUUID: UUID,
    @Schema(description = "Description of the molde",
        example = "This is a sample molde description", required = true)
    val description: String,
    @Schema(description = "SVG file representing the molde",
        type = "string", format = "binary", required = true)
    val svg: MultipartFile
)
