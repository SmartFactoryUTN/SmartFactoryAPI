package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Molde.Molde
import java.util.UUID

class TizadaResult (
    val uuid: UUID,
    val url: String,
    val configuration: TizadaConfiguration,
    val bin: TizadaContainer,
    val parts: List<Molde>,
    val materialUtilization: Int,
    val iterations: Int,
    val timeoutReached: Boolean
)