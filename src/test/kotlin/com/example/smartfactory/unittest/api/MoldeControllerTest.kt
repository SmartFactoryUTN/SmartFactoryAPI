package com.example.smartfactory.unittest.api

import com.example.smartfactory.Domain.GenericResponse
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Exceptions.MoldeNotFoundException
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.api.MoldeController
import com.example.smartfactory.application.Molde.CreateMoldeRequest
import com.example.smartfactory.application.Molde.MoldeService
import com.example.smartfactory.application.Molde.UpdateMoldeRequest
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.integration.LambdaService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class MoldeControllerTest {

    lateinit var mvc: MockMvc

    @InjectMockKs
    lateinit var moldeController: MoldeController

    @MockK
    lateinit var moldeService: MoldeService

    @MockK
    private lateinit var lambdaService: LambdaService

    @MockK
    private lateinit var moldeRepository: MoldeRepository


    private var objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(){
        mvc = MockMvcBuilders.standaloneSetup(moldeController).build()
        objectMapper.findAndRegisterModules()

        moldeService = mockk()
        lambdaService = mockk()
        moldeRepository = mockk()

        moldeController = MoldeController(moldeService)
    }

    fun createMolde() {
        //Arrange
        val moldeUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val svgFile = MockMultipartFile("svg", "test.svg", "image/svg+xml", "<svg></svg>".byteInputStream())

        coEvery { moldeService.createMolde(any()) } returns Molde(
            uuid = moldeUUID,
            owner = userUUID,
            "Un molde",
            "https://servicio-de-tizada.s3.us-east-2.amazonaws.com/moldeA.svg",
            "Un molde",
            1.0,
            true,
            LocalDateTime.now())


        //Act
        val response = mvc.perform(
            multipart("/api/molde/create")
                .file("svg", svgFile.bytes)
                .param("name", "Un molde")
                .param("userUUID", userUUID.toString())
                .param("description", "Un molde")
        )
            .andExpect(status().isCreated)
            .andReturn().response

        val actualResponse = objectMapper.readValue(response.contentAsString, TizadaResponse::class.java)

        //Assert
        assertNotNull(actualResponse)
    }

    @Test
    fun `should create molde successfully`() = runBlocking {
        // Arrange
        val name = "Test Molde"
        val userUUID = UUID.randomUUID()
        val description = "This is a test molde"
        val svgFile = MockMultipartFile("file", "test.svg", "image/svg+xml", "dummy data".toByteArray())

        val createMoldeRequest = CreateMoldeRequest(
            name = name,
            userUUID = userUUID,
            description = description,
            svg = svgFile
        )

        val generatedUUID = UUID.randomUUID()
        val svgUrl = "https://example.com/test.svg"

        val expectedMolde = Molde(
            uuid = generatedUUID,
            owner = userUUID,
            name = name,
            url = svgUrl,
            description = description,
            area = null,
            active = true,
            createdAt = LocalDateTime.now()
        )

        coEvery { moldeService.createMolde(createMoldeRequest) } returns expectedMolde
        coEvery { moldeService.isSvgFile(any()) } returns true

        // Act
        val response: ResponseEntity<TizadaResponse<Any>> = moldeController.createMolde(
            name = name,
            userUUID = userUUID,
            description = description,
            svg = svgFile
        )

        // Assert
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("success", response.body?.status)
        assertEquals(expectedMolde, (response.body?.data as Map<*, *>)["molde"])

        coVerify(exactly = 1) { moldeService.createMolde(createMoldeRequest) }
    }

    @Test
    fun `test getMoldeById successful`() {

        // Arrange
        val moldeUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val molde = Molde(
            uuid = moldeUUID,
            owner = userUUID,
            name = "Test Molde",
            url = "",
            description = "test",
            active = true,
            createdAt = LocalDateTime.now(),
            area = 10.0
        )

        every { moldeService.getMoldeById(moldeUUID) } returns molde

        // Act
        val response = moldeController.getMoldeById(moldeUUID)

        // Assert
        assertEquals("success", response.status)
        assertEquals(molde, response.data)

        verify { moldeService.getMoldeById(moldeUUID) }
    }

    @Test
    fun `test updateMolde successful`() {

        // Arrange
        val moldeUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()

        val molde = Molde(
            uuid = moldeUUID,
            owner = userUUID,
            name = "Test Molde",
            url = "",
            description = "test",
            active = true,
            createdAt = LocalDateTime.now(),
            area = 10.0
        )
        val updateRequest = UpdateMoldeRequest(
            name = "Updated Name",
            description =  "Updated Description"
        )

        every { moldeService.updateMolde(moldeUUID, updateRequest) } returns molde

        // Act
        val response = moldeController.updateMolde(updateRequest, moldeUUID)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body?.status)

        verify { moldeService.updateMolde(moldeUUID, updateRequest) }
    }

    @Test
    fun `test updateMolde with MoldeNotFoundException`() {
        // Arrange
        val moldeUUID = UUID.randomUUID()
        val updateRequest = UpdateMoldeRequest("Updated Name", "Updated Description")

        every { moldeService.updateMolde(moldeUUID, updateRequest) } throws MoldeNotFoundException("Molde not found")

        // Act
        val response = moldeController.updateMolde(updateRequest, moldeUUID)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("fail", response.body?.status)

        verify { moldeService.updateMolde(moldeUUID, updateRequest) }
    }

    @Test
    fun `test deleteMolde successful`() {
        // Arrange
        val moldeUUID = UUID.randomUUID()

        every { moldeService.deleteMolde(moldeUUID) } returns Unit

        // Act
        val response = moldeController.deleteMolde(moldeUUID)

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify { moldeService.deleteMolde(moldeUUID) }
    }

    @Test
    fun `test deleteMolde with MoldeNotFoundException`() {
        // Arrange
        val moldeUUID = UUID.randomUUID()

        every { moldeService.deleteMolde(moldeUUID) } throws MoldeNotFoundException("Molde not found")

        // Act
        val response = moldeController.deleteMolde(moldeUUID)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("fail", (response.body as GenericResponse<*>).status)

        verify { moldeService.deleteMolde(moldeUUID) }
    }

}
