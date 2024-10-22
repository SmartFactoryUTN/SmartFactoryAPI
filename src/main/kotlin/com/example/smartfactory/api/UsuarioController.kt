package com.example.smartfactory.api

import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Repository.UsuarioRepository
import com.example.smartfactory.application.Tizada.Response.UsuarioResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@PreAuthorize("permitAll()")
@RequestMapping("api/users")
class UserController(
    @Autowired private val usuarioRepository: UsuarioRepository,
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
                externalId = userRegistrationRequest.userId,
                email = userRegistrationRequest.email,
                name = userRegistrationRequest.name ?: userRegistrationRequest.email,
                parts = null,
                tizadas = null
            )
            usuarioRepository.save(newUser)
            return ResponseEntity("User created successfully", HttpStatus.CREATED)
        } else {
            return ResponseEntity("User already exists", HttpStatus.OK)
        }
    }

    @GetMapping("/{id}")
    fun getUser(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UsuarioResponse<Any>>
    {
        //JWT should provide auth0 user id
        val subject = jwt.claims["sub"] as String
        val user: Usuario =
            usuarioRepository.getUsuarioByUuid(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        if (user.externalId != subject) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this resource")
        }

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            UsuarioResponse(status = "success", data = mapOf(
                "id" to user.uuid,
                "extId" to user.externalId,
                "name" to user.name,
                "email" to user.email
            )
            )
        )
    }
}

data class UserRegistrationRequest(
    val userId: String,
    val email: String,
    val name: String?,
)
