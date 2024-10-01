package com.example.smartfactory.Domain

import java.time.LocalDateTime

interface Auditable  {
    var createdAt: LocalDateTime
    var updatedAt: LocalDateTime?
    var deletedAt: LocalDateTime?
}
