package com.example.smartfactory.unittest.api

import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Repository.UsuarioRepository
import com.example.smartfactory.api.UserController
import com.example.smartfactory.api.UserRegistrationRequest
import com.example.smartfactory.application.Tizada.Response.UsuarioResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.server.ResponseStatusException
import java.util.*

class UserControllerTest {

    @InjectMockKs
    private lateinit var userController: UserController
    @MockK
    private lateinit var usuarioRepository: UsuarioRepository
    private var objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(){
        usuarioRepository = mockk()
        userController = UserController(usuarioRepository)
        objectMapper.findAndRegisterModules()
    }
    @Test
    fun registerUser() {
    }

    @Test
    fun getUser() {
    }

    @Test
    fun `should return user details when ID and JWT sub match`() {
        // Arrange
        val userId = "auth0|1234567890"
        val usuarioUUID = UUID.randomUUID()
        val user = Usuario(
            uuid = usuarioUUID,
            externalId = userId,
            name = "John Doe",
            email = "john.doe@example.com",
            parts = null,
            tizadas = null,
            subscription = "PREMIUM")

        every { usuarioRepository.getUsuarioByUuid(usuarioUUID) } returns user

        // Mock the JWT
        val jwt = mockk<Jwt>()
        every { jwt.claims["sub"] } returns userId

        // Act

        // Call the controller method
        val response = userController.getUser(usuarioUUID, jwt)

        // Assert
        val expectedResponse = UsuarioResponse(
            status = "success",
            data = mapOf(
                "id" to usuarioUUID,
                "extId" to "auth0|1234567890",
                "name" to "John Doe",
                "email" to "john.doe@example.com"
            )
        )

        /*val actualResponse = objectMapper.readValue(response.contentAsString, UsuarioResponse::class.java)

        val nestedData = actualResponse.data as Map<*, *>
        val actualTizadaResponse = nestedData["tizada"] as Map<*, *>*/

        assertEquals(expectedResponse, response.body)
    }

    @Test
    fun `should throw NOT_FOUND when user is not found`() {
        // Mock the service behavior for user not found
        val uuid = UUID.randomUUID()
        every { usuarioRepository.getUsuarioByUuid(uuid) } returns null

        // Mock the JWT
        val jwt = mockk<Jwt>()
        every { jwt.claims["sub"] } returns "auth0|1234567890"

        // Assert that a NOT_FOUND exception is thrown
        val exception = assertThrows<ResponseStatusException> {
            userController.getUser(uuid, jwt)
        }

        // Assert the exception status is 404 NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }

    @Test
    fun `should throw FORBIDDEN when JWT sub does not match userId`() {
        // Arrange
        val uuid = UUID.randomUUID()
        val user = Usuario(
            uuid = uuid,
            externalId = "auth0|9999999999",
            name = "John Doe",
            email = "john.doe@example.com",
            parts = null,
            tizadas =  null
        )
        every { usuarioRepository.getUsuarioByUuid(uuid) } returns user

        // Mock the JWT
        val jwt = mockk<Jwt>()
        every { jwt.claims["sub"] } returns "auth0|1234567890"

        // Act / Assert
        val exception = assertThrows<ResponseStatusException> {
            userController.getUser(uuid, jwt)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
    }

    @Test
    fun `should create new user when user does not exist`() {
        // Arrange
        val userRegistrationRequest = UserRegistrationRequest(
            userId = "auth0|1234567890",
            email = "john.doe@example.com",
            name = "John Doe"
        )

        every { usuarioRepository.findByExternalId(userRegistrationRequest.userId) } returns null // No user exists
        every { usuarioRepository.save(any()) } answers {firstArg()}

        // Act
        val response: ResponseEntity<String> = userController.registerUser(userRegistrationRequest)

        // Assert
        verify { usuarioRepository.save(any()) }
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("User created successfully", response.body)
    }

    @Test
    fun `should return user already exists when user exists`() {
        // Arrange
        val userRegistrationRequest = UserRegistrationRequest(
            userId = "auth0|1234567890",
            email = "john.doe@example.com",
            name = "John Doe"
        )

        val existingUser = Usuario(
            uuid = UUID.randomUUID(),
            externalId = userRegistrationRequest.userId,
            email = "john.doe@example.com",
            name = "John Doe",
            parts = null,
            tizadas = null
        )

        every { usuarioRepository.findByExternalId(userRegistrationRequest.userId) } returns existingUser

        // Act
        val response: ResponseEntity<String> = userController.registerUser(userRegistrationRequest)


        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User already exists", response.body)
        verify(exactly = 0) { usuarioRepository.save(any()) }
    }


}
