package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Usuarios.Usuario
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UsuarioRepository: CrudRepository<Usuario, Long>{
    fun findByExternalId(auth0Id: String): Usuario?
    fun getUsuarioByUuid(uuid: UUID):Usuario?
}

