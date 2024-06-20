package com.example.smartfactory

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
    fun receiveFormData(@RequestBody formData: FormData): String {
        println("Received data: $formData")
        // Here you can handle the form data, e.g., save it to a database
        return "Data received successfully"
    }
}
