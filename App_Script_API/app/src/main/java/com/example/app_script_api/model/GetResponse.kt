package com.example.app_script_api.model

// Clase GetResponse para peticiones GET
data class GetResponse<T>(
    val status: String,
    val type: String? = null,
    val data: T? = null,
    val error: String? = null
)
