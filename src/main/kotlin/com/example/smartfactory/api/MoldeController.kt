package com.example.smartfactory.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.application.Molde.CreateMoldeRequest
import com.example.smartfactory.application.Molde.CreateMoldeResponse
import com.example.smartfactory.application.Molde.MoldeService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("api/molde")
@Tag(name = "Moldes", description = "Endpoints para moldes")
class MoldeController(private val moldeService: MoldeService) {
    @PostMapping
    fun createMolde(@RequestBody createMoldeRequest: CreateMoldeRequest): GenericResponse<CreateMoldeResponse> {
        val res = moldeService.createMolde(createMoldeRequest)
        return GenericResponse(HttpStatus.CREATED.value(), "ok", res)
    }

    @GetMapping("/{id}")
    fun getMoldeById(@PathVariable("id") id: UUID): GenericResponse<Molde?> {
        return GenericResponse(HttpStatus.OK.value(), "ok", moldeService.getMoldeById(id))
    }

    @GetMapping
    fun getAllMoldes(): GenericResponse<List<Molde?>> {
        return GenericResponse(HttpStatus.OK.value(), "ok", moldeService.getAllMoldes())
    }
}