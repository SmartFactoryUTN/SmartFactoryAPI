package com.example.smartfactory.Domain

data class GenericResponse<T>(
    val code: Int,
    val status: String,
    val data: T
)
