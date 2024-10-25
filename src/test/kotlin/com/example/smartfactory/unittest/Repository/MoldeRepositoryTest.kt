package com.example.smartfactory.unittest.Repository

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Repository.MoldeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class MoldeRepositoryTest{

    @Autowired
    lateinit var moldeRepository: MoldeRepository

    @Test
    fun moldeRepository_Save_ReturnsSavedMolde(){

        // Arrange
        val moldeUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val url = "https://servicio-de-tizada.s3.us-east-2.amazonaws.com/moldeA.svg"
        val molde = Molde(
            uuid = moldeUUID,
            name = "Un Molde",
            owner = userUUID,
            url = url,
            description = "desc",
            area = 1.0,
            active = true,
            createdAt = LocalDateTime.now()
        )

        // Act
        val savedMolde = moldeRepository.save(molde)

        // Assert
        assertEquals("Un Molde", savedMolde.name)
        assertEquals("desc", savedMolde.description)
        assertEquals(userUUID, savedMolde.owner)
        assertEquals(moldeUUID, savedMolde.uuid)
        assertEquals(url, savedMolde.url)
    }
    @Test
    fun moldeRepository_findMoldeByOwner(){

        // Arrange
        val moldeUUID = UUID.randomUUID()
        val userUUID = UUID.randomUUID()
        val url = "https://servicio-de-tizada.s3.us-east-2.amazonaws.com/moldeA.svg"
        val molde = Molde(
            uuid = moldeUUID,
            name = "Un Molde",
            owner = userUUID,
            url = url,
            description = "desc",
            area = 1.0,
            active = true,
            createdAt = LocalDateTime.now()
        )

        val otherMoldeUUID = UUID.randomUUID()
        val otherUserUUID = UUID.randomUUID()
        val otherUrl = "https://servicio-de-tizada.s3.us-east-2.amazonaws.com/moldeB.svg"
        val otherMolde = Molde(
            uuid = otherMoldeUUID,
            name = "Un Molde",
            owner = otherUserUUID,
            url = otherUrl,
            description = "desc",
            area = 1.0,
            active = true,
            createdAt = LocalDateTime.now()
        )

        // Act
        moldeRepository.save(molde)
        moldeRepository.save(otherMolde)

        val otherUserMoldes = moldeRepository.findMoldeByOwner(otherUserUUID)
        val totalModels = moldeRepository.getAllMoldes()

        // Assert
        assertEquals(1, otherUserMoldes.count())
        assertEquals(2, totalModels.count())
    }
}
