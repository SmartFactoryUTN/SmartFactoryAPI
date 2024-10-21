package com.example.smartfactory.api
import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@PreAuthorize("permitAll()")
@RequestMapping("api/users")
class UserController(
    @Autowired private val userRepository: UsuarioRepository,
    @Value("\${auth.register.user.secret}") private val authSecret: String
) {

    @PostMapping("/register")
    fun registerUser(
        @RequestBody userRegistrationRequest: UserRegistrationRequest
    ): ResponseEntity<String> {

        // Check if the user already exists by Auth0 ID
        val existingUser = userRepository.findByExternalId(userRegistrationRequest.auth0Id)

        if (existingUser == null) {
            // If not, create and save the new user
            val newUser = Usuario(
                uuid = UUID.randomUUID(),
                externalId = userRegistrationRequest.auth0Id,
                email = userRegistrationRequest.email,
                name = userRegistrationRequest.name,
                parts = null,
                tizadas = null,
                subscription = "PREMIUM"
            )
            userRepository.save(newUser)
            return ResponseEntity("User created successfully", HttpStatus.CREATED)
        } else {
            return ResponseEntity("User already exists", HttpStatus.OK)
        }
    }
}

data class UserRegistrationRequest(
    val auth0Id: String,
    val email: String,
    val name: String
)
