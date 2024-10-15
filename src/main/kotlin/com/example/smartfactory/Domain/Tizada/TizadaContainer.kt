package com.example.smartfactory.Domain.Tizada

import com.example.smartfactory.Domain.Auditable
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
    override var updatedAt: LocalDateTime?,
    override var deletedAt: LocalDateTime?,
): Auditable {
    companion object {
        const val DEFAULT_UUID = "60440d33-e9b5-4e71-9573-f8c00010acbd"
        const val DEFAULT_NAME = "Mesa default"
        const val DEFAULT_HEIGHT = 2000
        const val DEFAULT_WIDTH = 2000
        const val DEFAULT_URL = "https://elasticbeanstalk-sa-east-1-951718808729.s3.sa-east-1.amazonaws.com/60440d33-e9b5-4e71-9573-f8c00010acbd.svg"
        const val DEFAULT_AREA = DEFAULT_WIDTH * DEFAULT_HEIGHT

        fun getDefaultContainer(): TizadaContainer {
            return TizadaContainer(
                uuid = UUID.fromString(DEFAULT_UUID),
                name = DEFAULT_NAME,
                height = DEFAULT_HEIGHT,
                width = DEFAULT_WIDTH,
                url = DEFAULT_URL,
                area = DEFAULT_AREA.toDouble(),
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        }

    }

    fun toBin(): Bin {
        return Bin(uuid = this.uuid.toString(), quantity = 1)
    }
}
