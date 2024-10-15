package com.example.smartfactory.application.Tizada.Response

import com.fasterxml.jackson.annotation.JsonInclude

data class TizadaResponse<T>(
    val status: String,  // The status of the response: "success", "fail", or "error"
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null,  // The data to return (optional, depending on the type of response)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,  // Error or failure message (optional)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val code: Int? = null  // Error code for error responses (optional)
)
