package com.example.app_script_api.model

//Data class Estadisticas para el endpoint GET /estadisticas
data class Estadisticas(
    val totalFacturas: Int = 0,
    val sumaTotal: Double = 0.0,
    val mediaFactura: Double = 0.0,
)
