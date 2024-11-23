package com.example.smartfactory.unittest.Repository

import com.example.smartfactory.Domain.Usuarios.Usuario
import com.example.smartfactory.Repository.UsuarioRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*
import kotlin.test.assertEquals

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UsuarioRepositoryTest {
    @Autowired
    lateinit var usuarioRepository: UsuarioRepository

    @Test
    fun usuarioRepository_Save_ReturnsSavedUsuario() {
        // Arrange
        val usuarioUUID = UUID.randomUUID()
        val usuario = Usuario(
            uuid = usuarioUUID,
            email = "gasti@smartfactory.com",
            externalId = "extId|123",
            name = "Gaston",
            parts = null,
            tizadas = null,
        )
        // Act
        val savedUsuario = usuarioRepository.save(usuario)

        // Assert
        assertEquals("Gaston", savedUsuario.name)
        assertEquals("gasti@smartfactory.com", savedUsuario.email)
        assertEquals("extId|123", savedUsuario.externalId)
        assertEquals(usuarioUUID, savedUsuario.uuid)
    }
}
