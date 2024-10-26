package com.example.smartfactory.application.Molde

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Exceptions.MoldeNotFoundException
import com.example.smartfactory.Repository.MoldeRepository
import com.example.smartfactory.integration.LambdaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MoldeService(
    private val moldeRepository: MoldeRepository,
    private val lambdaService: LambdaService
) {
    suspend fun createMolde(molde: CreateMoldeRequest): Molde {
        // Step 1: Upload the SVG file and get its URL
        val generatedUUID = UUID.randomUUID()

        val svgUrl = lambdaService.uploadFile(molde, generatedUUID)

        // Step 2: Create the Molde object
        val newMolde = Molde(
            uuid = generatedUUID,
            owner = molde.userUUID,
            name = molde.name,
            url = svgUrl, // Use the real URL where the SVG is stored
            description = molde.description,
            area = null,  // Set area if needed
            active = true,
            createdAt = LocalDateTime.now()
        )

        // Step 3: Save the Molde object in the repository
        withContext(Dispatchers.IO) {
            moldeRepository.save(newMolde)
        }

        return newMolde
    }

    fun getMoldeById(id: UUID): Molde? {
        return moldeRepository.findMoldeByUuid(id)
    }

    fun getAllMoldes(): List<Molde> {
        return moldeRepository.getAllMoldes()
    }

    fun getAllMoldesByOwner(owner: UUID): List<Molde>? {
        return moldeRepository.findMoldeByOwner(owner)
    }

    fun updateMolde(id: UUID, request: UpdateMoldeRequest): Molde {
        val molde = moldeRepository.findMoldeByUuid(id) ?: throw MoldeNotFoundException("No Molde found with id $id")
        request.name?.let { molde.name = it }
        request.description?.let { molde.description = it }
        molde.updatedAt = LocalDateTime.now()
        return moldeRepository.save(molde)
    }

    fun deleteMolde(id: UUID) {
        val molde = moldeRepository.findMoldeByUuid(id) ?: throw MoldeNotFoundException("No Molde found with id $id")
        molde.deletedAt = LocalDateTime.now()
        molde.active = false
        moldeRepository.save(molde)
    }
}
