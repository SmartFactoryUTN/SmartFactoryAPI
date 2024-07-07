package com.example.smartfactory.Domain.Tizada

import java.util.UUID

//data class User(val name: String, val age: Int)

class Tizada(
    val uuid: UUID,
    val configuration: TizadaConfiguration,
    val parts: ArrayList<TizadaPart>,
    val bin: TizadaPart
)
