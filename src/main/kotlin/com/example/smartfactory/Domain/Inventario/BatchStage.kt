package com.example.smartfactory.Domain.Inventario

enum class BatchStage {
    REQUESTED,
    AWAITING_FABRIC,
    AWAITING_TIZADA,
    IN_REVIEW,
    AWAITING_SEWING,
    SHIPPED,
    ERROR
}