package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
import com.example.smartfactory.application.Tizada.CM_TO_SVG_FACTOR_FORM
import com.example.smartfactory.application.Tizada.Request.Bin
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

@Suppress("all")
@Entity
@Table(name = "tizada_containers")
class TizadaContainer (
    @Id @Column(name = "tizada_container_id")
    val uuid: UUID,
    val name: String,
    @NotNull
    val height: Int,
    @NotNull
    val width: Int,
    val url: String,
    val area: Double = (height * width).toDouble(),
    override var createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime? = null,
    override var deletedAt: LocalDateTime? = null,
): Auditable {

    fun toBin(): Bin {
        return Bin(
            uuid = this.uuid.toString(),
            quantity = 1,
            height = this.height * CM_TO_SVG_FACTOR_FORM,
            width = this.width * CM_TO_SVG_FACTOR_FORM
        )
    }
}
