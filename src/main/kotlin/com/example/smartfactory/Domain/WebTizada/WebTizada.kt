package com.example.smartfactory.Domain.WebTizada

import com.example.smartfactory.Domain.Tizada.Tizada
import java.util.*

class WebTizada (
    val id: Long,
    val uuid: UUID,
    val tableWidth: Int,
    val tableLength: Int,
    val result: Tizada,  // tizada o directamente un string con el svg, a decidir
    val name: String,
    val favorite: Boolean,  // tal vez para marcar con una estrellita una tizada
    var active: Boolean
)
