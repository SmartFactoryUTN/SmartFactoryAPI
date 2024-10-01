package com.example.smartfactory.application.Molde

import com.example.smartfactory.Domain.Molde.Molde
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
        val svgUrl = lambdaService.uploadFile(molde)

        // Step 2: Create the Molde object
        val newMolde = Molde(
            uuid = UUID.randomUUID(),
            name = molde.name,
            url = svgUrl, // Use the real URL where the SVG is stored
            description = molde.description,
            area = null,  // Set area if needed
            active = true,
            stock = 0,
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
}
