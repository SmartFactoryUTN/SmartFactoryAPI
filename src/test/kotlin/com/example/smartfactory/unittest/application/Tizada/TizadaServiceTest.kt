package com.example.smartfactory.unittest.application.Tizada

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.Domain.Tizada.TizadaContainer
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.Exceptions.TizadaNotFoundException
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.Repository.TizadaRepository
import com.example.smartfactory.application.Tizada.Request.*
import com.example.smartfactory.application.Tizada.TizadaService
import com.example.smartfactory.integration.InvokeTizadaResponse
import com.example.smartfactory.integration.LambdaService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
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

    @Test
    fun `test createTizada success`() = runBlocking {
        // Arrange
        val ownerUUID = UUID.randomUUID()
        val createTizadaRequest = CreateTizadaRequest(
            name = "New Tizada",
            molds = listOf(Part(uuid = UUID.randomUUID().toString(), quantity = 5)),
            height = 100,
            width = 50,
            maxTime = 60,
            utilizationPercentage = 80
        )

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

        val tizada = Tizada(
            uuid = UUID.randomUUID(),
            owner = UUID.randomUUID(),
            name = "Test Tizada",
            bin = bin,
            configuration = configuration,
            active = true,
            parts = mutableListOf(),
            results = mutableListOf(),
            state = TizadaState.CREATED,
            updatedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now()
        )

        val containerURL = "https://mock-container-url.com"

        coEvery { lambdaService.uploadContainer(any(), any(), any()) } returns containerURL
        coEvery { tizadaRepository.save(any()) } returns tizada

        // Act
        val result = tizadaService.createTizada(createTizadaRequest, ownerUUID)

        // Assert
        assertNotNull(result)
        coVerify { tizadaRepository.save(any<Tizada>()) }
        coVerify { lambdaService.uploadContainer(any(), any(), any()) }
    }

    @Test
    fun `test getTizadaByUUID success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()

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

        val tizada = Tizada(
            uuid = tizadaUUID,
            owner = UUID.randomUUID(),
            name = "Test Tizada",
            bin = bin,
            configuration = configuration,
            active = true,
            parts = mutableListOf(),
            results = mutableListOf(),
            state = TizadaState.CREATED,
            updatedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now()
            )
        tizada.moldesDeTizada = mutableListOf()
        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns tizada
        every { moldeRepository.findMoldeByUuid(any()) } returns mockk()

        // Act
        val result = tizadaService.getTizadaByUUID(tizadaUUID)

        // Assert
        assertEquals(tizada, result)
        verify { tizadaRepository.getTizadaByUuid(tizadaUUID) }
    }

    @Test
    fun `test getTizadaByUUID not found`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns null

        // Act & Assert
        val exception = assertThrows<TizadaNotFoundException> {
            tizadaService.getTizadaByUUID(tizadaUUID)
        }
        assertEquals("No se encontró la tizada con ID $tizadaUUID", exception.message)
        verify { tizadaRepository.getTizadaByUuid(tizadaUUID) }
    }

    @Test
    fun `test updateTizada success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
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

        val tizada = Tizada(
            uuid = tizadaUUID,
            owner = UUID.randomUUID(),
            name = "Test Tizada",
            bin = bin,
            configuration = configuration,
            active = true,
            parts = mutableListOf(),
            results = mutableListOf(),
            state = TizadaState.CREATED,
            updatedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now()
        )
        val updateRequest = UpdateTizadaRequest(name = "Updated Tizada")

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns tizada
        every { tizadaRepository.save(any<Tizada>()) } returns tizada

        // Act
        val result = tizadaService.updateTizada(tizadaUUID, updateRequest)

        // Assert
        assertEquals(tizadaUUID, result)
        verify { tizadaRepository.save(any<Tizada>()) }
    }

    @Test
    fun `test updateTizada not found`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        val updateRequest = UpdateTizadaRequest(name = "Updated Tizada")

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns null

        // Act & Assert
        val exception = assertThrows<TizadaNotFoundException> {
            tizadaService.updateTizada(tizadaUUID, updateRequest)
        }
        assertEquals("No se encontró la tizada con ID $tizadaUUID", exception.message)
        verify { tizadaRepository.getTizadaByUuid(tizadaUUID) }
    }

    @Test
    fun `test deleteTizada success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
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

        val tizada = Tizada(
            uuid = tizadaUUID,
            owner = UUID.randomUUID(),
            name = "Test Tizada",
            bin = bin,
            configuration = configuration,
            active = true,
            parts = mutableListOf(),
            results = mutableListOf(),
            state = TizadaState.CREATED,
            updatedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now()
        )

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns tizada
        every { tizadaRepository.save(any<Tizada>()) } returns tizada

        // Act
        tizadaService.deleteTizada(tizadaUUID)

        // Assert
        verify { tizadaRepository.save(any<Tizada>()) }
    }

    @Test
    fun `test deleteTizada not found`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns null

        // Act & Assert
        val exception = assertThrows<TizadaNotFoundException> {
            tizadaService.deleteTizada(tizadaUUID)
        }
        assertEquals("No se encontró la tizada con ID $tizadaUUID", exception.message)
        verify { tizadaRepository.getTizadaByUuid(tizadaUUID) }
    }

    @Test
    fun `test invokeTizada success`() = runBlocking {
        // Arrange
        val invokeTizadaRequest = InvokeTizadaRequest(
            tizadaUUID = UUID.randomUUID().toString(),
            userUUID = UUID.randomUUID().toString()
        )

        val bin = TizadaContainer(
            uuid = UUID.randomUUID(),
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

        val tizada = Tizada(
            uuid = UUID.fromString(invokeTizadaRequest.tizadaUUID),
            name = "Test Tizada",
            state = TizadaState.CREATED,
            parts = mutableListOf(),
            bin = bin,
            configuration = configuration,
            updatedAt = null,
            active = true,
            createdAt = LocalDateTime.now(),
            owner = UUID.randomUUID(),
            results = mutableListOf(),
        )
        tizada.moldesDeTizada = mutableListOf()
        val invokeResponse = InvokeTizadaResponse(HttpStatus.ACCEPTED.toString(), "")

        every { tizadaRepository.getTizadaByUuid(UUID.fromString(invokeTizadaRequest.tizadaUUID)) } returns tizada
        coEvery { lambdaService.invokeLambdaAsync(any()) } returns invokeResponse
        coEvery { tizadaRepository.save(any()) } returns tizada

        // Act
        val result = tizadaService.invokeTizada(invokeTizadaRequest)

        // Assert
        assertNotNull(result)
        assertEquals(invokeResponse, result)
        assertEquals(TizadaState.IN_PROGRESS, tizada.state)
        assertNotNull(tizada.updatedAt)
        coVerify { lambdaService.invokeLambdaAsync(any()) }
        coVerify { tizadaRepository.save(tizada) }
    }

    @Test
    fun `test invokeTizada throws TizadaNotFoundException`() = runBlocking {
        // Arrange
        val invokeTizadaRequest = InvokeTizadaRequest(
            tizadaUUID = UUID.randomUUID().toString(),
            userUUID = UUID.randomUUID().toString()
        )

        every { tizadaRepository.getTizadaByUuid(UUID.fromString(invokeTizadaRequest.tizadaUUID)) } returns null

        // Act & Assert
        val exception = assertThrows<TizadaNotFoundException> {
            runBlocking { tizadaService.invokeTizada(invokeTizadaRequest) }
        }

        assertEquals("No se encontró la tizada con ID ${invokeTizadaRequest.tizadaUUID}", exception.message)
        coVerify(exactly = 0) { lambdaService.invokeLambdaAsync(any()) }
    }

    @Test
    fun `test saveTizadaFinalizada success`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        val request = TizadaNotificationRequest(
            tizadaUUID = tizadaUUID,
            userUUID = UUID.randomUUID(),
            parts = listOf("molde-ddcee655-2464-42f0-a1d2-cd1f62c5b8ed", "molde-f940ab1e-6107-4399-aeeb-eda764b1bddd"),
            url = "http://some-url.com",
            materialUtilization = 85,
            iterations = 10,
            timeoutReached = false
        )
        val tizada = Tizada(
            uuid = tizadaUUID,
            name = "Test Tizada",
            owner = UUID.randomUUID(),
            state = TizadaState.IN_PROGRESS,
            configuration = mockk(),
            bin = mockk(),
            parts = mutableListOf(),
            results = mutableListOf(),
            active = true,
            createdAt = LocalDateTime.now()
        )

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns tizada
        every { moldeRepository.findMoldeByUuid(any()) } returns mockk()
        every { tizadaRepository.save(any()) } returns tizada

        // Act
        tizadaService.saveTizadaFinalizada(request)

        // Assert
        assertEquals(TizadaState.FINISHED, tizada.state)
        assertNotNull(tizada.results?.firstOrNull())
        coVerify { tizadaRepository.save(tizada) }
    }

    @Test
    fun `test saveTizadaFinalizada throws TizadaNotFoundException`() {
        // Arrange
        val tizadaUUID = UUID.randomUUID()
        val request = TizadaNotificationRequest(
            tizadaUUID = tizadaUUID,
            userUUID = UUID.randomUUID(),
            parts = listOf("molde-1", "molde-2"),
            url = "http://some-url.com",
            materialUtilization = 85,
            iterations = 10,
            timeoutReached = false
        )

        every { tizadaRepository.getTizadaByUuid(tizadaUUID) } returns null

        // Act & Assert
        val exception = assertThrows<TizadaNotFoundException> {
            tizadaService.saveTizadaFinalizada(request)
        }

        assertEquals("No se encontró la tizada con ID $tizadaUUID", exception.message)
        coVerify(exactly = 0) { tizadaRepository.save(any()) }
    }

}
