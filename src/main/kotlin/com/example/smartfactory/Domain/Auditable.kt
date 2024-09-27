package com.example.smartfactory.Domain

import java.time.LocalDateTime

abstract class Auditable {
    abstract var createdAt: LocalDateTime
    abstract var updatedAt: LocalDateTime?
    abstract var deletedAt: LocalDateTime?
}
