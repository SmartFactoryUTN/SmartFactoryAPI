package com.example.smartfactory.application.Tizada.Response

data class TizadaResponse<T>(
    val status: String,  // The status of the response: "success", "fail", or "error"
    val data: T? = null,  // The data to return (optional, depending on the type of response)
    val message: String? = null,  // Error or failure message (optional)
    val code: Int? = null  // Error code for error responses (optional)
)
