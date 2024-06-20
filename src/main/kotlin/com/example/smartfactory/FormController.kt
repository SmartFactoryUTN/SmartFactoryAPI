package com.example.smartfactory

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class FormData(
    val ancho: String,
    val largo: String,
    val tiempo: String,
    val porcentaje: String
)

@RestController
@RequestMapping("/api")
class FormController {

    @PostMapping("/data")
    fun receiveFormData(@RequestBody formData: FormData): ResponseEntity<Map<String, String>> {
        println("Received data: $formData")
        // Handle the received data, e.g., save it to a database
        val response = mapOf("message" to "Data received successfully")
        return ResponseEntity.ok(response)
    }
}