package com.example.smartfactory.Domain.Usuarios

import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.Tizada
import java.util.*

class Usuario (
    val uuid: UUID,
    val tizadas: List<Tizada>,
    val parts: List<Molde>,
    val nombre: String,
    val email: String
)