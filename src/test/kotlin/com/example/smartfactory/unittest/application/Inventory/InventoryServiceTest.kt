package com.example.smartfactory.application.Inventory

import com.example.smartfactory.Domain.Inventory.FabricColor
import com.example.smartfactory.Domain.Inventory.FabricPiece
import com.example.smartfactory.Domain.Inventory.FabricRoll
import com.example.smartfactory.Domain.Inventory.Garment
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.MoldsQuantity
import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Exceptions.FabricRollNotFoundException
import com.example.smartfactory.Exceptions.MoldeNotFoundException
import com.example.smartfactory.Exceptions.TizadaInvalidStateException
import com.example.smartfactory.Repository.*
import com.example.smartfactory.application.Inventory.Request.*
import com.example.smartfactory.application.Tizada.TizadaService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

internal class InventoryServiceTest {

    private lateinit var inventoryService: InventoryService
    private val garmentRepository: GarmentRepository = mockk()
    private val moldeRepository: MoldeRepository = mockk()
    private val fabricRollRepository: FabricRollRepository = mockk()
    private val fabricPieceRepository: FabricPieceRepository = mockk()
    private val fabricColorRepository: FabricColorRepository = mockk()
    private val tizadaService: TizadaService = mockk()

    @BeforeEach
    fun setUp() {
        inventoryService = InventoryService(
            garmentRepository,
            moldeRepository,
            fabricRollRepository,
            fabricPieceRepository,
            fabricColorRepository,
            tizadaService
        )
    }

    @Test
    fun `createGarment should create and save a garment successfully`() {
        val garmentComponent = GarmentComponents(
            moldeId = UUID.randomUUID(),
            fabricRollId = UUID.randomUUID(),
            quantity = 2
        )
        val createGarmentRequest = CreateGarmentRequest(
            article = "Test Article",
            description = "Test Description",
            garmentComponents = listOf(garmentComponent)
        )
        val user = Usuario(
            uuid = UUID.randomUUID(),
            tizadas = null,
            parts = null,
            name = "Test User",
            email = "test@example.com",
            externalId = "external-123"
        )
        val fabricColor = FabricColor(UUID.randomUUID(), "Blue")
        val fabricRoll = FabricRoll(
            fabricRollId = garmentComponent.fabricRollId,
            name = "Roll Test",
            description = "Description test",
            color = fabricColor,
            stock = 10,
            user = user,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
        val molde = Molde(
            uuid = garmentComponent.moldeId,
            owner = user.uuid,
            name = "Molde Test",
            url = "http://example.com/molde",
            description = "Test Description",
            area = 10.0,
            active = true,
            createdAt = LocalDateTime.now()
        )
        val fabricPiece = FabricPiece(
            fabricPieceId = UUID.randomUUID(),
            molde = molde,
            stock = 10,
            user = user,
            fabricRoll = fabricRoll,
            name = "${molde.name} color ${fabricRoll.color.name}"
        )

        every { moldeRepository.findMoldeByUuid(garmentComponent.moldeId) } returns molde
        every { fabricRollRepository.getFabricRollByFabricRollId(garmentComponent.fabricRollId) } returns fabricRoll
        every { fabricPieceRepository.getFabricPieceByFabricRollAndMolde(any(), any()) } returns fabricPiece
        every { garmentRepository.save(any()) } returns mockk()

        val garmentId = inventoryService.createGarment(createGarmentRequest, user)

        verify { garmentRepository.save(any()) }
        assertNotNull(garmentId)
    }

    @Test
    fun `createGarment should throw MoldeNotFoundException when molde is not found`() {
        val garmentComponent = GarmentComponents(
            moldeId = UUID.randomUUID(),
            fabricRollId = UUID.randomUUID(),
            quantity = 2
        )
        val createGarmentRequest = CreateGarmentRequest(
            article = "Test Article",
            description = "Test Description",
            garmentComponents = listOf(garmentComponent)
        )
        val user = Usuario(
            uuid = UUID.randomUUID(),
            tizadas = null,
            parts = null,
            name = "Test User",
            email = "test@example.com",
            externalId = "external-123"
        )

        every { moldeRepository.findMoldeByUuid(garmentComponent.moldeId) } returns null

        val exception = assertThrows<MoldeNotFoundException> {
            inventoryService.createGarment(createGarmentRequest, user)
        }

        assertEquals("No pudimos obtener este molde", exception.message)
        verify(exactly = 0) { garmentRepository.save(any()) }
    }

    @Test
    fun `updateGarment should update garment successfully`() {
        val garmentId = UUID.randomUUID()
        val updateGarmentRequest = UpdateGarmentRequest(
            article = "Updated Article",
            description = "Updated Description",
            stock = 5
        )
        val user = Usuario(
            uuid = UUID.randomUUID(),
            tizadas = null,
            parts = null,
            name = "Test User",
            email = "test@example.com",
            externalId = "external-123"
        )
        val garment = Garment(
            garmentId = garmentId,
            article = "Old Article",
            description = "Old Description",
            stock = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null,
            garmentPieces = mutableListOf(),
            user = user
        )

        every { garmentRepository.getGarmentByGarmentId(garmentId) } returns garment
        every { garmentRepository.save(any()) } returns mockk()

        val response = inventoryService.updateGarment(garmentId, updateGarmentRequest)

        assertEquals("Updated Article", garment.article)
        assertEquals("Updated Description", garment.description)
        assertEquals(5, garment.stock)
        verify { garmentRepository.save(garment) }
        assertEquals(garmentId, response.garmentId)
        assertEquals(5, response.newStock)
    }

    @Test
    fun `getFabricPieceIfExistsOrCreate should create and return a new FabricPiece when it does not exist`() {
        val moldeId = UUID.randomUUID()
        val fabricRollId = UUID.randomUUID()
        val user = Usuario(
            uuid = UUID.randomUUID(),
            tizadas = null,
            parts = null,
            name = "Test User",
            email = "test@example.com",
            externalId = "external-123"
        )
        val molde = Molde(moldeId, user.uuid, "Test Molde", "http://example.com", "Description", 10.0, true, LocalDateTime.now())
        val fabricColor = FabricColor(UUID.randomUUID(), "Red")
        val fabricRoll = FabricRoll(fabricRollId, "Roll Test", "Description test", fabricColor, 50, user, LocalDateTime.now(), null, null)
        val newFabricPiece = FabricPiece(UUID.randomUUID(), molde, 0, user, fabricRoll, "New Piece")

        every { moldeRepository.findMoldeByUuid(moldeId) } returns molde
        every { fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) } returns fabricRoll
        every { fabricPieceRepository.getFabricPieceByFabricRollAndMolde(fabricRoll, molde) } returns null
        every { fabricPieceRepository.save(any()) } returns newFabricPiece

        val result = inventoryService.getFabricPieceIfExistsOrCreate(moldeId, fabricRollId, user)

        verify { fabricPieceRepository.save(any()) }
        assertEquals(newFabricPiece, result)
    }

