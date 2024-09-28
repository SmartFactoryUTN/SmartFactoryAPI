package com.example.smartfactory.api

import com.example.smartfactory.application.Tizada.Request.Bin
import com.example.smartfactory.application.Tizada.Request.InvokeConfiguration
import com.example.smartfactory.application.Tizada.Request.InvokeTizadaRequest
import com.example.smartfactory.application.Tizada.Request.Part
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.example.smartfactory.application.Tizada.TizadaService
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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

        val nestedData = actualResponse.data as Map<String, Any>
        val actualTizadaResponse = nestedData["tizada"] as Map<String, Any>

        // Assert response matches the expected response
        assertEquals(expectedResponse.status, actualResponse.status)
        assertEquals(invokeTizadaResponseMock.statusCode, actualTizadaResponse["statusCode"].toString())
        assertEquals(invokeTizadaResponseMock.payload, actualTizadaResponse["payload"].toString())
        assertEquals(expectedResponse.status, actualResponse.status)
    }

    @Test
    fun notifyTizada(){
        //Arrange
        //Act
        //Assert
    }
}
