package com.example.app_script_api.model

//Clase PostRespone para peticiones Post
data class PostResponse(
    val status: String,
    val message: String? = null,
    val error: String? = null
)