    @Test
    fun `getFabricPieceIfExistsOrCreate should return existing FabricPiece when it exists`() {
        val moldeId = UUID.randomUUID()
        val fabricRollId = UUID.randomUUID()
        val user = Usuario(
            uuid = UUID.randomUUID(),
            tizadas = null,
            parts = null,
            name = "Test User",
            email = "test@example.com",
            externalId = "external-123"
        )
        val molde = Molde(moldeId, user.uuid, "Test Molde", "http://example.com", "Description", 10.0, true, LocalDateTime.now())
        val fabricColor = FabricColor(UUID.randomUUID(), "Red")
        val fabricRoll = FabricRoll(fabricRollId, "Roll Test", "Description test", fabricColor, 50, user, LocalDateTime.now(), null, null)
        val existingFabricPiece = FabricPiece(UUID.randomUUID(), molde, 10, user, fabricRoll, "Existing Piece")

        every { moldeRepository.findMoldeByUuid(moldeId) } returns molde
        every { fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) } returns fabricRoll
        every { fabricPieceRepository.getFabricPieceByFabricRollAndMolde(fabricRoll, molde) } returns existingFabricPiece

        val result = inventoryService.getFabricPieceIfExistsOrCreate(moldeId, fabricRollId, user)

        verify(exactly = 0) { fabricPieceRepository.save(any()) }
        assertEquals(existingFabricPiece, result)
    }

    @Test
    fun `updateFabricRoll should update fabric roll successfully`() {
        val fabricRollId = UUID.randomUUID()
        val updateRequest = UpdateFabricRollRequest(name = "Updated Roll", stock = 100, description = "Updated description")
        val user = Usuario(UUID.randomUUID(), null, null, "Test User", "test@example.com", "external-123")
        val fabricColor = FabricColor(UUID.randomUUID(), "Blue")
        val fabricRoll = FabricRoll(fabricRollId, "Original Roll", "Original description", fabricColor, 50, user, LocalDateTime.now(), null, null)

        every { fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) } returns fabricRoll
        every { fabricRollRepository.save(any()) } returns fabricRoll

        val response = inventoryService.updateFabricRoll(fabricRollId, updateRequest)

        verify { fabricRollRepository.save(fabricRoll) }
        assertEquals("Updated Roll", fabricRoll.name)
        assertEquals(100, fabricRoll.stock)
        assertEquals(fabricRollId, response.fabricRollId)
        assertEquals(100, response.newStock)
    }

    @Test
    fun `updateFabricRoll should throw FabricRollNotFoundException when fabric roll is not found`() {
        val fabricRollId = UUID.randomUUID()
        val updateRequest = UpdateFabricRollRequest(name = "Updated Roll", description = "Updated description", stock = 100)

        every { fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) } returns null

        val exception = assertThrows<FabricRollNotFoundException> {
            inventoryService.updateFabricRoll(fabricRollId, updateRequest)
        }

        assertEquals("No se encontró el rollo de tella con ID $fabricRollId", exception.message)
        verify(exactly = 0) { fabricRollRepository.save(any()) }
    }

    @Test
    fun `convertFabricRoll should update fabric roll stock and create fabric pieces when tizada state is FINISHED`() {
        val user = Usuario(UUID.randomUUID(), null, null, "Test User", "test@example.com", "external-123")
        val fabricColor = FabricColor(UUID.randomUUID(), "Blue")
        val fabricRollId = UUID.randomUUID()
        val fabricRoll = FabricRoll(fabricRollId, "Roll Test", "Description", fabricColor, 50, user, LocalDateTime.now(), null, null)
        val molde = Molde(UUID.randomUUID(), user.uuid, "Molde Test", "http://example.com/molde", "Description", 10.0, true, LocalDateTime.now())
        val moldsQuantity = MoldsQuantity(molde, 3)
        val tizada = Tizada(
            uuid = UUID.randomUUID(),
            owner = user.uuid,
            name = "Tizada Test",
            configuration = mockk(),
            parts = mutableListOf(moldsQuantity),
            bin = mockk(),
            results = null,
            state = TizadaState.FINISHED,
            active = true,
            createdAt = LocalDateTime.now()
        )

        val rollQuantity = Rolls(fabricRollId, 5)
        val convertRequest = ConvertFabricRollRequest(
            tizadaId = tizada.uuid,
            layerMultiplier = 2,
            rollsQuantity = listOf(rollQuantity)
        )

        every { tizadaService.getTizadaByUUID(convertRequest.tizadaId) } returns tizada
        every { fabricRollRepository.getFabricRollByFabricRollId(fabricRollId) } returns fabricRoll
        every { fabricPieceRepository.getFabricPieceByFabricRollAndMolde(fabricRoll, molde) } returns null
        every { fabricPieceRepository.save(any()) } returns mockk()
        every { fabricRollRepository.save(fabricRoll) } returns mockk()

        inventoryService.convertFabricRoll(convertRequest, user)

        verify { fabricPieceRepository.save(any()) }
        verify { fabricRollRepository.save(fabricRoll) }
        assertEquals(45, fabricRoll.stock) // 50 - 5
    }

    @Test
    fun `convertFabricRoll should throw exception if tizada state is not FINISHED`() {
        val user = Usuario(UUID.randomUUID(), null, null, "Test User", "test@example.com", "external-123")
        val tizada = Tizada(
            uuid = UUID.randomUUID(),
            owner = user.uuid,
            name = "Tizada Test",
            configuration = mockk(),
            parts = mutableListOf(),
            bin = mockk(),
            results = null,
            state = TizadaState.IN_PROGRESS,
            active = true,
            createdAt = LocalDateTime.now()
        )

        val convertRequest = ConvertFabricRollRequest(
            tizadaId = tizada.uuid,
            layerMultiplier = 2,
            rollsQuantity = listOf(Rolls(UUID.randomUUID(), 5))
        )

        every { tizadaService.getTizadaByUUID(convertRequest.tizadaId) } returns tizada

        val exception = assertThrows<TizadaInvalidStateException> {
            inventoryService.convertFabricRoll(convertRequest, user)
        }

        assertEquals("No se puede convertir una tizada que no esté en estado finalizada.", exception.message)
    }
}
