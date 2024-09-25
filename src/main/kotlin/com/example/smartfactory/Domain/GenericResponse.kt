package com.example.smartfactory.Domain

data class GenericResponse<T>(
    val status: String,
    val data: T
)
