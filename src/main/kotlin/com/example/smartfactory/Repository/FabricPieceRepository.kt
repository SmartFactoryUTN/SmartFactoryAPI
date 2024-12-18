package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.FabricPiece
import com.example.smartfactory.Domain.Inventory.FabricRoll
import com.example.smartfactory.Domain.Molde.Molde
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FabricPieceRepository: CrudRepository<FabricPiece, UUID> {
    fun getFabricPieceByFabricRollAndMolde(fabricColor: FabricRoll, molde: Molde): FabricPiece?

    fun getFabricPieceByFabricPieceId(fabricPieceId: UUID): FabricPiece?

    fun findAllByUserUuidAndActive(userUuid: UUID, active: Boolean = true): List<FabricPiece>
}
