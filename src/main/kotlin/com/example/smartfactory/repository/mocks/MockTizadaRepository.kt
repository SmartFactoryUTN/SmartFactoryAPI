package com.example.smartfactory.repository.mocks

import com.example.smartfactory.Domain.Inventario.BatchStage
import com.example.smartfactory.Domain.Molde.Molde
import com.example.smartfactory.Domain.Tizada.*
import com.example.smartfactory.repository.TizadaRepository
import org.springframework.cglib.core.Local
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*


@Repository
class MockTizadaRepository: TizadaRepository {
    final val parts = mutableListOf(
        Molde(UUID.randomUUID(), "Articulo03", "asdasd.com", "Articulo 03, lycra, color azul", 142.2, true, LocalDateTime.now(), null, null)
    )
    final val bin = TizadaContainer(UUID.randomUUID(), "Mesa de corte de la fábrica del viejo de Eze", 500, 400, (500*400).toDouble())
    final val results = mutableListOf(TizadaResult(UUID.randomUUID(), "asdasd.com", TizadaConfiguration(), bin, parts, 75, 561, false))
    val tizadas = mutableListOf(
        Tizada(UUID.randomUUID(), "Tizada de ropa interior", TizadaConfiguration(), parts, bin, results, BatchStage.AWAITING_TIZADA, TizadaState.IN_PROGRESS, true, LocalDateTime.now(), null, null),
        Tizada(UUID.randomUUID(), "Tizada de ropa interior con mangas", TizadaConfiguration(), parts, bin, results, BatchStage.AWAITING_TIZADA, TizadaState.IN_PROGRESS, true, LocalDateTime.now(), null, null),
        Tizada(UUID.randomUUID(), "Tizada de torso", TizadaConfiguration(), parts, bin, results, BatchStage.AWAITING_TIZADA, TizadaState.IN_PROGRESS, true, LocalDateTime.now(), null, null)
    )

    override fun getTizada(id: UUID): Tizada? {
        return tizadas.find { it.uuid == id }
    }

    override fun createTizada(t: Tizada) {
        tizadas.add(t)
    }

    override fun getAllTizadas(): Collection<Tizada> {
        return tizadas
    }

    override fun queueTizada(tizada: Tizada): Tizada {
        TODO("Not yet implemented")
    }
}
