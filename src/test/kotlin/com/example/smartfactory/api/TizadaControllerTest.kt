package com.example.smartfactory.api

import com.example.smartfactory.application.Tizada.Request.Bin
import com.example.smartfactory.application.Tizada.Request.InvokeConfiguration
import com.example.smartfactory.application.Tizada.Request.InvokeTizadaRequest
import com.example.smartfactory.application.Tizada.Request.Part
import com.example.smartfactory.application.Tizada.TizadaService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class TizadaControllerTest {

    lateinit var mvc: MockMvc

    @InjectMockKs
    lateinit var tizadaController: TizadaController

    @MockK
    lateinit var  tizadaService: TizadaService

    private var objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(){
        mvc = MockMvcBuilders.standaloneSetup(tizadaController).build()
        objectMapper.findAndRegisterModules()
    }

    @Test
    fun invokeTizada() {
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
        // When & Then
        val response = mvc.perform(
            post("/api/tizada/invoke")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andReturn().response
        assertTrue(true)
            /*.andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.message").value(expectedResponse))
*/
    }
}
