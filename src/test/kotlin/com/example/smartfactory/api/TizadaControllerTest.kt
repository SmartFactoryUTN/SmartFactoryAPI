package com.example.smartfactory.api

import com.example.smartfactory.application.Tizada.Request.Bin
import com.example.smartfactory.application.Tizada.Request.InvokeConfiguration
import com.example.smartfactory.application.Tizada.Request.InvokeTizadaRequest
import com.example.smartfactory.application.Tizada.Request.Part
import com.example.smartfactory.application.Tizada.TizadaService
import com.example.smartfactory.integration.LambdaService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TizadaController::class)
class TizadaControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockK
    private lateinit var tizadaService: TizadaService  // Mock the service layer
    @MockK
    private lateinit var lambdaService: LambdaService
    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @BeforeEach
    fun setUp() {
        // Initialize mocks
        MockKAnnotations.init(this)
    }

    @Test
    fun `should invoke lambda and return success response`() {
        // Given
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

        val expectedResponse = "Lambda invoked asynchronously. Status: 202"

        every { lambdaService.invokeLambdaAsync(any()) } returns expectedResponse

        // When & Then
        mockMvc.perform(
            post("/api/tizada/invoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.message").value(expectedResponse))

        // Verify that the Lambda service was called
        verify(exactly = 1) { lambdaService.invokeLambdaAsync(any()) }
    }

    @Test
    fun `test lambda invocation through controller with MockK`() {
        // Given: A sample payload
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

        val expectedResponse = "Lambda invoked asynchronously. Status: 202"

        // Mock behavior of tizadaService
        every { tizadaService.invokeTizada(any()) } returns expectedResponse

        // When & Then: Perform the POST request and assert the response
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/tizada/invoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.message").value(expectedResponse))

        // Verify that the service method was called exactly once
        verify(exactly = 1) { tizadaService.invokeTizada(any()) }
    }

}
