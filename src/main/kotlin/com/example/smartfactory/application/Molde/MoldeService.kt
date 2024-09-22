package com.example.smartfactory.application.Molde

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.repository.MoldeRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MoldeService(private val moldeRepository: MoldeRepository) {
    fun createMolde(req: CreateMoldeRequest): CreateMoldeResponse {
        val molde = Molde(
            uuid = UUID.randomUUID(),
            name = req.name,
            url = "asdasd.com",
            description = req.description,
            area = null,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
        moldeRepository.save(molde)
        return CreateMoldeResponse("Molde cargado correctamente.")
    }

    fun getMoldeById(id: UUID): Molde? {
        return moldeRepository.findMoldeByUuid(id)
    }

    fun getAllMoldes(): List<Molde> {
        return moldeRepository.getAllMoldes()
    }
}