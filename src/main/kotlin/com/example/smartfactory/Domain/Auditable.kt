package com.example.smartfactory.Domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

abstract class Auditable {
    abstract var createdAt: LocalDateTime
    abstract var updatedAt: LocalDateTime?
    abstract var deletedAt: LocalDateTime?
}