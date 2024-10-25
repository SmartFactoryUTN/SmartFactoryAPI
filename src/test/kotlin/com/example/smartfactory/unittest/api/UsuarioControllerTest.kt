package com.example.smartfactory.unittest.api

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Repository.UsuarioRepository
import com.example.smartfactory.api.UserRegistrationRequest
import com.example.smartfactory.api.UsuarioController
import com.example.smartfactory.application.Molde.MoldeService
import com.example.smartfactory.application.Tizada.Response.UsuarioResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UsuarioControllerTest {

    @InjectMockKs
    private lateinit var usuarioController: UsuarioController
    @MockK
    private lateinit var usuarioRepository: UsuarioRepository
    @MockK
    private lateinit var moldeService: MoldeService

    private var objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(){
        usuarioRepository = mockk()
        moldeService = mockk()
        usuarioController = UsuarioController(usuarioRepository, moldeService)
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
        val response = usuarioController.getUser(usuarioUUID, jwt)

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

        assertEquals(expectedResponse.data, response.body?.data)
        assertEquals(HttpStatus.OK, response.statusCode)
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
            usuarioController.getUser(uuid, jwt)
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

        val jwt = mockk<Jwt>()
        every { jwt.claims["sub"] } returns "auth0|1234567890"

        // Act / Assert
        val exception = assertThrows<ResponseStatusException> {
            usuarioController.getUser(uuid, jwt)
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
        val response: ResponseEntity<String> = usuarioController.registerUser(userRegistrationRequest)

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
        val response: ResponseEntity<String> = usuarioController.registerUser(userRegistrationRequest)


        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User already exists", response.body)
        verify(exactly = 0) { usuarioRepository.save(any()) }
    }

    @Test
    fun `should return all moldes for a valid user`() = runBlocking {
        // Mock UUID and JWT
        val userUUID = UUID.randomUUID()
        val jwt = mockk<Jwt>()
        val subject = "auth0|12345"

        // Mock the JWT claims
        every { jwt.claims["sub"] } returns subject

        // Mock the user and the moldes
        val user = Usuario(
            uuid = userUUID,
            tizadas =  null,
            parts = null,
            name = "user",
            externalId = "auth0|12345",
            email = "john.doe@example.com"
        )
        val expectedMoldes = listOf(Molde(UUID.randomUUID(), userUUID, "Molde1", "url1", "desc1", null, true, LocalDateTime.now()))

        // Mock repository and service behavior
        every { usuarioRepository.getUsuarioByUuid(userUUID) } returns user
        every { moldeService.getAllMoldesByOwner(userUUID) } returns expectedMoldes

        // Invoke the controller method
        val response: ResponseEntity<UsuarioResponse<Any>> = usuarioController.getMoldesByUserUUID(userUUID, jwt)

        // Assert the response
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body?.status)
        assertEquals(expectedMoldes, (response.body?.data as Map<*, *>)["moldes"])

        // Verify interactions
        verify(exactly = 1) { usuarioRepository.getUsuarioByUuid(userUUID) }
        verify(exactly = 1) { moldeService.getAllMoldesByOwner(userUUID) }
    }

    @Test
    fun `should throw forbidden when user is not authorized`() = runBlocking {

        // Arrange
        val userUUID = UUID.randomUUID()
        val jwt = mockk<Jwt>()
        val subject = "auth0|54321"  // different from user externalId

        every { jwt.claims["sub"] } returns subject

        val user = Usuario(
            uuid = userUUID,
            tizadas =  null,
            parts = null,
            name = "user",
            externalId = "auth0|12345",
            email = "john.doe@example.com"
        )

        every { usuarioRepository.getUsuarioByUuid(userUUID) } returns user

        // Act/Assert
        assertFailsWith<ResponseStatusException> {
            usuarioController.getMoldesByUserUUID(userUUID, jwt)
        }.also { exception ->
            assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        }

        verify(exactly = 1) { usuarioRepository.getUsuarioByUuid(userUUID) }
        verify(exactly = 0) { moldeService.getAllMoldesByOwner(any()) }
    }

    @Test
    fun `should throw not found when user does not exist`() = runBlocking {

        // Arrange
        val userUUID = UUID.randomUUID()
        val jwt = mockk<Jwt>()
        val subject = "auth0|12345"

        every { jwt.claims["sub"] } returns subject

        every { usuarioRepository.getUsuarioByUuid(userUUID) } returns null

        // Act/Assert
        assertFailsWith<ResponseStatusException> {
            usuarioController.getMoldesByUserUUID(userUUID, jwt)
        }.also { exception ->
            assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        }

        verify(exactly = 1) { usuarioRepository.getUsuarioByUuid(userUUID) }
        verify(exactly = 0) { moldeService.getAllMoldesByOwner(any()) }
    }



}
