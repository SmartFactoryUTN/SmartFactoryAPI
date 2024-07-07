package com.example.smartfactory.Domain.Tizada

data class GenericResponse<T>(
    val code: Int,
    val status: String,
    val data: T
)
