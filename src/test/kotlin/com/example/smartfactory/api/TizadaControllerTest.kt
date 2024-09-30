package com.example.smartfactory.api

import com.example.smartfactory.application.Tizada.Request.*
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.application.Tizada.TizadaService
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import kotlin.test.assertEquals


@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class TizadaControllerTest {

    lateinit var mvc: MockMvc

    @InjectMockKs
    lateinit var tizadaController: TizadaController

    @MockK
    lateinit var  tizadaService: TizadaService

    @MockK
    lateinit var lambdaService: LambdaService

    lateinit var jsonInvokeTizadaRequest: JacksonTester<InvokeTizadaRequest>
    lateinit var jsonTizadaNotificationRequest: JacksonTester<TizadaNotificationRequest>
    private var objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(){
        mvc = MockMvcBuilders.standaloneSetup(tizadaController).build()
        objectMapper.findAndRegisterModules()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun invokeTizada() {
        // Arrange
        val invokeTizadaResponseMock = InvokeTizadaResponse("202", "''")

        every { lambdaService.invokeLambdaAsync(any()) } returns invokeTizadaResponseMock
        every { tizadaService.invokeTizada(any()) } returns invokeTizadaResponseMock

        val payload = InvokeTizadaRequest(
            tizadaUUID = UUID.randomUUID().toString(),
            user = "a038a4d2-8502-455f-a154-aa87b1cc3fec",
            parts = listOf(
                Part(uuid = "moldeA", quantity = 5),
                Part(uuid = "moldeB", quantity = 10),
                Part(uuid = "moldeC", quantity = 10)
            ),
            bin = Bin(uuid = "contenedorA", quantity = 1),
            configuration = InvokeConfiguration(maxIterations = 20, materialUtilization = 50, timeout = 0)
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

    @Test
    fun notifyTizada(){

        //Arrange
        val tizadaUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val binUUID = UUID.randomUUID().toString()
        val part1 = UUID.randomUUID().toString()
        val part2 = UUID.randomUUID().toString()

        val resultUrl =
            "https://servicio-de-tizada.s3.us-east-2.amazonaws.com/result/38f21fe2-8198-4467-9bda-2f3b8815befe/result.svg"
        val payload = TizadaNotificationRequest(
            tizadaUUID = tizadaUUID,
            url = resultUrl,
            userUUID = userUUID,
            configuration = TizadaConfigurationRequest(
                id = 1,
                spaceBetweenParts = 0.0
            ),
            bin = TizadaContainerRequest(
                uuid = binUUID,
                name = "Mesa de corte",
                height = 10,
                width = 10,
                area = 100.0
            ),
            parts = listOf(part1, part2),
            materialUtilization = 50,
            iterations = 100,
            timeoutReached = false
        )

        every { tizadaService.saveTizadaFinalizada(any()) } returns mockk()
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
        assertEquals(2, nestedData["parts"])
        assertEquals("Mesa de corte", nestedData["bin"])
        assertEquals(resultUrl, nestedData["url"])
    }
}
