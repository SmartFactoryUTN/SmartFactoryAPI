package com.example.smartfactory.Exceptions.handler

import com.example.smartfactory.Exceptions.UploadMoldeException
import com.example.smartfactory.application.Tizada.Response.TizadaResponse
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.factory.parsing.Problem
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.NativeWebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    // Handle validation errors for @Valid annotated request bodies
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<TizadaResponse<Any>> {
        val errors = ex.bindingResult.fieldErrors.map { it.field to it.defaultMessage }.toMap()

        return ResponseEntity.badRequest().body(
            TizadaResponse(
                status = "fail",
                data = errors
            )
        )
    }

    // Optionally, handle other exceptions like ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<TizadaResponse<Any>> {
        val errors = ex.constraintViolations.associate { it.propertyPath.toString() to it.message }

        return ResponseEntity.badRequest().body(
            TizadaResponse(
                status = "fail",
                data = errors
            )
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<TizadaResponse<Any>> {
        return ResponseEntity.badRequest().body(
            TizadaResponse(
                status = "fail",
                data = mapOf("message" to ex.localizedMessage)
            )
        )
    }

    @ExceptionHandler(UploadMoldeException::class)
    fun handleUploadMoldeException(ex: UploadMoldeException): ResponseEntity<TizadaResponse<Any>> {
        return ResponseEntity.badRequest().body(
            TizadaResponse(
                status = "fail",
                data = mapOf("message" to ex.localizedMessage)
            )
        )
    }
}
