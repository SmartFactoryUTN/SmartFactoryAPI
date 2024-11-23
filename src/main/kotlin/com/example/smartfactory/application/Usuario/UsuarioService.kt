package com.example.smartfactory.application.Usuario

import com.example.smartfactory.Exceptions.UsuarioNotFoundException
import com.example.smartfactory.Repository.UsuarioRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.max

@Service
class UsuarioService(
    @Autowired
    private val usuarioRepository: UsuarioRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun consumeUsuarioCredits(amount: Int, usuarioUUID: UUID) {

        val usuario = usuarioRepository.getUsuarioByUuid(usuarioUUID)
            ?: throw UsuarioNotFoundException("Usuario no encontrado uuid: $usuarioUUID")
        val usuarioCredits = usuario.credits
        val newCredits = max(
            usuarioCredits - amount,
            0
        )
        usuario.credits = newCredits

        logger.info {
            "Consumiendo créditos para el usuario ${usuario.uuid}. " +
                    "Cantidad: $amount - Disponibles: $usuarioCredits - Resultado: ${usuario.credits}"
        }

        usuarioRepository.save(usuario)
    }
}
