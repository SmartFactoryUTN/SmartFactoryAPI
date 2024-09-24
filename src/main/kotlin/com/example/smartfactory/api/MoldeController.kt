package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.application.Molde.CreateMoldeRequest
import com.example.smartfactory.application.Molde.CreateMoldeResponse
import com.example.smartfactory.application.Molde.MoldeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("api/molde")
@Tag(name = "Moldes", description = "Endpoints para moldes")
class MoldeController(private val moldeService: MoldeService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Crear un nuevo molde",
        description = "Dado un nombre, una descripción y un svg (a definir, por ahora un string): crea ese molde"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Molde creado correctamente"),
            ApiResponse(responseCode = "400", description = "Molde inválido"),
            ApiResponse(responseCode = "404", description = "No autorizado para realizar esta operación"),
            ApiResponse(responseCode = "500", description = "")
        ]
    )
    fun createMolde(@RequestBody createMoldeRequest: CreateMoldeRequest): GenericResponse<Molde> {
        val res = moldeService.createMolde(createMoldeRequest)
        return GenericResponse(HttpStatus.CREATED.name, res)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Obtener molde por UUID",
        description = "Dado un UUID, devuelve una entidad Molde"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Molde obtenido correctamente"),
            ApiResponse(responseCode = "404", description = "Molde no encontrado "),
            ApiResponse(responseCode = "500")
        ]
    )
    fun getMoldeById(@PathVariable("id") id: UUID): GenericResponse<Molde?> {
        return GenericResponse( HttpStatus.OK.name, moldeService.getMoldeById(id))
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Obtener todos los moldes",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Moldes obtenidos correctamente"),
            ApiResponse(responseCode = "500")
        ]
    )
    fun getAllMoldes(): GenericResponse<List<Molde?>> {
        return GenericResponse(HttpStatus.OK.name, moldeService.getAllMoldes())
    }
}