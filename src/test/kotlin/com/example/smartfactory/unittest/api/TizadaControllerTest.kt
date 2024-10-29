package com.example.smartfactory.unittest.api

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.Domain.Tizada.TizadaContainer
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.UsuarioRepository
import com.example.smartfactory.api.TizadaController
import com.example.smartfactory.application.Tizada.Request.*
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.application.Tizada.TizadaService
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals


@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class TizadaControllerTest {

    lateinit var mvc: MockMvc

    @InjectMockKs
    lateinit var tizadaController: TizadaController

    @MockK
    lateinit var tizadaService: TizadaService

    @MockK
    lateinit var usuarioRepository: UsuarioRepository

    @MockK
    lateinit var lambdaService: LambdaService

    lateinit var jsonInvokeTizadaRequest: JacksonTester<InvokeTizadaRequest>
    lateinit var jsonTizadaNotificationRequest: JacksonTester<TizadaNotificationRequest>
    lateinit var jsonCreateTizadaRequest: JacksonTester<CreateTizadaRequest>
    private var objectMapper = ObjectMapper()
    private lateinit var jwt: Jwt

    @BeforeEach
    fun setUp(){
        mvc = MockMvcBuilders.standaloneSetup(tizadaController).build()
        objectMapper.findAndRegisterModules()
        JacksonTester.initFields(this, objectMapper)
        jwt = mockk {
            every { claims } returns mapOf("sub" to "externalId")
        }
    }

    @Test
    fun invokeTizada() {
        // Arrange
        val invokeTizadaResponseMock = InvokeTizadaResponse("202", "''")

        every { lambdaService.invokeLambdaAsync(any()) } returns invokeTizadaResponseMock
        every { tizadaService.invokeTizada(any()) } returns invokeTizadaResponseMock

        val payload = InvokeTizadaRequest(
            tizadaUUID = UUID.randomUUID().toString(),
            userUUID = "a038a4d2-8502-455f-a154-aa87b1cc3fec",
        )
        val expectedResponse = TizadaResponse(
            status = "success",
            data = mapOf("tizada" to invokeTizadaResponseMock)
        )

        // Act
        val response = mvc.perform(
            post("/api/tizada/invoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvokeTizadaRequest.write(payload).json)
        )
            .andExpect(status().isNoContent) // Expect 204 status
            .andReturn().response

        val actualResponse = objectMapper.readValue(response.contentAsString, TizadaResponse::class.java)

        val nestedData = actualResponse.data as Map<*, *>
        val actualTizadaResponse = nestedData["tizada"] as Map<*, *>

        // Assert response matches the expected response
        assertEquals(expectedResponse.status, actualResponse.status)
        assertEquals(invokeTizadaResponseMock.statusCode, actualTizadaResponse["statusCode"].toString())
        assertEquals(invokeTizadaResponseMock.payload, actualTizadaResponse["payload"].toString())
        assertEquals(expectedResponse.status, actualResponse.status)
    }

    @Suppress("MaxLineLength")
    @Test
    fun notifyTizada(){

        //Arrange
        val tizadaUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val part1 = UUID.randomUUID().toString()
        val part2 = UUID.randomUUID().toString()

        val resultUrl =
            "https://servicio-de-tizada.s3.us-east-2.amazonaws.com/result/38f21fe2-8198-4467-9bda-2f3b8815befe/result.svg"
        val payload = TizadaNotificationRequest(
            tizadaUUID = tizadaUUID,
            url = resultUrl,
            userUUID = userUUID,
            parts = listOf(part1, part2),
            materialUtilization = 50,
            iterations = 100,
            timeoutReached = false,
            status = TizadaResultStatus.SUCCESS.toString()
        )

        every { tizadaService.saveTizadaFinalizada(any()) } returns mockk()
        every { tizadaService.findTizadaResult(any()) } returns null
        //Act
        val response = mvc.perform(
            post("/api/tizada/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonTizadaNotificationRequest.write(payload).json)
        )
            .andExpect(status().isCreated)
            .andReturn().response

        val actualResponse = objectMapper.readValue(response.contentAsString, TizadaResponse::class.java)
        val nestedData = actualResponse.data as Map<*, *>

        //Assert
        assertEquals(tizadaUUID.toString(), nestedData["tizadaUUID"].toString())
        assertEquals(userUUID.toString(), nestedData["userUUID"].toString())

        assertEquals(resultUrl, nestedData["url"])
    }

    @Test
    fun `test deleteTizada success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()

        every { tizadaService.deleteTizada(tizadaUUID) } returns Unit

        // Act
        val response = tizadaController.deleteTizada(tizadaUUID)

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify { tizadaService.deleteTizada(tizadaUUID) }
    }

    @Test
    fun `test deleteTizada not found`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        every { tizadaService.deleteTizada(tizadaUUID) } throws TizadaNotFoundException("Tizada not found")

        // Act
        val response = tizadaController.deleteTizada(tizadaUUID)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("fail", (response.body as TizadaResponse<*>).status)
        verify { tizadaService.deleteTizada(tizadaUUID) }
    }

    @Test
    fun `test getTizada success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val containerId = UUID.randomUUID()

        val bin = TizadaContainer(
            uuid = containerId,
            name = "Mesa de corte",
            height = 100,
            width = 100,
            url = "http://dummy.com",
            area = (1000 * 100).toDouble(),
            createdAt = LocalDateTime.now()
        )

        val configuration = TizadaConfiguration(
            id = UUID.randomUUID(),
            time = 10,
            utilizationPercentage = 80
        )
        val tizadaData = Tizada(
            uuid = tizadaUUID,
            owner = userUUID,
            name = "Una tizada",
            configuration = configuration,
            bin = bin,
            results = mutableListOf(),
            state = TizadaState.CREATED,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null,
            parts = mutableListOf()
        )

        every { tizadaService.getTizadaByUUID(tizadaUUID) } returns tizadaData

        // Act
        val response = tizadaController.getTizada(tizadaUUID)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body?.status)
        assertEquals(tizadaData, response.body?.data)
        verify { tizadaService.getTizadaByUUID(tizadaUUID) }
    }

    @Test
    fun `test getTizada not found`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()

        every { tizadaService.getTizadaByUUID(tizadaUUID) } throws TizadaNotFoundException("Tizada not found")

        // Act
        val response = tizadaController.getTizada(tizadaUUID)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("fail", response.body?.status)
        assertEquals("Tizada not found", (response.body?.data as Map<*, *>)["message"])
        verify { tizadaService.getTizadaByUUID(tizadaUUID) }
    }

    @Test
    fun `test createTizada success`() = runBlocking {
        // Arrange
        val createTizadaRequest = CreateTizadaRequest(
            name = "Una Tizada",
            height = 10,
            width = 100,
            maxTime = 1000,
            utilizationPercentage = 80,
            molds = mutableListOf()
        )
        val ownerUUID = UUID.randomUUID()
        val createdTizadaUUID = UUID.randomUUID()

        every { usuarioRepository.findByExternalId("externalId") } returns mockk { every { uuid } returns ownerUUID }
        coEvery { tizadaService.createTizada(createTizadaRequest, ownerUUID) } returns createdTizadaUUID

        // Act
        val response = tizadaController.createTizada(createTizadaRequest, jwt)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("success", response.body?.status)
        assertEquals(createdTizadaUUID, response.body?.data)
        coVerify { tizadaService.createTizada(createTizadaRequest, ownerUUID) }
        verify { usuarioRepository.findByExternalId("externalId") }
    }

    @Test
    fun `test createTizada forbidden`() = runBlocking {
        // Arrange
        val createTizadaRequest = CreateTizadaRequest(
            name = "Una Tizada",
            height = 10,
            width = 100,
            maxTime = 1000,
            utilizationPercentage = 80,
            molds = mutableListOf()
        )

        every { usuarioRepository.findByExternalId("externalId") } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            runBlocking {
                tizadaController.createTizada(createTizadaRequest, jwt)
            }
        }
        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        verify { usuarioRepository.findByExternalId("externalId") }
    }

    @Test
    fun `test updateTizada success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        val updateTizadaRequest = UpdateTizadaRequest(
            name = "Una Tizada",
        )

        every { tizadaService.updateTizada(tizadaUUID, updateTizadaRequest) } returns tizadaUUID

        // Act
        val response = tizadaController.updateTizada(tizadaUUID, updateTizadaRequest)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body?.status)
        assertEquals(tizadaUUID, response.body?.data)
        verify { tizadaService.updateTizada(tizadaUUID, updateTizadaRequest) }
    }

    @Test
    fun `test updateTizada not found`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        val updateTizadaRequest = UpdateTizadaRequest(
            name = "Una Tizada",
        )

        every { tizadaService.updateTizada(tizadaUUID, updateTizadaRequest) } throws TizadaNotFoundException("Tizada not found")

        // Act
        val response = tizadaController.updateTizada(tizadaUUID, updateTizadaRequest)

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("fail", response.body?.status)
        assertEquals("Tizada not found", (response.body?.data as Map<*, *>)["message"])
        verify { tizadaService.updateTizada(tizadaUUID, updateTizadaRequest) }
    }

}
