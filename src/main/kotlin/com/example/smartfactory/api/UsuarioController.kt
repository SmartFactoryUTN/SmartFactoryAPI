package com.example.smartfactory.api

import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Repository.UsuarioRepository
import com.example.smartfactory.application.Molde.MoldeService
import com.example.smartfactory.application.Tizada.Response.UsuarioResponse
import com.example.smartfactory.application.Tizada.TizadaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("api/users")
class UsuarioController(
    @Autowired private val usuarioRepository: UsuarioRepository,
    @Autowired private val moldeService: MoldeService,
    private val tizadaService: TizadaService
) {

    @PostMapping("/register")
    fun registerUser(
        @RequestBody userRegistrationRequest: UserRegistrationRequest,
    ): ResponseEntity<String> {

        // Check if the user already exists by Auth0 ID
        val existingUser = usuarioRepository.findByExternalId(userRegistrationRequest.userId)

        if (existingUser == null) {
            // If not, create and save the new user
            val newUser = Usuario(
                uuid = UUID.randomUUID(),
                tizadas = null,
                parts = null,
                name = userRegistrationRequest.name ?: userRegistrationRequest.email,
                email = userRegistrationRequest.email,
                externalId = userRegistrationRequest.userId,
                fabricRolls = null,
                garments = null,
                fabricPieces = null,
                credits = 100
            )
            usuarioRepository.save(newUser)
            return ResponseEntity("User created successfully", HttpStatus.CREATED)
        } else {
            return ResponseEntity("User already exists", HttpStatus.OK)
        }
    }

    @GetMapping("/info")
    fun getUserInfo(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UsuarioResponse<Any>>
    {
        //JWT should provide auth0 user id
        val subject = jwt.claims["sub"] as String
        val user: Usuario =
            usuarioRepository.findByExternalId(subject) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            UsuarioResponse(status = "success", data = mapOf(
                "id" to user.uuid,
                "extId" to user.externalId,
                "name" to user.name,
                "email" to user.email,
                "credits" to user.credits,
                "subscription" to user.subscription
            ))
        )
    }

    @GetMapping("/{uuid}/moldes")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Obtener todos los moldes de un usuario",
        description = "Dado un UUID de un usuario, retorna todos los moldes asociados a ese UUID"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Molde obtenido correctamente"),
            ApiResponse(responseCode = "404", description = "Molde no encontrado"),
            ApiResponse(responseCode = "500")
        ]
    )
    fun getMoldesByUserUUID(
        @PathVariable("uuid") userUUID: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UsuarioResponse<Any>> {

        //JWT should provide auth0 user id
        val subject = jwt.claims["sub"] as String
        val user: Usuario =
            usuarioRepository.getUsuarioByUuid(userUUID) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        if (user.externalId != subject) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")
        }

        val moldes = moldeService.getAllMoldesByOwner(userUUID)

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            UsuarioResponse(status = "success", data = mapOf("moldes" to moldes)
        ))
    }

    @GetMapping("/{uuid}/tizadas")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Obtener todas las tizadas de un usuario",
        description = "Dado un UUID de un usuario, retorna todos las tizadas asociadas a ese UUID"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tizadas obtenidas correctamente"),
            ApiResponse(responseCode = "404", description = "Tizadas no encontradas"),
            ApiResponse(responseCode = "500")
        ]
    )
    fun getTizadasByUserUUID(
        @PathVariable("uuid") userUUID: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UsuarioResponse<Any>>
    {
        validateTenantIsolation(userUUID, jwt)

        val tizadas = tizadaService.getAllTizadasByOwner(userUUID)

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            UsuarioResponse(status = "success", data = mapOf("tizadas" to tizadas)
            ))

    }

    private fun validateTenantIsolation(userUUID: UUID, jwt: Jwt){
        //JWT should provide auth0 user id
        val subject = jwt.claims["sub"] as String
        val user: Usuario =
            usuarioRepository.getUsuarioByUuid(userUUID) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        if (user.externalId != subject) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")
        }
    }
}

data class UserRegistrationRequest(
    val userId: String,
    val email: String,
    val name: String?,
)
