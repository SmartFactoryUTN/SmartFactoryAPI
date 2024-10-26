package com.example.smartfactory.unittest.application.Tizada

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.Domain.Tizada.TizadaContainer
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.Repository.TizadaRepository
import com.example.smartfactory.application.Tizada.TizadaService
import com.example.smartfactory.integration.LambdaService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class TizadaServiceTest {

    // Mocks
    @MockK
    private lateinit var tizadaRepository: TizadaRepository
    @MockK
    private lateinit var tizadaService: TizadaService
    @MockK
    private lateinit var moldeRepository: MoldeRepository
    @MockK
    private lateinit var lambdaService: LambdaService


    @BeforeEach
    fun setup() {
        tizadaRepository = mockk()
        moldeRepository = mockk()
        lambdaService = mockk()
        tizadaService = TizadaService(tizadaRepository, moldeRepository, lambdaService)
    }

    @Test
    fun `should return tizada by uuid`() {

        val containerId = UUID.randomUUID()
        val bin = TizadaContainer(
            uuid = containerId,
            name = "Mesa de corte",
            height = 10,
            width = 20,
            url = "https://dummy.com",
            area = (10 * 20).toDouble(),
            createdAt = LocalDateTime.now()
        )
        val configuration = TizadaConfiguration(
            UUID.randomUUID(), time = 10, utilizationPercentage = 90
        )

        val tizadaUUID = UUID.randomUUID()

        val expectedTizada = Tizada(
            uuid = tizadaUUID,
            name =  "Tizada1",
            bin = bin,
            configuration = configuration,
            createdAt = LocalDateTime.now(),
            parts = mutableListOf(),
            results = mutableListOf(),
            state = TizadaState.CREATED,
            updatedAt = null,
            deletedAt = null,
            active = true,
            owner = UUID.randomUUID()
        )
        expectedTizada.moldesDeTizada = mutableListOf()

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns expectedTizada

        // Act
        val result = tizadaService.getTizadaByUUID(tizadaUUID)

        // Assert the results
        assertNotNull(result)
        assertEquals(expectedTizada, result)

        // Verify interaction
        verify(exactly = 1) { tizadaRepository.getTizadaByUuid(tizadaUUID) }
    }

    @Test
    fun `should return null when tizada not found by uuid`() {

        // Arrange
        val tizadaUUID = UUID.randomUUID()

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns null

        // Act
        val exception = org.junit.jupiter.api.assertThrows<TizadaNotFoundException> {
            tizadaService.getTizadaByUUID(tizadaUUID)
        }

        assertEquals("No se encontró la tizada con ID $tizadaUUID", exception.message)

        verify(exactly = 1) { tizadaRepository.getTizadaByUuid(tizadaUUID) }
    }

    @Test
    fun `should return tizadas for a given owner`() {

        // Arrange
        val userUUID = UUID.randomUUID()

        every { tizadaRepository.findTizadasByOwner(userUUID) } returns null

        // Act
        val result = tizadaService.getAllTizadasByOwner(userUUID)

        // Assert
        assertNull(result)
        verify(exactly = 1) { tizadaRepository.findTizadasByOwner(userUUID) }
    }

}
