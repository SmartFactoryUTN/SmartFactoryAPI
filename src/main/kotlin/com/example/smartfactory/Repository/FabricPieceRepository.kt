package com.example.smartfactory.Repository

import com.example.smartfactory.Domain.Inventory.FabricColor
import com.example.smartfactory.Domain.Inventory.FabricPiece
import com.example.smartfactory.Domain.Molde.Molde
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FabricPieceRepository: CrudRepository<FabricPiece, UUID> {
    fun getFabricPieceByColorAndMolde(fabricColor: FabricColor, molde: Molde): FabricPiece?
}