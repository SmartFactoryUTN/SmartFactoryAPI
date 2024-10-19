package com.example.smartfactory.unittest.api

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.api.MoldeController
import com.example.smartfactory.application.Molde.MoldeService
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class MoldeControllerTest {

    lateinit var mvc: MockMvc

    @InjectMockKs
    lateinit var moldeController: MoldeController

    @MockK
    lateinit var moldeService: MoldeService

    private var objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(){
        mvc = MockMvcBuilders.standaloneSetup(moldeController).build()
        objectMapper.findAndRegisterModules()
    }

    fun createMolde() {
        //Arrange
        val moldeUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val svgFile = MockMultipartFile("svg", "test.svg", "image/svg+xml", "<svg></svg>".byteInputStream())

        // Mock the service call
        coEvery { moldeService.createMolde(any()) } returns Molde(
            moldeUUID,
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
}
